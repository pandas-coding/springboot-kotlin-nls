package com.sigmoid98.business.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

// 将Java的BigDecimal序列化为String的序列化器
object BigDecimalToStringSerializer : KSerializer<BigDecimal> {

    // 描述序列化后的数据类型，这里我们将其描述为字符串
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "java.math.BigDecimal",
        kind = PrimitiveKind.STRING,
    )

    // 如何将 BigDecimal 对象序列化
    override fun serialize(encoder: Encoder, value: BigDecimal) {
        // 使用 toPlainString() 来避免科学计数法，确保精度和格式的统一
        encoder.encodeString(value.toPlainString())
    }

    // 如何从序列化后的数据中反序列化出 BigDecimal 对象
    override fun deserialize(decoder: Decoder): BigDecimal {
        // 从字符串中构造 BigDecimal 对象
        return BigDecimal(decoder.decodeString())
    }
}