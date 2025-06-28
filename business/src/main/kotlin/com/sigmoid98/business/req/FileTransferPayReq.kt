package com.sigmoid98.business.req

import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 语音识别下单接口请求参数
 * 字段为空视作前端没有传入
 */
data class FileTransferPayReq(
    val memberId: Long? = null,
    val name: String? = null,
    val second: Int? = null,
    val amount: BigDecimal? = null,
    val audio: String? = null,
    val fileSign: String? = null,
    val payStatus: String? = null,
    val status: String? = null,
    val lang: String? = null,
    @field:NotBlank("[vod] 不能为空")
    val vod: String = "",
    val taskId: String? = null,
    val transStatusCode: Int? = null,
    val transStatusText: String? = null,
    val transTime: LocalDateTime? = null,
    val solveTime: LocalDateTime? = null,
    val channel: String? = null,
)
