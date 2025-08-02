package com.sigmoid98.business.config

import com.sigmoid98.business.interceptor.AdminLoginInterceptor
import com.sigmoid98.business.interceptor.LoggingInterceptor
import com.sigmoid98.business.interceptor.WebLoginInterceptor
import jakarta.annotation.Resource
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SpringMvcConfig(
    @Resource private val loggingInterceptor: LoggingInterceptor,
    @Resource private val webLoginInterceptor: WebLoginInterceptor,
    @Resource private val adminLoginInterceptor: AdminLoginInterceptor,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        // 添加生成日志跟踪号的interceptor
        registry.addInterceptor(loggingInterceptor)

        // 设置web/*路径下不要包含context-path
        registry.addInterceptor(webLoginInterceptor)
            .addPathPatterns("/web/**")
            .excludePathPatterns(
                "/web/kaptcha/image-code/*",
                "/web/member/login",
                "/web/member/register",
                "/web/member/reset",
                "/web/sms-code/send-for-register",
                "/web/sms-code/send-for-reset",
            )

        // 设置admin/*路径下不要包含context-path
        registry.addInterceptor(adminLoginInterceptor)
            .addPathPatterns("/admin/**")
            .excludePathPatterns(
                "/admin/kaptcha/image-code/*",
                "/admin/user/login",
                "/admin/report/**",
            )
    }
}