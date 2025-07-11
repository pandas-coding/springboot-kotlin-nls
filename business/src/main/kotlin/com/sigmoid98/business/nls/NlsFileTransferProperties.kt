package com.sigmoid98.business.nls

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "nls.file-transfer")
data class NlsFileTransferProperties(
    val accessKeyId: String,
    val accessKeySecret: String,
    val regionId: String,
    val endpointName: String,
    val product: String,
    val domain: String,
    val version: String,
    val taskVersion: String,
    val callback: String,
)
