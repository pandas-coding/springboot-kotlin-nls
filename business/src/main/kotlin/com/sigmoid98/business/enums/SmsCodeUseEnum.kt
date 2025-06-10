package com.sigmoid98.business.enums

enum class SmsCodeUseEnum(
    val code: String,
    val desc: String,
) {
    REGISTER("0", "注册"),
    RESET("1", "重置密码"),
}