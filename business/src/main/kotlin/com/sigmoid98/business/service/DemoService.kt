package com.sigmoid98.business.service

import com.sigmoid98.business.mapper.custom.DemoMapperCustom
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

@Service
class DemoService {

    @Resource
    lateinit var demoMapperCustom: DemoMapperCustom

    fun count(): Int {
        return demoMapperCustom.count()
    }

}