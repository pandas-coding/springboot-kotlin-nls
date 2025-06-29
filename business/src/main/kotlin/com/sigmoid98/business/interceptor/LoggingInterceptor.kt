package com.sigmoid98.business.interceptor

import cn.hutool.core.util.IdUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * 用于为日志添加日志跟踪号的interceptor
 */
@Component
class LoggingInterceptor: HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        MDC.put("LOG_ID", IdUtil.getSnowflakeNextIdStr())
        return true
    }
}