package com.sigmoid98.business.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.sigmoid98.business.domain.User
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import com.sigmoid98.business.mapper.UserMapper
import com.sigmoid98.business.req.UserLoginReq
import com.sigmoid98.business.resp.UserLoginResp
import com.sigmoid98.business.util.JwtProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

@Service
class UserService(
    @Resource private val userMapper: UserMapper,
    @Resource private val jwtProvider: JwtProvider,
) {

    companion object {
        private val kLogger = KotlinLogging.logger {  }
    }

    fun selectByLoginName(loginName: String): User? {
        val userList = KtQueryChainWrapper(userMapper, User())
            .eq(User::loginName, loginName)
            .list()
        return userList.firstOrNull()
    }

    /**
     * 登录
     */
    fun login(req: UserLoginReq): UserLoginResp {
        val savedUser = selectByLoginName(req.loginName)
        if (savedUser == null) {
            kLogger.warn { "登录名不存在, ${req.loginName}" }
            throw BusinessException(BusinessExceptionEnum.USER_LOGIN_ERROR)
        }

        if (!savedUser.password.equals(req.password, ignoreCase = true)) {
            kLogger.warn { "密码错误: ${req.loginName}" }
            throw BusinessException(BusinessExceptionEnum.USER_LOGIN_ERROR)
        }

        kLogger.info { "登录成功, ${req.loginName}" }
        val savedUserId = savedUser.id!!
        val savedUserName = savedUser.loginName!!

        val token = jwtProvider.createLoginToken(
            "id" to savedUserId.toString(),
            "name" to savedUserName,
        )

        return UserLoginResp(
            id = savedUserId,
            loginName = savedUserName,
            token = token,
        )
    }
}