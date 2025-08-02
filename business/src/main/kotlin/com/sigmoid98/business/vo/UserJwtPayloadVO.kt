package com.sigmoid98.business.vo

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer

data class UserJwtPayloadVO(
    @field:JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val name: String,
)
