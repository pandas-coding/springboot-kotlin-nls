package com.sigmoid98.business.converter

import com.sigmoid98.business.domain.Demo
import com.sigmoid98.business.resp.DemoQueryResp
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper
interface DemoConvertor {
    companion object {
        val INSTANCE: DemoConvertor = Mappers.getMapper(DemoConvertor::class.java)
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "mobile", target = "mobile")
    fun demoToDemoQueryResp(demo: Demo): DemoQueryResp

    fun demosToDemoQueryResps(demos: List<Demo>): List<DemoQueryResp> // MapStruct 会自动处理列表

}