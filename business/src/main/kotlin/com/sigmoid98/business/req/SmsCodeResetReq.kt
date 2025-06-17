package com.sigmoid98.business.req

import jakarta.validation.constraints.NotBlank

/**
 * 重置密码请求参数
 */
data class SmsCodeResetReq(
    @field:NotBlank(message = "[手机号] 不能为空")
    val mobile: String = "",
    @field:NotBlank(message = "[图片验证码] 不能为空")
    val imageCode: String = "",
    @field:NotBlank(message = "[图片验证码] 参数非法")
    val imageCodeToken: String = "",
)
