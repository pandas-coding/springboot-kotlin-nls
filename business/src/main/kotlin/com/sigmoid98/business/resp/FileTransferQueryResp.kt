package com.sigmoid98.business.resp

import com.sigmoid98.business.serializer.BigDecimalToStringSerializer
import com.sigmoid98.business.serializer.JavaLocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.LongAsStringSerializer

import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class FileTransferQueryResp(
    @Serializable(with = LongAsStringSerializer::class)
    val id: Long,

    val memberId: Long,

    val name: String,

    val second: Int,

    @Serializable(with = BigDecimalToStringSerializer::class)
    val amount: BigDecimal,

    val audio: String,

    val fileSign: String,

    val payStatus: String,

    val status: String,

    val lang: String,

    val vod: String,

    val taskId: String?,

    val transStatusCode: Int?,

    val transStatusText: String?,

    @Serializable(with = JavaLocalDateTimeSerializer::class)
    val transTime: LocalDateTime?,

    @Serializable(with = JavaLocalDateTimeSerializer::class)
    val solveTime: LocalDateTime?,

    @Serializable(with = JavaLocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,

    @Serializable(with = JavaLocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime?,
)
