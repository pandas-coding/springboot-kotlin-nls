package com.sigmoid98.business.controller.web

import cn.hutool.crypto.digest.DigestUtil
import com.sigmoid98.business.enums.SmsCodeUseEnum
import com.sigmoid98.business.req.MemberRegisterReq
import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.service.MemberService
import com.sigmoid98.business.service.SmsCodeService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/member")
class WebMemberController(
    @Resource private val memberService: MemberService,
    @Resource private val smsCodeService: SmsCodeService,
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/register")
    fun register(@Valid @RequestBody req: MemberRegisterReq): CommonResp<Any> {
        // 加密用户密码
        val desensitizedReq = req.copy(
            password = DigestUtil.md5Hex(req.password.lowercase())
        )

        logger.info { "会员注册开始: ${req.mobile}" }

        smsCodeService.validCode(
            mobile = desensitizedReq.mobile,
            use = SmsCodeUseEnum.REGISTER.code,
            code = desensitizedReq.code,
        )
        logger.info { "会员注册验证码校验通过: ${req.mobile}" }

        memberService.register(desensitizedReq)

        return CommonResp()
    }

}