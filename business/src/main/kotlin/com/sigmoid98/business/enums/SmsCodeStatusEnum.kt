package com.sigmoid98.business.enums

enum class SmsCodeStatusEnum(
    val code: String,
    val desc: String,
) {
    USED("1", "已使用"),
    NOT_USED("0", "未使用")
}