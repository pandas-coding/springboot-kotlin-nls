package com.sigmoid98.business.controller.web

import com.google.code.kaptcha.impl.DefaultKaptcha
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

@RestController
@RequestMapping("/web/kaptcha")
class WebKaptchaController(
    @Resource(name = "defaultKaptcha") private val defaultKaptcha: DefaultKaptcha,
    @Resource private val stringRedisTemplate: StringRedisTemplate,
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @GetMapping("/image-code/{imageCodeToken}")
    fun imageCode(
        @PathVariable(value = "imageCodeToken") imageCodeToken: String,
        response: HttpServletResponse,
    ) {
        runCatching {
            // 生成验证码字符串
            val createText = defaultKaptcha.createText()
            // 将生成的验证码放入redis缓存中，有效期300秒
            stringRedisTemplate.opsForValue().set(imageCodeToken, createText, 300, TimeUnit.SECONDS)
            // 使用验证码字符串生成验证码图片
            val challenge = defaultKaptcha.createImage(createText)

            ByteArrayOutputStream().use { it ->
                ImageIO.write(challenge, "jpg", it)
                it.toByteArray()
            }
        }.onSuccess { captchaBytes ->
            // 4. 使用 `apply` 作用域函数来配置 response 对象，更具可读性
            response.apply {
                setHeader("Cache-Control", "no-store")
                setHeader("Pragma", "no-cache")
                setDateHeader("Expires", 0)
                contentType = "image/jpeg"
            }

            // 再次使用 `use` 确保输出流被正确关闭
            response.outputStream.use { it.write(captchaBytes) }
        }.onFailure { ex ->
            when (ex) {
                is IllegalArgumentException -> {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND)
                }
                else -> {
                    logger.error(ex) { "Kaptcha generation failed for token: $imageCodeToken" }
                    // 向上抛出，让全局异常处理器统一处理为 500 Internal Server Error
                    throw ex
                }
            }
        }
    }

}