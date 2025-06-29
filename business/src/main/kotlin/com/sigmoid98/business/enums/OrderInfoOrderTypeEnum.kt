package com.sigmoid98.business.enums

enum class OrderInfoOrderTypeEnum(
    val code: String,
    val desc: String,
) {
    FILE_TRANSFER_PAY("1", "语音识别单次付费"),
    ;
}