package com.sigmoid98.business.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import com.sigmoid98.business.context.LoginMemberContext
import com.sigmoid98.business.resp.MemberLoginResp
import com.sigmoid98.business.util.JwtProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

@Component
class WebLoginInterceptor(
    @Resource private val jwtProvider: JwtProvider,
    @Resource private val loginMemberContext: LoginMemberContext,
    private val objectMapper: ObjectMapper,
) : HandlerInterceptor {

    companion object {
        private val logger = KotlinLogging.logger {  }
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // OPTIONS请求不做校验,
        // 前后端分离的架构, 前端会发一个OPTIONS请求先做预检, 对预检请求不做校验
        if (request.method.equals(HttpMethod.OPTIONS.name(), ignoreCase = true)) {
            return true
        }

        val path = request.requestURL.toString()
        logger.info { "接口登录拦截, path: $path" }

        // 1. 获取并验证 token
        val token = request.getHeader("Token")
        logger.info { "网站登录验证开始, token: $token" }

        if (token.isNullOrEmpty()) {
            logger.info { "token为空, 请求被拦截" }
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }
        if (!jwtProvider.validate(token)) {
            logger.info { "token校验不通过, 请求被拦截, Token: $token" }
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }

        // 2. 从有效的 token 中获取 payload
        // 使用 runCatching 优雅处理潜在的解析异常
        // JwtUtil.getPayload 返回 Map<String, Any>?
        val payload = jwtProvider.getPayload(token) ?: run {
            // 理论上 validate() 通过后这里不应为 null，但作为安全兜底
            logger.error { "Token 校验通过但无法解析 Payload，请求被拦截. Token: $token" }
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }

        logger.info { "当前登录会员信息 (来自JWT): $payload" }
        return runCatching {
            val member = objectMapper.convertValue(payload, MemberLoginResp::class.java)

            val memberWithToken = member.copy(token = token)
            loginMemberContext.member = memberWithToken

            true
        }.getOrElse { ex ->
            // 如果 Map 无法转换为 MemberLoginResp (如字段不匹配)，则捕获异常
            logger.error(ex) { "从 JWT Payload 转换用户信息失败, 请求被拦截." }
            response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            false
        }
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        // 在请求处理完毕后，清理 ThreadLocal，防止内存泄漏
        loginMemberContext.removeMember()
    }
}