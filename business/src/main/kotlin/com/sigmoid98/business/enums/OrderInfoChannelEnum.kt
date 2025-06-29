package com.sigmoid98.business.enums

enum class OrderInfoChannelEnum(
    val code: String,
    val desc: String,
) {
    ALIPAY("A", "支付宝"),
    WXPAY("W", "微信"),
    ;
}