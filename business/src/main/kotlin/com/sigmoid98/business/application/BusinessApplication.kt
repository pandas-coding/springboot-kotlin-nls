package com.sigmoid98.business.application

import com.sigmoid98.business.alipay.AlipayProperties
import com.sigmoid98.business.nls.NlsFileTransferProperties
import com.sigmoid98.business.properties.DemoProperties
import org.mybatis.spring.annotation.MapperScan
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@ComponentScan("com.sigmoid98")
@MapperScan("com.sigmoid98.business.mapper")
@EnableConfigurationProperties(value = [
    AlipayProperties::class,
    NlsFileTransferProperties::class,
    DemoProperties::class,
])
@SpringBootApplication
class BusinessApplication

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val environment = runApplication<BusinessApplication>(*args)
        .environment;

    val appServerPort = environment.getProperty("server.port")
    val appServerContextPath = environment.getProperty("server.servlet.context-path")
    logger.info { "Application bootup!!!" }
    logger.info { "Application bootup at: http://localhost:${appServerPort}${appServerContextPath}" }
}
