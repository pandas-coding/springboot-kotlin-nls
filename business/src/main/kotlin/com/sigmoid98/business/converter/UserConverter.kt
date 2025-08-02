package com.sigmoid98.business.converter

import com.sigmoid98.business.resp.MemberLoginResp
import com.sigmoid98.business.vo.UserJwtPayloadVO
import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konvert
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.api.Mapping
import io.mcarle.konvert.api.config.KONVERTER_GENERATE_CLASS
import io.mcarle.konvert.injector.spring.KComponent

/**
 * user相关data class的转化类
 */
@Konverter([
    // set to make will generate a class instead of an object.
    Konfig(key = KONVERTER_GENERATE_CLASS, value = "true"),
    // 启用此配置以避免konvert无法处理nullable类型到Nonnullable类型的转换
    Konfig(key = "konvert.enforce-not-null", value = "true"),
])
@KComponent
interface UserConverter {

    @Konvert(mappings = [
        Mapping(source = "id", target = "id"),
        Mapping(source = "name", target = "name"),
        Mapping(target = "token", expression = "token"),
    ])
    fun voToDTO(
        @Konverter.Source
        vo: UserJwtPayloadVO,
        token: String,
    ): MemberLoginResp
}