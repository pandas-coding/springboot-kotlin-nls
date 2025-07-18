package com.sigmoid98.business.controller.web

import com.sigmoid98.business.req.FileTransferSubtitleQueryReq
import com.sigmoid98.business.req.GenSubtitleReq
import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.resp.FileTransferSubtitleQueryResp
import com.sigmoid98.business.resp.PageResp
import com.sigmoid98.business.service.FileTransferSubtitleService
import jakarta.annotation.Resource
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/file-transfer-subtitle")
class WebFileTransferSubtitleController(
    @Resource private val fileTransferSubtitleService: FileTransferSubtitleService,
) {

    /**
     * 查询字幕信息
     */
    @GetMapping("/query")
    fun query(@Valid req: FileTransferSubtitleQueryReq): CommonResp<PageResp<FileTransferSubtitleQueryResp>> {
        val respList = fileTransferSubtitleService.query(req)
        return CommonResp(content = respList)
    }

    /**
     * 生成字幕并返回文件链接
     */
    @GetMapping("/gen-subtitle")
    fun genSubtitle(@Valid req: GenSubtitleReq): CommonResp<String> {
        val url = fileTransferSubtitleService.genSubtitle(req)
        return CommonResp(content = url)
    }
}