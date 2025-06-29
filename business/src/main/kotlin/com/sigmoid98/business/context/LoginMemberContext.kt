package com.sigmoid98.business.context

import com.sigmoid98.business.resp.MemberLoginResp
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class LoginMemberContext {

    companion object {
        private val logger = KotlinLogging.logger {}

        // threadLocal中保存一个登陆信息
        private val memberLoginThreadLocal = ThreadLocal<MemberLoginResp>()
    }

    var member: MemberLoginResp
        get() = memberLoginThreadLocal.get()
        set(member) {
            memberLoginThreadLocal.set(member)
        }

    /**
     * threadLocal中的用户id
     */
    val id: Long
        get() {
            try {
                return memberLoginThreadLocal.get().id
            } catch (ex: Exception) {
                logger.error(ex) { "获取登录会员信息异常" }
                throw ex
            }
        }

    fun removeMember() {
        memberLoginThreadLocal.remove()
    }
}