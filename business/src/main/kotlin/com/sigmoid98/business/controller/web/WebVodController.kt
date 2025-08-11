package com.sigmoid98.business.controller.web

import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import com.sigmoid98.business.properties.DemoProperties
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
    @Resource private val demoProperties: DemoProperties,
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

    /**
     * 上传实例音频, 如果上传过, 直接取上传过的示例音频, 并计算出收费金额, 时长等
     */
    @GetMapping("/upload-demo")
    fun uploadDemo(): CommonResp<DemoProperties> {
        val title = "${demoProperties.key}-${demoProperties.name}"
        val searchMediaResponse = vodService.searchByTitle(title)

        // 1. 使用 if-else 表达式，避免声明可变的 vid 变量
        val vid = if (searchMediaResponse.total > 0) {
            logger.info { "该文件已上传过, title=${title}" }
            // 2. 使用 Kotlin 的属性访问和集合函数
            searchMediaResponse.mediaList.first().mediaId
        } else {
            // 注释保留，因为它们包含重要的上下文信息
            // File file = ResourceUtils.getFile("classpath:" + demoProperties.getName());
            // UploadVideoResponse videoResponse = VodUtil.uploadLocalFile(title, file.getAbsolutePath());
            // 上面两行只在本地起作用，生产打包后，demo.wav会被打进jar包里，导致file.getAbsolutePath()报错
            // 所以修改demo.name配置为全路径，如：demo.name=/Users/temp/nls/demo.wav，并手动放入demo.wav文件，生产也需要手动放入demo.wav文件
            val videoResponse = vodService.uploadLocalFile(title, demoProperties.name)

            // 需要延迟2秒，才能拿到刚上传的音频的时长，否则金额计算出来是0
            // 注意：在响应式编程或协程中，应使用 delay() 而非 Thread.sleep() 来避免阻塞线程。
            // 在传统 Spring MVC 中，Thread.sleep() 是可行的，但仍需谨慎。
            Thread.sleep(2000)

            videoResponse.videoId
        }

        // 获取音频地址
        val mezzanineInfo = vodService.getMezzanineInfo(vid)
        // 使用更安全、更简洁的字符串处理函数
        val rawFileUrl = mezzanineInfo.mezzanine.fileURL.substringBefore('?')

        // 使用作用域函数 apply 来配置 demoProperties 对象，使代码更紧凑和链式
        val updatedProperties = DemoProperties(
            name = demoProperties.name,
            audio = rawFileUrl,
            key = demoProperties.key,
            amount = vodService.calcAmount(vid),
            lang = demoProperties.lang,
            vid = vid,
        )

        // 5. 构造函数调用更简洁
        return CommonResp(updatedProperties)
    }

}