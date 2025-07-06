package com.sigmoid98.business.service

import com.sigmoid98.business.enums.OrderInfoOrderTypeEnum
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AfterPayService(
    @Resource private val orderInfoService: OrderInfoService,
    @Resource private val fileTransferService: FileTransferService,
) {

    companion object {
        private val logger = KotlinLogging.logger {  }
    }

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

        if (orderInfo.orderType == OrderInfoOrderTypeEnum.FILE_TRANSFER_PAY.code) {
            logger.info { "语音识别单次付费, 更新语音识别表状态" }
            if (orderInfo.info.isNullOrEmpty()) {
                logger.info { "语音识别orderInfo异常, orderInfo: $orderInfo" }
                return
            }
            val fileTransferId = orderInfo.info!!.toLong()
            fileTransferService
        }

    }
}