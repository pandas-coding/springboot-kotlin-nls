package com.sigmoid98.business.req

import jakarta.validation.constraints.NotBlank

/**
 * 重置密码请求参数
 */
data class MemberResetReq(
    @field:NotBlank(message = "[手机号] 不能为空")
    val mobile: String,
    @field:NotBlank(message = "[密码] 不能为空")
    val password: String,
    @field:NotBlank(message = "[验证码] 不能为空")
    val code: String,
)
