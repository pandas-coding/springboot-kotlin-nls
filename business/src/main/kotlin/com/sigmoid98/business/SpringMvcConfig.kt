package com.sigmoid98.business

import com.sigmoid98.business.interceptor.WebLoginInterceptor
import jakarta.annotation.Resource
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SpringMvcConfig(
    @Resource private val webLoginInterceptor: WebLoginInterceptor,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {

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
    }
}