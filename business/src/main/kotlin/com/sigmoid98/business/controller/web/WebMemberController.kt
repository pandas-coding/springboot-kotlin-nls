package com.sigmoid98.business.controller.web

import cn.hutool.crypto.digest.DigestUtil
import com.sigmoid98.business.enums.SmsCodeUseEnum
import com.sigmoid98.business.req.MemberLoginReq
import com.sigmoid98.business.req.MemberRegisterReq
import com.sigmoid98.business.req.MemberResetReq
import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.resp.MemberLoginResp
import com.sigmoid98.business.service.KaptchaService
import com.sigmoid98.business.service.MemberService
import com.sigmoid98.business.service.SmsCodeService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/member")
class WebMemberController(
    @Resource private val memberService: MemberService,
    @Resource private val smsCodeService: SmsCodeService,
    @Resource private val kaptchaService: KaptchaService
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/register")
    fun register(@Valid @RequestBody req: MemberRegisterReq): CommonResp<Any> {
        // 加密用户密码
        val desensitizedReq = req.copy(
            password = DigestUtil.md5Hex(req.password.lowercase()),
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

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: MemberLoginReq): CommonResp<MemberLoginResp> {
        val desensitizedReq = req.copy(
            password = DigestUtil.md5Hex(req.password.lowercase()),
        )

        logger.info { "会员登录开始: ${desensitizedReq.mobile}" }
        // 校验图片验证码
        kaptchaService.validCode(desensitizedReq.imageCode, desensitizedReq.imageCodeToken)

        val memberLoginResp = memberService.login(desensitizedReq)
        return CommonResp(memberLoginResp)
    }

    @PostMapping("/reset")
    fun reset(req: MemberResetReq): CommonResp<Unit> {
        val desensitizedReq = req.copy(
            password = DigestUtil.md5Hex(req.password.lowercase()),
        )

        logger.info { "会员忘记密码重置开始: ${req.mobile}" }
        smsCodeService.validCode(req.mobile, SmsCodeUseEnum.RESET.code, req.code)
        logger.info { "忘记密码验证码校验通过: ${req.mobile}" }

        memberService.reset(desensitizedReq)
        return CommonResp()
    }

}