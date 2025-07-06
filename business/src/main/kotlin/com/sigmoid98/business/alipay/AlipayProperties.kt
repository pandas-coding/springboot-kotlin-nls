package com.sigmoid98.business.alipay

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "alipay")
data class AlipayProperties(
    val gatewayHttpsHost: String,
    val gatewayHost: String,
    val appId: String,
    val appPrivateKey: String,
    val alipayPublicKey: String,
    val encryptKey: String,
    val notifyUrl: String,
    val returnUrl: String,
)
