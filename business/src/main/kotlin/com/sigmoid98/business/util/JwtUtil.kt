package com.sigmoid98.business.util

import cn.hutool.core.date.DateField
import cn.hutool.core.date.DateTime
import cn.hutool.crypto.GlobalBouncyCastleProvider
import cn.hutool.jwt.JWTPayload
import cn.hutool.jwt.JWTUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey
import kotlin.time.Duration.Companion.minutes

@Component
class JwtUtil(
    @Value("\${jwt.secret}")
    val secret: String,
    @Value("\${jwt.expiration-ms}")
    val expiration: Long,
) {

    private val signingKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * jwt salt key
     */
    private val jwtSaltKey = "springboot-nls"

    companion object {
        private val logger = KotlinLogging.logger {} // Logger for SmsCodeService

        val STANDARD_CLAIMS = setOf(Claims.ISSUED_AT, Claims.EXPIRATION, Claims.NOT_BEFORE)
    }

    /**
     *
     */
    @Deprecated("deprecated for new jwt provider")
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

    fun createLoginToken(vararg claims: Pair<String, Any>): String {
        val payload = claims.toMap()
        logger.info { "准备生成登录token, payload: $payload" }
        // 将传入的 Pair 数组直接转换为 Map
        return createToken(payload = payload)
    }

    /**
     * 创建 JWT Token 的核心方法。
     * @param payload 要添加到token中的自定义数据。
     * @param validityInMinutes token的有效期（分钟），默认为 24 * 60 (24小时)。
     * @return 生成的 JWT 字符串。
     */
    fun createToken(
        payload: Map<String, Any>,
        validityInMinutes: Long = 24 * 60,
    ): String {
        logger.info { "开始生成JWT token, payload：$payload，有效期：$validityInMinutes 分钟"}

        val now = Instant.now()
        val expirationTime = now.plusSeconds(validityInMinutes.minutes.inWholeSeconds)

        return Jwts.builder()
            .claims()
            .add(payload)
            .issuedAt(Date.from(now))
            .notBefore(Date.from(now))
            .expiration(Date.from(expirationTime))
            .and()
            .signWith(signingKey)
            .compact()
            .also {
                logger.info { "生成JWT token成功：$this" }
            }
    }

    fun validate(token: String): Boolean {
        logger.info { "开始JWT token校验, token: $token" }

        return runCatching {
            Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token)
            true
        }.getOrElse { ex ->
            when (ex) {
                is SecurityException, is MalformedJwtException, is UnsupportedJwtException, is IllegalArgumentException ->
                    logger.error(ex) { "JWT token 校验失败：无效的Token格式或签名" }
                is ExpiredJwtException ->
                    logger.warn(ex) { "JWT token 校验失败：Token已过期" }
                else ->
                    logger.error(ex) { "JWT token 校验发生未知异常" }
            }
            false
        }
    }

    fun getPayload(token: String): Map<String, Any>? {
        return runCatching {
            val claims: Jws<Claims> = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)

            val customPayload = claims.payload.filterKeys { it !in STANDARD_CLAIMS }
            logger.info { "根据token获取原始内容: $customPayload" }
            customPayload
        }.getOrNull()
    }
}