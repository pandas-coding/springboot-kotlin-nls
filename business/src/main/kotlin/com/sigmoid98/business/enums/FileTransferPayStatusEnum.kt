package com.sigmoid98.business.enums

enum class FileTransferPayStatusEnum(
    val code: String,
    val desc: String,
) {

    I("I", "未支付"),
    P("P", "处理中"),
    S("S", "支付成功"),
    F("F", "支付失败"),
    ;
}