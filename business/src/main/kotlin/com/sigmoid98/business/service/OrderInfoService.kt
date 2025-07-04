package com.sigmoid98.business.service

import cn.hutool.core.util.IdUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper
import com.sigmoid98.business.alipay.AlipayService
import com.sigmoid98.business.context.LoginMemberContext
import com.sigmoid98.business.domain.OrderInfo
import com.sigmoid98.business.enums.OrderInfoChannelEnum
import com.sigmoid98.business.enums.OrderInfoStatusEnum
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import com.sigmoid98.business.mapper.OrderInfoMapper
import com.sigmoid98.business.req.OrderInfoPayReq
import com.sigmoid98.business.resp.OrderInfoPayResp
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Service
class OrderInfoService(
    @Resource private val orderInfoMapper: OrderInfoMapper,
    @Resource private val loginMemberContext: LoginMemberContext,
    @Resource private val alipayService: AlipayService,
    @Resource private val afterPayService: AfterPayService,
) {

    companion object {
        private val logger = KotlinLogging.logger {  }
        private val ORDER_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
    }

    fun pay(req: OrderInfoPayReq): OrderInfoPayResp {
        val newOrderNo = genOrderNo()
        val now = LocalDateTime.now()

        val orderInfo = OrderInfo().apply {
            id = IdUtil.getSnowflakeNextId()
            orderNo = newOrderNo
            orderAt = now
            orderType = req.orderType
            info = req.info
            memberId = loginMemberContext.id
            amount = req.amount
            payAt = now
            channel = req.channel
            channelAt = null
            status = OrderInfoStatusEnum.I.code
            desc = req.desc
            createdAt = now
            updatedAt = now
        }
        orderInfoMapper.insert(orderInfo)

        // 请求支付宝接口
        if (req.channel != OrderInfoChannelEnum.ALIPAY.code) {
            logger.warn { "支付渠道[${req.channel}]不存在" }
            throw BusinessException(BusinessExceptionEnum.PAY_ERROR)
        }
        // 调用支付宝下单接口
        val response = alipayService.pay(
            subject = req.desc,
            outTradeNo = newOrderNo,
            totalAmount = req.amount.toPlainString(),
        )
        return OrderInfoPayResp(
            orderNo = newOrderNo,
            channelResult = response.body,
        )
    }

    fun genOrderNo(): String =
        "${LocalDateTime.now().format(ORDER_NO_FORMATTER)}${Random.nextInt(100, 1000)}"

    /**
     * 查询订单状态
     */
    fun queryOrderStatus(orderNo: String): String? {
        val orderInfo = selectByOrderNo(orderNo = orderNo)
        if (orderInfo == null) {
            return null
        }

        // 全链路查询
        if (orderInfo.status != OrderInfoStatusEnum.I.code) {
            return orderInfo.status
        }
        if (orderInfo.channel != OrderInfoChannelEnum.ALIPAY.code) {
            return orderInfo.status
        }
        val alipayResponse = alipayService.query(orderNo)
        val tradeStatus = alipayResponse.tradeStatus
        if (tradeStatus != "TRADE_SUCCESS" && tradeStatus != "TRADE_FINISHED") {
            return orderInfo.status
        }

        when (tradeStatus) {
            "TRADE_SUCCESS", "TRADE_FINISHED" -> {
                val sendPayDate = alipayResponse.sendPayDate
                val payDateTime = LocalDateTime.parse(sendPayDate, ORDER_NO_FORMATTER)
                afterPayService.afterPaySuccess(orderNo, payDateTime)
                return OrderInfoStatusEnum.S.code
            }
            else -> return orderInfo.status
        }
    }

    fun selectByOrderNo(orderNo: String): OrderInfo? {
        val list = KtQueryChainWrapper(orderInfoMapper, OrderInfo())
            .eq(OrderInfo::orderNo, orderNo)
            .list()
        return list.firstOrNull()
    }

    fun afterPaySuccess(orderNo: String, channelTime: LocalDateTime): Boolean {
        return KtUpdateChainWrapper(orderInfoMapper, OrderInfo())
            .set(OrderInfo::status, OrderInfoStatusEnum.S.code)
            .set(OrderInfo::channelAt, channelTime)
            .set(OrderInfo::updatedAt, LocalDateTime.now())
            .eq(OrderInfo::orderNo, orderNo)
            .eq(OrderInfo::status, OrderInfoStatusEnum.I.code)
            .update()
    }
}