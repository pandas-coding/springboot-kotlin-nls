package com.sigmoid98.business.controller

import com.sigmoid98.business.req.DemoQueryReq
import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.resp.DemoQueryResp
import com.sigmoid98.business.service.DemoService
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @Resource
    lateinit var demoService: DemoService

    @GetMapping ("/hello")
    fun hello(): CommonResp<String> {
        val hello = "hello from business/testController:hello()"
        return CommonResp(hello)
    }

    @GetMapping("/count")
    fun count(): CommonResp<Int> {
        val count = demoService.count()
        return CommonResp(count)
    }

    @GetMapping("/query")
    fun query(req: DemoQueryReq): CommonResp<List<DemoQueryResp>> {
        val demoList = demoService.query(req)
        return CommonResp(demoList)
    }

}