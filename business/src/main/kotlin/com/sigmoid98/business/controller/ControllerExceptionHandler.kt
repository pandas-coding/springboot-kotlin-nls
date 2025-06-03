package com.sigmoid98.business.controller

import com.sigmoid98.business.resp.CommonResp
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

private val logger = KotlinLogging.logger {}

/**
 * 统一异常处理、数据预处理等
 */
@ControllerAdvice
class ControllerExceptionHandler {

    /**
     * 所有异常统一处理
     */
    @ExceptionHandler(value = [Exception::class])
    @ResponseBody
    fun exceptionHandler(ex: Exception): CommonResp<Any?> {
        val commonAnyResp = CommonResp<Any?>(
            content = "系统出现异常, 请联系管理员",
            success = false,
        )
        logger.error(ex) { "系统异常:\n" }

        return commonAnyResp
    }
}