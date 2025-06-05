package com.sigmoid98.business.controller

import com.sigmoid98.business.resp.CommonResp
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.View

private val logger = KotlinLogging.logger {}

/**
 * 统一异常处理、数据预处理等
 */
@ControllerAdvice
class ControllerExceptionHandler(private val error: View) {

    /**
     * 所有异常统一处理
     */
    @ExceptionHandler(value = [Exception::class])
    @ResponseBody
    fun exceptionHandler(ex: Exception): CommonResp<Any?> {
        val commonAnyResp = CommonResp<Any?>(
            success = false,
            message = "系统出现异常, 请联系管理员",
            errorMessage = ex.message,
        )
        logger.error(ex) { "系统异常:\n" }

        return commonAnyResp
    }


    /**
     * 校验异常统一处理
     */
    @ExceptionHandler(value = [BindException::class])
    @ResponseBody
    fun exceptionHandler(ex: BindException): CommonResp<Any?> {

        val validationErrorMessage = ex.bindingResult.allErrors.first().defaultMessage

        val commonResp = CommonResp<Any?>(
            success = false,
            message = validationErrorMessage,
            errorMessage = ex.message,
        )
        logger.error(ex) { "参数校验异常: $validationErrorMessage" }
        return commonResp
    }
}