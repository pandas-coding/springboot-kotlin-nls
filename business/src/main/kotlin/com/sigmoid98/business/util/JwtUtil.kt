package com.sigmoid98.business.util

import cn.hutool.core.date.DateField
import cn.hutool.core.date.DateTime
import cn.hutool.crypto.GlobalBouncyCastleProvider
import cn.hutool.jwt.JWTPayload
import cn.hutool.jwt.JWTUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class JwtUtil {

    /**
     * jwt salt key
     */
    private val jwtSaltKey = "springboot-nls"

    companion object {
        private val logger = KotlinLogging.logger {} // Logger for SmsCodeService
    }

    fun createLoginToken(map: Map<String, Any>): String {
        logger.info { "准备生成登录token, 原始map: $map" }
        return createToken(map, 24 * 60)
    }

    fun createToken(map: Map<String, Any>, expireMinutes: Int): String {
        logger.info { "开始生成JWT token, map: $map" }

        GlobalBouncyCastleProvider.setUseBouncyCastle(false)
        val now = DateTime.now()
        val expireTime = now.offsetNew(DateField.MINUTE, expireMinutes)
        val payload = buildMap<String, Any> {
            put(JWTPayload.ISSUED_AT, now)
            put(JWTPayload.EXPIRES_AT, expireTime)
            put(JWTPayload.NOT_BEFORE, now)
            putAll(map)
        }

        return JWTUtil.createToken(payload, jwtSaltKey.toByteArray()).also { token ->
            logger.info { "生成成功JWT token: $token" }
        }

    }
}