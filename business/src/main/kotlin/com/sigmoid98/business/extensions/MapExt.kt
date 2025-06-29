package com.sigmoid98.business.extensions

import kotlinx.serialization.json.*

// 定义一个默认的 Json 实例
val DefaultJson: Json = Json {
    // ignoreUnknownKeys 对于从 Map 转换非常重要，因为 Map 可能包含额外字段
    ignoreUnknownKeys = true

    // coerceInputValues 仍然有用，它可以处理一些情况，
    // 例如将 JSON 中的 "true" 字符串解码为 Boolean 字段
    coerceInputValues = true
}

/**
 * 【推荐的直接转换方法】
 * 将 Map<String, Any?> 直接在内存中转换为指定的 data class T，不经过中间JSON字符串。
 * 类似于 Jackson 的 objectMapper.convertValue()。
 *
 * @param T 目标数据类的类型，必须是 @Serializable。
 * @param json 用于解码的 kotlinx.serialization.Json 实例。
 * @return 转换后的 data class 实例。
 */
inline fun <reified T : Any> Map<String, Any?>.convertToDataValue(json: Json = DefaultJson): T {
    // 1. 将 Map<String, Any?> 转换为一个在类型上更兼容的 JsonObject。
    //    这是此方法的关键，toJsonElement 的实现至关重要。
    val jsonObject = this.toJsonObject()

    // 2. 直接从内存中的 JsonObject 解码为目标类型 T。
    //    此方法避免了序列化到字符串再反序列化的开销。
    return json.decodeFromJsonElement(jsonObject)
}

/**
 * 辅助扩展函数，将 Map<String, Any?> 转换为 JsonObject。
 */
fun Map<String, Any?>.toJsonObject(): JsonObject {
    val content = this.mapValues { (_, value) ->
        // 将每个 value 转换为对应的 JsonElement
        value.toJsonElement()
    }
    return JsonObject(content)
}

/**
 * 【关键优化】
 * 辅助扩展函数，将任意类型的值（Any?）安全地转换为 JsonElement。
 *
 * 此实现的核心在于对 Long 类型的特殊处理，将其预先转换为字符串形式的 JsonPrimitive。
 * 这解决了在直接解码时最常见的类型不匹配问题（数字ID -> 字符串字段）。
 */
private fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is String -> JsonPrimitive(this)
    // 关键：将 Long 显式转换为 JSON 字符串，以匹配通常为 String 类型的 ID 字段。
    // 这个分支必须在 `is Number` 之前，因为 Long 也是 Number。
    is Long -> JsonPrimitive(this.toString())
    // 其他数字类型（Int, Double, Float等）保持为 JSON 数字。
    is Number -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is Map<*, *> -> {
        // 递归处理嵌套的Map
        @Suppress("UNCHECKED_CAST")
        (this as Map<String, Any?>).toJsonObject()
    }
    is Iterable<*> -> {
        // 递归处理嵌套的List
        val elements = this.map { it.toJsonElement() }
        JsonArray(elements)
    }
    // 对于不支持的类型，抛出异常以保证类型安全。
    else -> throw IllegalArgumentException("Type ${this.javaClass.name} is not supported for conversion to JsonElement")
}