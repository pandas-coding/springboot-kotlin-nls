package com.sigmoid98.business.controller.web

import com.sigmoid98.business.req.SmsCodeRegisterReq
import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.service.KaptchaService
import com.sigmoid98.business.service.SmsCodeService
import jakarta.annotation.Resource
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/sms-code")
class WebSmsCodeController(
    @Resource val smsCodeService: SmsCodeService,
    @Resource val kaptchaService: KaptchaService,
) {

    @PostMapping("/send-for-register")
    fun sendForRegister(@Valid @RequestBody req: SmsCodeRegisterReq): CommonResp<Unit> {

        // 校验图片验证码，防止短信攻击，不加的话，只能防止同一手机攻击，加上图片验证码，可防止不同的手机号攻击
        kaptchaService.validCode(req.imageCode, req.imageCodeToken)

        smsCodeService.sendCodeForRegister(req.mobile)
        return CommonResp()
    }
}