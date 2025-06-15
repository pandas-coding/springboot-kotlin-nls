package com.sigmoid98.business.resp

data class MemberLoginResp(
    val id: Long,
    val name: String,
    val token: String,
)
