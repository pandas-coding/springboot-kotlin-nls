package com.sigmoid98.business.req

import jakarta.validation.constraints.NotBlank

data class DemoQueryReq(
    // A default value is required for non-nullable data class field.
    // Make the field nullable even its not or set a default value for a non-nullable data class field,
    // This will stop kotlin validation to happen before javax.
    // @link https://dev.to/mikhailepatko/using-spring-boot-starter-validation-library-with-kotlin-534a
    @field:NotBlank(message = "[手机号] 不能为空")
    val mobile: String = "",
)