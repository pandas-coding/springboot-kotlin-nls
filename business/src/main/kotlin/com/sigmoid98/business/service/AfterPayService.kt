package com.sigmoid98.business.service

import com.sigmoid98.business.enums.OrderInfoOrderTypeEnum
import com.sigmoid98.business.enums.OrderInfoStatusEnum
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AfterPayService(
    @Resource private val orderInfoService: OrderInfoService,
    @Resource private val fileTransferService: FileTransferService,
) {

    companion object {
        private val logger = KotlinLogging.logger {  }
    }

    /**
     * 新增方法：协调和处理订单支付状态
     * 这个方法可以被定时任务调用，或者被Controller层的一个“查询订单状态”接口调用
     */
    @Transactional
    fun reconcilePaymentStatus(orderNo: String): String {
        val orderInfo = orderInfoService.selectByOrderNo(orderNo)
            ?: throw Exception("订单不存在") // 建议抛出异常

        // 如果订单已经是成功状态，直接返回
        if (orderInfo.status == OrderInfoStatusEnum.S.code) {
            return OrderInfoStatusEnum.S.code
        }
        when (orderInfo.status) {
            // 如果订单已经是成功状态，直接返回
            OrderInfoStatusEnum.S.code -> return OrderInfoStatusEnum.S.code
            null -> throw Exception("订单状态异常")
        }

        // 主动去支付宝查询状态
        val (status, payTime) = orderInfoService.queryAlipayOrderStatus(orderNo) // 调用重构后的方法

        return when (Pair(status, payTime != null)) {
            // 如果查询到支付成功，则调用支付成功处理逻辑
            Pair(OrderInfoStatusEnum.S.code, true) -> {
                logger.info { "支付宝状态查询成功，订单号: $orderNo, 支付时间: $payTime" }
                this.afterPaySuccess(orderNo, payTime!!) // 或者直接使用 payTime，因为上面已经判断
                OrderInfoStatusEnum.S.code
            }
            // 返回当前数据库中的状态
            else -> orderInfo.status!!
        }
    }

    @Transactional
    fun afterPaySuccess(orderNo: String, channelTime: LocalDateTime) {
        logger.info { "执行支付成功操作开始" }

        // 校验订单是否存在
        val orderInfo = orderInfoService.selectByOrderNo(orderNo)
        if (null == orderInfo) {
            logger.error { "订单不存在: $orderNo" }
            return
        }

        // 更新订单状态为S
        logger.info { "更新订单信息开始" }
        val updateSuccess = orderInfoService.afterPaySuccess(orderNo = orderNo, channelTime = channelTime)
        if (!updateSuccess) {
            logger.error { "订单状态异常, 订单状态非初始状态, $orderNo, 操作结束" }
            return
        }
        logger.info { "更新订单信息成功: orderNo: $orderNo, 更新订单状态为S" }

        if (orderInfo.orderType == OrderInfoOrderTypeEnum.FILE_TRANSFER_PAY.code) {
            logger.info { "语音识别单次付费, 更新语音识别表状态" }
            if (orderInfo.info.isNullOrEmpty()) {
                logger.info { "语音识别orderInfo异常, orderInfo: $orderInfo" }
                return
            }
            val fileTransferId = orderInfo.info!!.toLong()
            fileTransferService.afterPaySuccess(fileTransferId)
        }

        logger.info { "执行支付成功操作结束" }
    }
}