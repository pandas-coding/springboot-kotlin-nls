package com.sigmoid98.business.vo

import com.sigmoid98.business.serializer.BusinessLongAsStringSerializer
import kotlinx.serialization.Serializable

/**
 * member info stored in jwt payload
 */
@Serializable
data class MemberJwtPayloadVO(
    @Serializable(with = BusinessLongAsStringSerializer::class)
    val id: Long,
    val name: String,
)
