package com.sigmoid98.business.service

import com.sigmoid98.business.mapper.DemoMapper
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

@Service
class DemoService {

    @Resource
    lateinit var demoMapper: DemoMapper

    fun count(): Int {
        return demoMapper.count()
    }

}