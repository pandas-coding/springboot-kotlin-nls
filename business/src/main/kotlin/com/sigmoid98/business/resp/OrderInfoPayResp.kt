package com.sigmoid98.business.resp

data class OrderInfoPayResp(
    val orderNo: String,
    // 调用支付渠道的返回值
    val channelResult: String,
)
