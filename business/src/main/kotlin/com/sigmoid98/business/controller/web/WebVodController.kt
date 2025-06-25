package com.sigmoid98.business.controller.web

import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import com.sigmoid98.business.req.GetUploadAuthReq
import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.resp.GetRepeatFileUploadAuthResp
import com.sigmoid98.business.resp.GetUploadAuthResp
import com.sigmoid98.business.util.VodUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/web/vod")
class WebVodController(
    @Resource private val vodService: VodUtil,
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/get-upload-auth")
    fun getUploadAuth(@Valid @RequestBody req: GetUploadAuthReq): CommonResp<Any> {
        logger.info { "获取上传凭证开始..." }

        val title = "${req.key}-${req.name}"
        val searchMediaResp = vodService.searchByTitle(title)
        if (searchMediaResp.total > 0) {
            logger.info { "该文件已上传过, 文件title: $title" }
            val media = searchMediaResp.mediaList.first()
            val vid = media.mediaId
            val getMezzanineInfoResp = vodService.getMezzanineInfo(vid)
            val fileUrl = getMezzanineInfoResp.mezzanine.fileURL
            val repeatFileUrl = fileUrl.split("\\?").first()

            logger.info { "重复文件 fileUrl: ${repeatFileUrl}, videoId: $vid" }
            val repeatFileUploadAuthResp = GetRepeatFileUploadAuthResp(
                fileUrl = repeatFileUrl,
                videoId = vid,
            )
            return CommonResp(content = repeatFileUploadAuthResp)
        }

        val client = runCatching {
            vodService.initVodClient()
        }.getOrElse { exception ->
            logger.error(exception) { "获取vod Client失败" }
            throw BusinessException(BusinessExceptionEnum.UPLOAD_VIDEO_ERROR)
        }

        val authResp = runCatching {
            val videoResponse = vodService.createUploadVideo(client, title)
            GetUploadAuthResp(
                uploadAuth = videoResponse.uploadAuth,
                uploadAddress = videoResponse.uploadAddress,
                videoId = videoResponse.videoId,
            )
        }.onSuccess { resp ->
            logger.info {
                """
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

    /**
     * 计算语音转字幕金额
     */
    @GetMapping("/calc-amount/{videoId}")
    fun calcAmount(@PathVariable videoId: String): CommonResp<BigDecimal> {
        val amount = vodService.calcAmount(videoId)
        return CommonResp(content = amount)
    }

}