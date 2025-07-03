package com.sigmoid98.business.service

import cn.hutool.core.util.IdUtil
import com.sigmoid98.business.context.LoginMemberContext
import com.sigmoid98.business.domain.OrderInfo
import com.sigmoid98.business.enums.OrderInfoStatusEnum
import com.sigmoid98.business.mapper.OrderInfoMapper
import com.sigmoid98.business.req.OrderInfoPayReq
import com.sigmoid98.business.resp.OrderInfoPayResp
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlin.random.nextInt

@Service
class OrderInfoService(
    @Resource private val orderInfoMapper: OrderInfoMapper,
    @Resource private val loginMemberContext: LoginMemberContext,
) {

    companion object {
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
    }

    fun genOrderNo(): String =
        "${LocalDateTime.now().format(ORDER_NO_FORMATTER)}${Random.nextInt(100, 1000)}"
}