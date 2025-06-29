package com.sigmoid98.business.resp

import com.sigmoid98.business.serializer.BusinessLongAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable
data class MemberLoginResp(
    @Serializable(with = BusinessLongAsStringSerializer::class)
    val id: Long,
    val name: String,
    val token: String,
)
