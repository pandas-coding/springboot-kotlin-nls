package com.sigmoid98.business.aspect

import cn.hutool.core.util.IdUtil
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.support.spring.PropertyPreFilters
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger {}

@Aspect
@Component
class LogAspect {

    @Pointcut("execution(public * com.sigmoid98..*Controller.*(..))")
    fun pointCut() {}

    @Before("pointCut()")
    fun doBefore(joinPoint: JoinPoint) {
        logger.info { "前置通知" }
    }

    @Around("pointCut()")
    fun doAround(proceedingJoinPoint: ProceedingJoinPoint): Any? {
        logger.info { "------------- 环绕通知开始 -------------" }
        val startTime = System.currentTimeMillis()
        // 开始打印请求日志
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        val request = attributes.request
        val signature = proceedingJoinPoint.signature
        val name = signature.name

        // 打印请求信息
        logger.info { "请求地址: ${request.requestURL} ${request.method}"}
        logger.info { "类名方法: ${signature.declaringTypeName}.$name" }
        logger.info { "远程地址: ${request.remoteAddr}"}

        // 打印请求参数
        val args = proceedingJoinPoint.args
        val arguments = arrayOfNulls<Any>(args.size)
        for (i in args.indices) {
            if (args[i] is ServletRequest || args[i] is ServletResponse || args[i] is MultipartFile) {
                continue
            }
            arguments[i] = args[i]
        }
        // 排除字段，敏感字段或太长的字段不显示：身份证、手机号、邮箱、密码等
        val excludeProperties = arrayOf("cvv2", "idCard")
        val filters = PropertyPreFilters()
        val excludefilter = filters.addFilter()
        excludefilter.addExcludes(*excludeProperties)
        logger.info { "请求参数: ${JSONObject.toJSONString(arguments, excludefilter)}"}
        val result = proceedingJoinPoint.proceed()
        logger.info { "返回结果: ${JSONObject.toJSONString(result, excludefilter)}"}
        logger.info { "------------- 环绕通知结束 耗时：${System.currentTimeMillis() - startTime} ms -------------"}
        return result
    }
}