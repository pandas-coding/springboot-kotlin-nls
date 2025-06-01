package com.sigmoid98.business.controller

import com.sigmoid98.business.mapper.DemoMapper
import com.sigmoid98.business.service.DemoService
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @Resource
    lateinit var demoService: DemoService


    @GetMapping ("/hello")
    fun hello(): String {
        return "hello from business/testController:hello()"
    }

    @GetMapping("/count")
    fun count(): Int {
        return demoService.count()
    }

}