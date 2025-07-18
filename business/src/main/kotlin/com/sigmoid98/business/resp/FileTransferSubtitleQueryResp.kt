package com.sigmoid98.business.resp

import com.sigmoid98.business.serializer.JavaLocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.LongAsStringSerializer
import java.time.LocalDateTime

@Serializable
data class FileTransferSubtitleQueryResp(
    @Serializable(with = LongAsStringSerializer::class)
    val id: Long,
    @Serializable(with = LongAsStringSerializer::class)
    val fileTransferId: Long,

    val index: Int,

    val begin: Int,

    val end: Int,

    val text: String,
    @Serializable(with = JavaLocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = null,
    @Serializable(with = JavaLocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime? = null,
)