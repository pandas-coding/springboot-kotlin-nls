package com.sigmoid98.business.req

import jakarta.validation.constraints.NotBlank

data class SmsCodeRegisterReq(
    @field:NotBlank(message = "[手机号] 不能为空")
    val mobile: String = "",
)
