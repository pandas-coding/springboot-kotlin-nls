package com.sigmoid98.business.service

import cn.hutool.core.util.IdUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper
import com.sigmoid98.business.context.LoginMemberContext
import com.sigmoid98.business.domain.MemberLoginLog
import com.sigmoid98.business.mapper.MemberLoginLogMapper
import com.sigmoid98.business.resp.MemberLoginResp
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 记录用户登录日志
 */
@Service
class MemberLoginLogService(
    @Resource private val memberLoginLogMapper: MemberLoginLogMapper,
    @Resource private val loginMemberContext: LoginMemberContext,
) {

    companion object {
        private val kLogger = KotlinLogging.logger {  }
    }

    fun save(memberLoginResp: MemberLoginResp) {
        kLogger.info { "增加会员登录日志: $memberLoginResp" }
        val now = LocalDateTime.now()
        val toSaveMemberLoginLog = MemberLoginLog().apply {
            id = IdUtil.getSnowflakeNextId()
            memberId = memberLoginResp.id
            loginTime = now
            token = memberLoginResp.token
            heartCount = 0
            lastHeartTime = now
        }
        memberLoginLogMapper.insert(toSaveMemberLoginLog)
    }

    fun updateHeartInfo() {
        val memberLoginResp = loginMemberContext.member
        val token = memberLoginResp.token
        kLogger.info { "更新会员心跳信息: $token" }

        val memberLoginLogList = KtQueryChainWrapper(memberLoginLogMapper, MemberLoginLog())
            .eq(MemberLoginLog::token, token)
            .orderByDesc(MemberLoginLog::id)
            .list()

        if (memberLoginLogList.isEmpty()) {
            kLogger.warn { "未找到该token的登录信息: $token, 会员ID: ${memberLoginResp.id}" }
            save(memberLoginResp)
            return
        }

        val latestLoginLog = memberLoginLogList.first()
        val now = LocalDateTime.now()
        val updateSuccess = KtUpdateChainWrapper(memberLoginLogMapper, MemberLoginLog())
            .eq(MemberLoginLog::id, latestLoginLog.id)
            .set(MemberLoginLog::heartCount, (latestLoginLog.heartCount ?: 0) + 1)
            .set(MemberLoginLog::lastHeartTime, now)
            .update()

        if (!updateSuccess) {
            kLogger.error { "更新用户心跳失败" }
        }
    }
}