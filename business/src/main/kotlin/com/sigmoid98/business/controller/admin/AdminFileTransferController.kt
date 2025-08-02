package com.sigmoid98.business.controller.admin

import com.sigmoid98.business.req.FileTransferQueryReq
import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.resp.FileTransferQueryResp
import com.sigmoid98.business.resp.PageResp
import com.sigmoid98.business.service.FileTransferService
import jakarta.annotation.Resource
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/file-transfer")
class AdminFileTransferController(
    @Resource private val fileTransferService: FileTransferService,
) {

    @GetMapping("/query")
    fun query(@Valid req: FileTransferQueryReq): CommonResp<PageResp<FileTransferQueryResp>> {
        val respList = fileTransferService.query(req)
        return CommonResp(content = respList)
    }

}