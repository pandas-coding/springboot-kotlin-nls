package com.sigmoid98.business.service

import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class KaptchaService(
    @Resource val stringRedisTemplate: StringRedisTemplate,
) {

    companion object {
        private val logger = KotlinLogging.logger {} // Logger for SmsCodeService
    }

    /**
     * 校验图片验证码
     * 根据验证码token去获取缓存中的验证码，和用户输入的验证码是否一致
     */
    fun validCode(code: String, token: String) {
        val imageCode = stringRedisTemplate.opsForValue().get(token)
        logger.info { "从redis中获取到的验证码: $imageCode" }

        if (imageCode.isNullOrEmpty()) {
            logger.warn { "验证码校验失败，验证码已过期" }
            throw BusinessException(BusinessExceptionEnum.IMAGE_CODE_ERROR)
        }

        if (imageCode.lowercase() != code.lowercase()) {
            logger.warn { "验证码校验失败，验证码不正确" }
            throw BusinessException(BusinessExceptionEnum.IMAGE_CODE_ERROR)
        }

        stringRedisTemplate.delete(token)
    }
}