package com.sigmoid98.business.controller.web

import cn.hutool.crypto.digest.DigestUtil
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

        val desensitizedReq = req.copy(
            password = DigestUtil.md5Hex(req.password)
        )

        memberService.register(desensitizedReq)

        return CommonResp()
    }

}