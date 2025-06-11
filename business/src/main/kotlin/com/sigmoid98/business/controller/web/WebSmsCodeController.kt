package com.sigmoid98.business.controller.web

import com.sigmoid98.business.req.SmsCodeRegisterReq
import com.sigmoid98.business.resp.CommonResp
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
) {

    @PostMapping("/send-for-register")
    fun sendForRegister(@Valid @RequestBody req: SmsCodeRegisterReq): CommonResp<Unit> {
        smsCodeService.sendCodeForRegister(req.mobile)
        return CommonResp()
    }
}