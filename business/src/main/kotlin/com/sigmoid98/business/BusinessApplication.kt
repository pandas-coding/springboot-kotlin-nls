package com.sigmoid98.business

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BusinessApplication

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger(BusinessApplication::class.java)
    val environment = runApplication<BusinessApplication>(*args)
        .environment;

    logger.info("bootup successfully!!!")
    logger.info("bootup at: http://localhost:${environment.getProperty("server.port")}${environment.getProperty("server.servlet.context-path")}/hello")
}
