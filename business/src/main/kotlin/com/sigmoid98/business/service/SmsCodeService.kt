package com.sigmoid98.business.service

import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.RandomUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.sigmoid98.business.domain.SmsCode
import com.sigmoid98.business.enums.SmsCodeStatusEnum
import com.sigmoid98.business.enums.SmsCodeUseEnum
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import com.sigmoid98.business.mapper.SmsCodeMapper
import com.sigmoid98.business.util.SmsUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.util.*

@Service
class SmsCodeService(
    @Resource val smsCodeMapper: SmsCodeMapper,
    @Resource val memberService: MemberService,
    @Resource val smsUtil: SmsUtil,
) {

    // Companion object to hold the logger instance
    companion object {
        private val logger = KotlinLogging.logger {} // Logger for SmsCodeService
    }

    /**
     * 注册发送验证码
     */
    fun sendCodeForRegister(mobile: String) {
        val member = memberService.selectByMobile(mobile)
        if (member != null) {
            throw BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_HAD_REGISTER)
        }

        sendCode(mobile, SmsCodeUseEnum.REGISTER.code)
    }

    /**
     * 发送短信验证码
     * 校验：如果1分钟内有同手机号同用途发送记录，则报错：短信请求过于频繁
     */
    fun sendCode(mobile: String, use: String) {
        val now = Date()
        val code = RandomUtil.randomNumbers(6)

        logger.info { "当前时间：${Date()}" }
        logger.info { "1分钟前: ${DateUtil.offsetMinute(Date(), -1)}" }

        val count = KtQueryChainWrapper(smsCodeMapper, SmsCode())
            .eq(SmsCode::mobile, mobile)
            .eq(SmsCode::use, use)
            .gt(SmsCode::createdAt, DateUtil.offsetMinute(Date(), -1)) // 一分钟以前新建的验证码
            .count()
        if (count > 0) {
            throw BusinessException(BusinessExceptionEnum.SMS_CODE_TOO_FREQUENT)
        }

        // 保存验证码到数据库
        val smsCode = SmsCode().apply {
            this.id = IdUtil.getSnowflakeNextId()
            this.mobile = mobile
            this.code = code
            this.use = use
            this.status = SmsCodeStatusEnum.NOT_USED.code
            this.createdAt = now
            this.updatedAt = now
        }
        smsCodeMapper.insert(smsCode)

        // 对接短信通道, 发送短信
        smsUtil.sendCode(mobile, code)
    }

    /**
     * 验证码校验:
     * 5分钟内、同手机号、同用途、未使用过的验证码才算有效
     * 只校验最后一次验证码
     */
    fun validCode(mobile: String, use: String, code: String) {

        val fiveMinutesAgo = DateUtil.offsetMinute(Date(), -5)

        val latestSmsCodeInFiveMinutes =
            KtQueryChainWrapper(smsCodeMapper, SmsCode())
                .eq(SmsCode::mobile, mobile)
                .eq(SmsCode::use, use)
                .eq(SmsCode::status, SmsCodeStatusEnum.NOT_USED.code)
                .gt(SmsCode::createdAt, fiveMinutesAgo)
                .orderByDesc(SmsCode::createdAt)
                .list()
                .firstOrNull()
                ?: run {
                    logger.warn { "验证码已过期或不存在, 手机号: ${mobile}, 输入验证码: ${code}, 用途: $use" }
                    throw BusinessException(BusinessExceptionEnum.SMS_CODE_EXPIRED)
                }

        if (latestSmsCodeInFiveMinutes.code != code) {
            logger.warn { "验证码不正确, 手机号: ${mobile}, 输入验证码: ${code}, 用途: $use" }
            throw BusinessException(BusinessExceptionEnum.SMS_CODE_ERROR)
        }

        // update sms-code status
        latestSmsCodeInFiveMinutes.apply {
            status = SmsCodeStatusEnum.USED.code
            updatedAt = Date()
        }.let {
            smsCodeMapper.updateById(latestSmsCodeInFiveMinutes)
        }
    }
}