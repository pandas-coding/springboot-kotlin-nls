package com.sigmoid98.business

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BusinessApplication


private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val environment = runApplication<BusinessApplication>(*args)
        .environment;

    val appServerPort = environment.getProperty("server.port")
    val appServerContextPath = environment.getProperty("server.servlet.context-path")
    logger.info { "Application bootup!!!" }
    logger.info { "Application bootup at: http://localhost:${appServerPort}${appServerContextPath}/hello" }
}
