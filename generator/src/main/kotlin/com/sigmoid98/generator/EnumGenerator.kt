package com.sigmoid98.generator

import com.sigmoid98.business.enums.FileTransferLangEnum
import com.sigmoid98.business.enums.FileTransferPayStatusEnum
import com.sigmoid98.business.enums.FileTransferStatusEnum
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor
import kotlin.system.measureTimeMillis

// 不能有"/"引导
const val PATH = "web/public/js/enums.ts"

fun main(args: Array<String>) {
    // 打印当前工作目录
    val currentWorkingDirectory = System.getProperty("user.dir")
    println("Current Working Directory: $currentWorkingDirectory")

    val targetFile = prepareFileForWritingInteractive(PATH)
    if (targetFile == null) {
        println("cannot create file in path: $PATH")
        return
    }

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
        println("className $clazz is not an enum class")
        return
    }

    // 使用 Kotlin 反射获取主构造函数中定义的属性
    // 这样可以精确地只获取 code, desc 等我们想要的属性，并过滤掉编译器生成的字段
    val constructorParamNames = clazz.primaryConstructor?.parameters?.mapNotNull { it.name } ?: emptyList()

    val targetProperties = clazz.members
        .filterIsInstance<KProperty1<out Enum<*>, *>>() // 筛选出所有成员属性
        .filter { it.name in constructorParamNames } as List<KProperty1<Enum<*>, *>> // 只保留主构造函数中定义的属性

    // 生成对象
    bufferObject.append("export const ").append(enumConst).append(" = {")
    enumValues.forEachIndexed { index, enumConstant ->
        bufferObject.append(enumConstant.name).append(":")
        formatJsonObj(bufferObject, targetProperties, enumConstant)
        if (index < enumValues.size - 1) {
            bufferObject.append(",")
        }
    }
    bufferObject.append("};\r\n")

    // 生成数组
    bufferArray.append("export const ").append(enumConst).append("_ARRAY = [")
    enumValues.forEachIndexed { index, enumConstant ->
        formatJsonObj(bufferArray, targetProperties, enumConstant)
        if (index < enumValues.size - 1) {
            bufferArray.append(",")
        }
    }
    bufferArray.append("];\r\n")
}

/**
 * 将一个枚举值转成JSON对象字符串
 * 比如：SeatColEnum.YDZ_A("A", "A", "1")
 * 转成：{code:"A",desc:"A",type:"1"}
 */
// 直接传递枚举实例而不是 Class 和 Object
private fun formatJsonObj(
    buffer: StringBuilder, // 为清晰起见重命名
    targetProperties: List<KProperty1<Enum<*>, *>>,
    enumInstance: Enum<*> // 实际的枚举常量
) {
    buffer.append("{")
    targetProperties.forEachIndexed { index, prop ->
        val propName = prop.name
        val propValue = prop.get(enumInstance) // 直接获取属性值，无需反射调用 getter

        // 修正为标准的 JSON 格式，key 和 value 都用双引号包裹
        buffer.append("\"").append(propName).append("\":\"").append(propValue).append("\"")

        if (index < targetProperties.size - 1) {
            buffer.append(",")
        }
    }
    buffer.append("}")
}

fun writeJs(content: String) {
    try {
        FileOutputStream(PATH).use { fos ->
            OutputStreamWriter(fos, "UTF-8").use { osw ->
                println("Writing to: $PATH")
                // 添加 ts-nocheck 注释，避免 TypeScript 对生成的文件进行类型检查
                // osw.write("// @ts-nocheck\r\n")
                osw.write("// This file is generated by a script, do not edit it directly.\r\n\r\n")
                osw.write(content)
            }
        }
    } catch (e: Exception) {
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

/**
 * 带有交互式确认的文件写入准备函数。
 * 它会检查文件是否存在，并根据情况与用户交互。
 *
 * @param filePath 要操作的文件路径字符串。
 * @return 如果用户确认可以继续操作，则返回一个 File 对象；如果用户取消，则返回 null。
 */
fun prepareFileForWritingInteractive(filePath: String): File? {
    val file = File(filePath)

    if (file.exists()) {
        // --- 文件已存在，询问是否覆盖 ---
        print("⚠️  Warning: File already exists at '${file.absolutePath}'.\nDo you want to overwrite it? (y/N): ")

        // readln() 读取一行输入，如果输入为空则会是空字符串
        // .lowercase() 转换为小写，方便比较
        // .trim() 去除前后空格
        val userInput = readlnOrNull()?.trim()?.lowercase()

        // 只有当用户明确输入 "y" 或 "yes" 时才继续
        if (userInput !in listOf("y", "yes")) {
            println("❌ Operation cancelled by user.")
            return null // 用户取消，返回 null
        }
        println("✅ User confirmed. Proceeding with overwrite...")

    } else {
        // --- 文件不存在，准备创建 ---
        println("ℹ️  Info: File not found at '${file.absolutePath}'.")
        println("   It will be created.")

        // 自动创建不存在的父目录
        val parentDir = file.parentFile
        if (parentDir != null && !parentDir.exists()) {
            val success = parentDir.mkdirs()
            if (success) {
                println("   Directory '${parentDir.absolutePath}' created.")
            } else {
                System.err.println("   Failed to create directory '${parentDir.absolutePath}'.")
                return null // 目录创建失败，无法继续
            }
        }
    }

    return file // 准备就绪，返回 File 对象
}