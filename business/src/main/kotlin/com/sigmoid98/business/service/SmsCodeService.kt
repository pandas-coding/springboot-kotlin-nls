package com.sigmoid98.business.service

import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.RandomUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.sigmoid98.business.domain.SmsCode
import com.sigmoid98.business.enums.SmsCodeStatusEnum
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import com.sigmoid98.business.mapper.SmsCodeMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.util.*

@Service
class SmsCodeService(
    @Resource
    val smsCodeMapper: SmsCodeMapper,
) {

    // Companion object to hold the logger instance
    companion object {
        private val logger = KotlinLogging.logger {} // Logger for SmsCodeService
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


        // 保存短信到数据库
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
    }
}