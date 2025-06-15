package com.sigmoid98.business.service

import cn.hutool.core.util.IdUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.sigmoid98.business.domain.Member
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import com.sigmoid98.business.mapper.MemberMapper
import com.sigmoid98.business.req.MemberLoginReq
import com.sigmoid98.business.req.MemberRegisterReq
import com.sigmoid98.business.resp.MemberLoginResp
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.util.Date

@Service
class MemberService(
    @Resource private val memberMapper: MemberMapper,
) {

    companion object {
        private val logger = KotlinLogging.logger {} // Logger for SmsCodeService
    }

    /**
     * 按手机号查会员信息
     */
    fun selectByMobile(mobile: String): Member? {
        val list = KtQueryChainWrapper(memberMapper, Member())
            .eq(Member::mobile, mobile)
            .list()

        return list.firstOrNull()
    }

    /**
     * 会员注册
     */
    fun register(req: MemberRegisterReq) {
        val registerMobile = req.mobile
        val savedMember = selectByMobile(registerMobile)
        if (null != savedMember) {
            throw BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_HAD_REGISTER)
        }

        val now = Date()
        val registerMember = Member().apply {
            id = IdUtil.getSnowflakeNextId()
            mobile = registerMobile
            password = req.password
            name = "${registerMobile.substring(0, 3)}****${registerMobile.substring(7)}"
            createdAt = now
            updatedAt = now
        }
        memberMapper.insert(registerMember)
    }

    /**
     * 登录
     */
    fun login(req: MemberLoginReq): MemberLoginResp {
        val loginMobile = req.mobile
        val savedMember = selectByMobile(loginMobile)
        if (savedMember == null) {
            logger.warn { "手机号不存在: $loginMobile" }
            throw BusinessException(BusinessExceptionEnum.MEMBER_LOGIN_ERROR)
        }

        if (savedMember.password.equals(req.password, ignoreCase = true)) {
            logger.warn { "密码错误: $loginMobile" }
            throw BusinessException(BusinessExceptionEnum.MEMBER_LOGIN_ERROR)
        }

        logger.info { "登录成功, 登录手机号: $loginMobile" }
        val loginResp = MemberLoginResp(
            id = savedMember.id!!,
            name = savedMember.name!!,
            token = "",
        )

        return loginResp
    }
}