package com.sigmoid98.business.converter

import com.sigmoid98.business.domain.Demo
import com.sigmoid98.business.resp.DemoQueryResp
import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konvert
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.api.Mapping
import io.mcarle.konvert.api.config.KONVERTER_GENERATE_CLASS
import io.mcarle.konvert.injector.spring.KComponent


@Konverter([
    // set to make will generate a class instead of an object.
    Konfig(key = KONVERTER_GENERATE_CLASS, value = "true"),
    // Demo DAO中由于数据库表没有配置not null导致字段nullable, 启用此配置以避免konvert无法处理nullable类型到Nonnullable类型的转换
    Konfig(key = "konvert.enforce-not-null", value = "true"),
])
@KComponent
interface DemoConverter {

    @Konvert(mappings = [
        Mapping(source = "id", target = "id"),
        Mapping(source = "mobile", target = "mobile")
    ])
    fun toDTO(demo: Demo): DemoQueryResp

    @Konvert(mappings = [
        Mapping(source = "id", target = "id"),
        Mapping(source = "mobile", target = "mobile")
    ])
    fun toDTOs(demos: List<Demo>): List<DemoQueryResp>
}