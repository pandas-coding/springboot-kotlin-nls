package com.sigmoid98.business.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime

/**
 * 一个用于 java.time.LocalDateTime 的 kotlinx.serialization 自定义序列化器。
 * 它将 LocalDateTime 序列化为 ISO 8601 格式的字符串，例如 "2025-06-30T15:45:30"。
 */
object JavaLocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("java.time.LocalDateTime", PrimitiveKind.STRING)

    /**
     * 序列化方法：将 LocalDateTime 对象转换为字符串。
     */
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        // value.toString() 默认就输出 ISO 8601 格式，非常方便。
        encoder.encodeString(value.toString())
    }

    /**
     * 反序列化方法：将字符串解析回 LocalDateTime 对象。
     */
    override fun deserialize(decoder: Decoder): LocalDateTime {
        // 从解码器中读取字符串，然后使用 LocalDateTime.parse 进行解析。
        return LocalDateTime.parse(decoder.decodeString())
    }


}