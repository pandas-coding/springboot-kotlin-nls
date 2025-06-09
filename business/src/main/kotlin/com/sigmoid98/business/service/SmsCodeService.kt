package com.sigmoid98.business.service

import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.RandomUtil
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper
import com.sigmoid98.business.domain.SmsCode
import com.sigmoid98.business.enums.SmsCodeStatusEnum
import com.sigmoid98.business.mapper.SmsCodeMapper
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Date

@Service
class SmsCodeService(
    @Resource
    val smsCodeMapper: SmsCodeMapper,
) {

    /**
     * 发送短信验证码
     */
    fun sendCode(mobile: String, use: String) {
        val now = Date()
        val code = RandomUtil.randomNumbers(6)

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