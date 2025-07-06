package com.sigmoid98.business.controller.web

import com.sigmoid98.business.req.FileTransferPayReq
import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.resp.OrderInfoPayResp
import com.sigmoid98.business.service.FileTransferService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/file-transfer")
class WebFileTransferController(
    @Resource private val fileTransferService: FileTransferService,
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/pay")
    fun pay(@Valid @RequestBody req: FileTransferPayReq): CommonResp<OrderInfoPayResp> {
        logger.info { "语音识别支付开始" }
        val orderInfoPayResp = fileTransferService.pay(req)
        logger.info { "语音识别支付结束" }
        return CommonResp(content = orderInfoPayResp)
    }

}