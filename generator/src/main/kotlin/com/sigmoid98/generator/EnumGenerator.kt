package com.sigmoid98.generator

import com.sigmoid98.business.enums.FileTransferLangEnum
import com.sigmoid98.business.enums.FileTransferPayStatusEnum
import com.sigmoid98.business.enums.FileTransferStatusEnum
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

import kotlin.system.measureTimeMillis

const val PATH = "web/public/js/enums.ts"

fun main(args: Array<String>) {
    val bufferObject = StringBuilder()
    val bufferArray = StringBuilder()

    val duration = measureTimeMillis {
        try {
            // 传递 Kotlin KClass 的 .java 属性来获取 Java Class 对象
            toJson(FileTransferLangEnum::class, bufferObject, bufferArray)
            toJson(FileTransferStatusEnum::class, bufferObject, bufferArray)
            toJson(FileTransferPayStatusEnum::class, bufferObject, bufferArray)

            val buffer = bufferObject.append("\r\n").append(bufferArray) // 保持特定的换行符
            writeJs(buffer.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // 字符串模板
    println("执行耗时: $duration 毫秒")
}

// Class<out Enum<*>> 更具体地表示枚举类
// Kotlin 没有受检异常，所以不需要 throws Exception
private fun toJson(clazz: KClass<out Enum<*>>, bufferObject: StringBuilder, bufferArray: StringBuilder) {
    // enumConst: 将YesNoEnum变成YES_NO
    if (clazz.simpleName.isNullOrEmpty()) {
        println("cannot find className for $clazz")
        return
    }
    // val 用于不可变变量
    val enumConst = clazz.simpleName!!.toUnderlineCase()
        .uppercase() // Kotlin 的 uppercase()
        .replace("_ENUM", "")

    // clazz.enumConstants 如果 clazz 不是枚举类型可能为 null（在此处不太可能）
    val enumValues = clazz.java.enumConstants
    if (enumValues.isNullOrEmpty()) {
        println("className ${clazz} is not an enum class")
        return
    }

    // 排除枚举属性和$VALUES，只获取code desc等
    // 使用 Kotlin 集合和函数式编程 (filter)
    val targetFields: List<Field> = clazz.java.declaredFields.filter { field ->
        // $VALUES 是一个合成字段，用于存储枚举实例数组
        Modifier.isPrivate(field.modifiers) && field.name != "\$VALUES"
    }

    // 生成对象
    bufferObject.append(enumConst).append("={")
    enumValues.forEachIndexed { index, enumConstant ->
        bufferObject.append(enumConstant.name).append(":") // Enum<*>.name 属性
        formatJsonObj(bufferObject, targetFields, enumConstant) //直接传递枚举常量
        if (index < enumValues.size - 1) {
            bufferObject.append(",")
        }
    }
    bufferObject.append("};\r\n") // 保持特定的换行符

    // 生成数组
    bufferArray.append(enumConst).append("_ARRAY=[")
    enumValues.forEachIndexed { index, enumConstant ->
        formatJsonObj(bufferArray, targetFields, enumConstant) //直接传递枚举常量
        if (index < enumValues.size - 1) {
            bufferArray.append(",")
        }
    }
    bufferArray.append("];\r\n") // 保持特定的换行符
}

/**
 * 将一个枚举值转成JSON对象字符串
 * 比如：SeatColEnum.YDZ_A("A", "A", "1")
 * 转成：{code:"A",desc:"A",type:"1"}
 */
// 直接传递枚举实例而不是 Class 和 Object
private fun formatJsonObj(
    buffer: StringBuilder, // 为清晰起见重命名
    targetFields: List<Field>,
    enumInstance: Enum<*> // 实际的枚举常量
) {
    buffer.append("{")
    targetFields.forEachIndexed { index, field ->
        val fieldName = field.name
        // Kotlin 的字符串操作来构造 getter 名称
        val getterName = "get${fieldName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}"
        try {
            // 从枚举实例的类中获取方法
            val getterMethod = enumInstance::class.java.getMethod(getterName)
            val value = getterMethod.invoke(enumInstance)
            buffer.append(fieldName).append(":\"").append(value).append("\"")
        } catch (e: NoSuchMethodException) {
            // 处理字段可能没有 getter 的情况（尽管根据模式不太可能）
            System.err.println("Getter $getterName not found for field $fieldName in ${enumInstance::class.java.simpleName}")
            buffer.append(fieldName).append(":\"ERROR_NO_GETTER\"")
        } catch (e: Exception) {
            // 其他反射调用异常
            System.err.println("Error invoking $getterName for field $fieldName: ${e.message}")
            buffer.append(fieldName).append(":\"ERROR_INVOKING_GETTER\"")
        }

        if (index < targetFields.size - 1) {
            buffer.append(",")
        }
    }
    buffer.append("}")
}

fun writeJs(content: String) {
    // 使用 'use' 扩展函数进行自动资源管理 (ARM / try-with-resources)
    try {
        FileOutputStream(PATH).use { fos ->
            OutputStreamWriter(fos, "UTF-8").use { osw ->
                println("Writing to: $PATH") // 字符串模板
                osw.write(content)
            }
        }
    } catch (e: Exception) {
        // 'use' 会处理关闭，因此不需要 finally 块来关闭
        e.printStackTrace()
    }
}

fun String.toUnderlineCase(): String {
    if (this.isBlank()) {
        return this
    }
    // 1. 将所有大写字母替换为 "_小写字母" (例如 "MyVariable" -> "_my_variable")
    //    (?<=[a-z])(?=[A-Z]) 匹配小写字母后的大写字母
    //    (?<=[A-Z])(?=[A-Z][a-z]) 匹配大写字母后跟一个大写字母再跟一个小写字母的情况 (如 UserID -> User_ID)
    val regex1 = "(?<=[a-z])(?=[A-Z])".toRegex()
    val regex2 = "(?<=[A-Z])(?=[A-Z][a-z])".toRegex()

    return this
        .replace(regex1, "_")
        .replace(regex2, "_")
        .lowercase() // 最后转换为全小写
}