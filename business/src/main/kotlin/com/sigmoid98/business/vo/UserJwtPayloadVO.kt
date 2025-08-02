package com.sigmoid98.business.vo

import com.sigmoid98.business.serializer.BusinessLongAsStringSerializer
import kotlinx.serialization.Serializable

/**
 * user info stored in jwt payload
 */
@Serializable
data class UserJwtPayloadVO(
    @Serializable(with = BusinessLongAsStringSerializer::class)
    val id: Long,
    val name: String,
)
