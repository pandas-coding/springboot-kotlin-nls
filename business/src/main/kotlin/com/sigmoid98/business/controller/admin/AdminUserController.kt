package com.sigmoid98.business.controller.admin

import cn.hutool.crypto.digest.DigestUtil
import com.sigmoid98.business.req.UserLoginReq
import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.resp.UserLoginResp
import com.sigmoid98.business.service.KaptchaService
import com.sigmoid98.business.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/user")
class AdminUserController(
    @Resource private val userService: UserService,
    @Resource private val kaptchaService: KaptchaService,
) {
    companion object {
        private val kLogger = KotlinLogging.logger {  }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: UserLoginReq): CommonResp<UserLoginResp> {
        val desensitizedReq = req.copy(
            password = DigestUtil.md5Hex(req.password.lowercase()),
        )
        kLogger.info { "userLogin password: ${desensitizedReq.password}" }
        kLogger.info { "用户登录开始: ${desensitizedReq.loginName}" }

        // 校验图片验证码
        kaptchaService.validCode(
            code = desensitizedReq.imageCode,
            token = desensitizedReq.imageCodeToken,
        )

        val userLoginResp = userService.login(desensitizedReq)
        return CommonResp(content = userLoginResp)
    }

}