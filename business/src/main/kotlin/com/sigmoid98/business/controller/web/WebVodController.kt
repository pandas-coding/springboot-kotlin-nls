package com.sigmoid98.business.controller.web

import com.alibaba.fastjson.JSONObject
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import com.sigmoid98.business.req.GetUploadAuthReq
import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.resp.GetUploadAuthResp
import com.sigmoid98.business.util.VodUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/vod")
class WebVodController(
    @Resource private val vodUtil: VodUtil,
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/get-upload-auth")
    fun getUploadAuth(@Valid @RequestBody req: GetUploadAuthReq): CommonResp<Any> {
        logger.info { "获取上传凭证开始..." }

        val title = "${req.key}-${req.name}"
        val searchMediaResp = vodUtil.searchByTitle(title)
        if (searchMediaResp.total > 0) {
            logger.info { "该文件已上传过, 文件title: $title" }
            val media = searchMediaResp.mediaList.first()
            val vid = media.mediaId
            val getMezzanineInfoResp = vodUtil.getMezzanineInfo(vid)
            val fileUrl = getMezzanineInfoResp.mezzanine.fileURL
            val respFileUrl = fileUrl.split("\\?").first()
            val respJsonObject = JSONObject().apply {
                put("fileUrl", respFileUrl)
                put("videoId", vid)
            }
            logger.info { "重复文件 fileUrl: ${respFileUrl}, videoId: $vid" }
            return CommonResp(content = respJsonObject)
        }

        val client = runCatching {
            vodUtil.initVodClient()
        }.getOrElse {
            exception ->
            logger.error(exception) { "获取vod Client失败" }
            throw BusinessException(BusinessExceptionEnum.UPLOAD_VIDEO_ERROR)
        }

        val authResp = runCatching {
            val videoResponse = vodUtil.createUploadVideo(client, title)
            GetUploadAuthResp(
                uploadAuth = videoResponse.uploadAuth,
                uploadAddress = videoResponse.uploadAddress,
                videoId = videoResponse.videoId,
            )
        }
            .onSuccess { resp ->
                logger.info { """
                    上传文件成功:
                    授权码 = ${resp.uploadAuth}
                    地址 = ${resp.uploadAddress}
                    videoId = ${resp.videoId}
                """.trimIndent()
                }
            }.getOrElse { ex ->
                logger.error(ex) { "获取上传凭证错误" }
                throw BusinessException(BusinessExceptionEnum.UPLOAD_VIDEO_ERROR)
            }
        logger.info { "获取上传凭证结束" }
        return CommonResp(content = authResp)
    }
}