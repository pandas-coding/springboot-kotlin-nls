package com.sigmoid98.business.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.math.BigDecimal

@ConfigurationProperties(prefix = "demo")
data class DemoProperties(
    val name: String,
    val audio: String,
    val key: String,
    val amount: BigDecimal,
    val lang: String,
    val vid: String,
)
