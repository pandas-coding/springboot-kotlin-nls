package com.sigmoid98.business.req

import jakarta.validation.constraints.NotBlank

/**
 * 注册请求参数
 */
data class MemberLoginReq(
    /** 手机号 */
    @field:NotBlank(message = "[手机号] 不能为空")
    val mobile: String = "",
    /** 密码 */
    @field:NotBlank(message = "[密码] 不能为空")
    val password: String = "",
    /** 验证码 */
    @field:NotBlank(message = "[图片验证码] 不能为空")
    val imageCode: String = "",
    @field:NotBlank(message = "[图片验证码] 参数非法")
    val imageCodeToken: String = "",
)
