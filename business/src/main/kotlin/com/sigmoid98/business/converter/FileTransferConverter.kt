package com.sigmoid98.business.converter

import com.sigmoid98.business.domain.FileTransfer
import com.sigmoid98.business.resp.FileTransferQueryResp
import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.api.config.KONVERTER_GENERATE_CLASS
import io.mcarle.konvert.injector.spring.KComponent

@Konverter([
    // set to make will generate a class instead of an object.
    Konfig(key = KONVERTER_GENERATE_CLASS, value = "true"),
    // 启用此配置以避免konvert无法处理nullable类型到Nonnullable类型的转换
    Konfig(key = "konvert.enforce-not-null", value = "true"),
])
@KComponent
interface FileTransferConverter {

    fun toDto(vo: FileTransfer): FileTransferQueryResp

    fun toDtoList(voList: List<FileTransfer>): List<FileTransferQueryResp>
}