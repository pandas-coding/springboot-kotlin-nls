package com.sigmoid98.business.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// 将Long序列化为String的序列化器
object BusinessLongAsStringSerializer : KSerializer<Long> {
    // 1. 描述符 (Descriptor): 告诉框架这个 Long 将被表示成什么类型
    // 在这里，我们告诉它我们将把 Long 序列化为原始的 String 类型。
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "BusinessLongAsString",
        kind = PrimitiveKind.STRING,
    )

    // 2. 序列化 (对象 -> JSON)
    // 当框架需要序列化一个 Long 值时，会调用这个方法。
    override fun serialize(encoder: Encoder, value: Long) {
        // 将 Long 转换为 String，然后编码为 JSON 字符串
        encoder.encodeString(value.toString())
    }

    // 3. 反序列化 (JSON -> 对象)
    // 当框架从 JSON 读取数据并期望得到一个 Long 时，会调用这个方法。
    override fun deserialize(decoder: Decoder): Long {
        // 从 JSON 中解码出字符串，然后转换回 Long
        return decoder.decodeString().toLong()
    }
}