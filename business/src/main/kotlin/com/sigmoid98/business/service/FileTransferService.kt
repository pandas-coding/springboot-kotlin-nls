package com.sigmoid98.business.service

import cn.hutool.core.util.IdUtil
import com.sigmoid98.business.context.LoginMemberContext
import com.sigmoid98.business.domain.FileTransfer
import com.sigmoid98.business.enums.FileTransferPayStatusEnum
import com.sigmoid98.business.enums.FileTransferStatusEnum
import com.sigmoid98.business.enums.OrderInfoOrderTypeEnum
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import com.sigmoid98.business.mapper.FileTransferMapper
import com.sigmoid98.business.req.FileTransferPayReq
import com.sigmoid98.business.req.OrderInfoPayReq
import com.sigmoid98.business.resp.OrderInfoPayResp
import com.sigmoid98.business.util.VodUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class FileTransferService(
    @Resource private val fileTransferMapper: FileTransferMapper,
    @Resource private val vodUtil: VodUtil,
    @Resource private val loginMemberContext: LoginMemberContext,
) {

    @Autowired
    private lateinit var orderInfoService: OrderInfoService

    companion object {
        private val logger = KotlinLogging.logger {} // Logger
    }

    fun pay(req: FileTransferPayReq): OrderInfoPayResp {
        if (req.amount == null) {
            throw Exception("pay amount cannot be null")
        }

        val now = LocalDateTime.now()

        // get video info
        val videoInfo = vodUtil.getVideoInfo(req.vod)
        val duration = videoInfo.video.duration
        logger.info { "计算视频费用, 视频: ${req.vod}, 时长: $duration" }
        val seconds = duration.toInt()
        val toSaveId = IdUtil.getSnowflakeNextId()

        val fileTransfer = FileTransfer().apply {
            id = toSaveId
            memberId = loginMemberContext.id
            name = req.name
            second = seconds
            amount = req.amount
            audio = req.audio
            fileSign = req.fileSign
            payStatus = FileTransferPayStatusEnum.I.code
            status = FileTransferStatusEnum.INIT.code
            lang = req.lang
            vod = req.vod
            createdAt = now
            updatedAt = now
        }
        fileTransferMapper.insert(fileTransfer)

        // 保存订单信息
        // 订单表的info关联的是file_transfer的id
        val orderInfoPayReq = OrderInfoPayReq(
            orderType = OrderInfoOrderTypeEnum.FILE_TRANSFER_PAY.code,
            info = toSaveId.toString(),
            amount = req.amount,
            channel = req.channel!!,
            desc = "语音识别付费",
        )
        return orderInfoService.pay(orderInfoPayReq)
    }

}