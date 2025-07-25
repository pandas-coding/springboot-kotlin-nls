package com.sigmoid98.business.resp

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.sigmoid98.business.serializer.JavaLocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

data class FileTransferQueryResp(
    @field:JsonSerialize(using = ToStringSerializer::class)
    val id: Long,

    @field:JsonSerialize(using = ToStringSerializer::class)
    val memberId: Long,

    val name: String,

    val second: Int,

    @field:JsonSerialize(using = ToStringSerializer::class)
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
