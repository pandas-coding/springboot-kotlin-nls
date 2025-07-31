package com.sigmoid98.business.req

import jakarta.validation.constraints.NotBlank

data class UserLoginReq(
    /**
     * 登录名
     */
    @field:NotBlank(message = "[登录名] 不能为空")
    val loginName: String = "",

    /**
     * 密码
     */
    @field:NotBlank(message = "[密码] 不能为空")
    val password: String = "",

    /**
     * 验证码
     */
    @field:NotBlank(message = "[图片验证码] 不能为空")
    val imageCode: String = "",

    /**
     * 图片验证码token
     */
    @field:NotBlank(message = "[图片验证码] 参数非法")
    val imageCodeToken: String = "",
)
