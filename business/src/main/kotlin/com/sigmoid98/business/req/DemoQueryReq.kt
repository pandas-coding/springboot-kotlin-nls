package com.sigmoid98.business.req

import jakarta.validation.constraints.NotBlank

data class DemoQueryReq(
    // Make the field nullable even its not, This will stop kotlin validation to happen before javax
    @field:NotBlank(message = "[手机号] 不能为空")
    val mobile: String?,
)