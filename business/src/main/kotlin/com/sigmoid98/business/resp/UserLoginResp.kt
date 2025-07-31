package com.sigmoid98.business.resp

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer

data class UserLoginResp(
    @field:JsonSerialize(using = ToStringSerializer::class)
    val id: Long,

    val loginName: String,

    val token: String,
)
