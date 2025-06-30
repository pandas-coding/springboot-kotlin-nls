package com.sigmoid98.business.req

import java.math.BigDecimal

data class OrderInfoPayReq(
    val orderType: String,
    val info: String,
    val amount: BigDecimal,
    val channel: String,
    val desc: String,
)
