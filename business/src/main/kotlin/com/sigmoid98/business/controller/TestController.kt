package com.sigmoid98.business.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.sigmoid98.business.domain.Demo
import com.sigmoid98.business.mapper.DemoMapper
import com.sigmoid98.business.req.DemoQueryReq
import com.sigmoid98.business.service.DemoService
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @Resource
    lateinit var demoService: DemoService

    @Resource
    lateinit var demoMapper: DemoMapper


    @GetMapping ("/hello")
    fun hello(): String {
        return "hello from business/testController:hello()"
    }

    @GetMapping("/count")
    fun count(): Int {
        return demoService.count()
    }

    @GetMapping("/query")
    fun query(req: DemoQueryReq): List<Demo> {
        val mobile = req.mobile
        val demoQueryWrapper = QueryWrapper<Demo>()
        demoQueryWrapper
            .eq("mobile", mobile)
            .orderByAsc("id")

        val list = demoMapper.selectList(demoQueryWrapper)
        return list
    }

}