package com.sigmoid98.business.controller.admin

import com.sigmoid98.business.req.UserLoginReq
import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.resp.UserLoginResp
import com.sigmoid98.business.service.KaptchaService
import com.sigmoid98.business.service.UserService
import jakarta.annotation.Resource
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/user")
class AdminUserController(
    @Resource private val userService: UserService,
    @Resource private val kaptchaService: KaptchaService,
) {

    fun login(@Valid @RequestBody req: UserLoginReq): CommonResp<UserLoginResp> {

    }

}