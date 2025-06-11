package com.sigmoid98.business.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.sigmoid98.business.domain.Member
import com.sigmoid98.business.mapper.MemberMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

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


}