package com.sigmoid98.business.service

import cn.hutool.core.util.IdUtil
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
}