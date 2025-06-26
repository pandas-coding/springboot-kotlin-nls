package com.sigmoid98.generator

import com.baomidou.mybatisplus.generator.FastAutoGenerator
import com.baomidou.mybatisplus.generator.config.OutputFile
import com.baomidou.mybatisplus.generator.config.rules.DateType
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine
import java.util.*

/**
 * 基于MyBatis-Plus Generator的代码生成器
 */
fun main() {
    // 1. 数据库连接信息
    val dbUrl = "jdbc:mysql://localhost:13306/springboot-kotlin-nls"
    val dbUsername = "root"
    val dbPassword = "123456"

    val generateTables = arrayOf("demo", "sms_code", "member", "file_transfer")

    // 2. 全局配置
    val projectPath = "${System.getProperty("user.dir")}/business" // 获取项目根路径
    val outputDir = "$projectPath/src/main/kotlin"   // Kotlin代码输出目录
    val xmlOutputDir = "$projectPath/src/main/resources/mapper" // XML输出目录

    println("开始生成代码:")
    println("Kotlin 文件输出目录: $outputDir")
    println("XML 文件输出目录: $xmlOutputDir")

    FastAutoGenerator.create(dbUrl, dbUsername, dbPassword)
        .globalConfig { builder ->
            builder
                .author("mybatis-plus code generator") // 作者
                .outputDir(outputDir)         // Kotlin 类输出目录
                .enableKotlin()               // 开启 Kotlin 模式！！!
                .dateType(DateType.TIME_PACK) // 使用 只使用 java.util.date 包下的日期类型
                .commentDate("yyyy-MM-dd HH:mm:ss") // 注释日期格式
                .disableOpenDir() // 生成后不打开输出目录 (可选)
        }
        .packageConfig { builder ->
            builder
                .parent("com.sigmoid98.business") // 父包名
                // .moduleName("system") // 模块名 (可选, 会在父包名下创建模块文件夹)
                .entity("domain")       // Entity 包名
                .mapper("mapper")       // Mapper 接口包名
                // .service("service")  // Service 包名 (如果需要)
                // .serviceImpl("service.impl") // Service Impl 包名 (如果需要)
                // .controller("controller") // Controller 包名 (如果需要)
                .pathInfo(Collections.singletonMap(OutputFile.xml, xmlOutputDir)) // 设置 XML 文件输出路径
        }
        .strategyConfig { builder ->
            builder.addInclude(*generateTables) // 需要生成的表名，可多个
                // .addTablePrefix("t_", "c_") // 表前缀过滤 (生成类名时会去掉这些前缀)

                // Entity实体类配置
                .entityBuilder()
                .enableFileOverride()
                // .enableLombok(false) // Kotlin 使用 data class，不需要 Lombok
                .enableChainModel() // 链式调用
                .enableTableFieldAnnotation() // 生成 @TableField 注解
                .formatFileName("%s") // Entity 文件名格式，默认 %sEntity
                // 设置Entity的模板配置, 不需要添加ftl后缀名, 会自动配置@link: https://baomidou.com/reference/new-code-generator-configuration/#%E6%A8%A1%E6%9D%BF%E9%85%8D%E7%BD%AE-templateconfig
                .kotlinTemplatePath("/templates/entity.kt")

                // Mapper 接口配置
                .mapperBuilder()
                .enableFileOverride()
                .enableMapperAnnotation() // 生成 @Mapper 注解
                .enableBaseResultMap()    // 生成通用 ResultMap
                .enableBaseColumnList()   // 生成通用 ColumnList
                .formatMapperFileName("%sMapper") // Mapper 接口文件名格式
                .formatXmlFileName("%sMapper")    // XML 文件名格式

                // Controller
                .controllerBuilder()
                .enableFileOverride()
                .disable()
                // Service
                .serviceBuilder()
                .enableFileOverride()
                .disable()
        }
        .templateEngine(FreemarkerTemplateEngine()) // 使用 Freemarker 引擎模板，如果依赖了 Velocity，也可以换成 VelocityTemplateEngine
        // .templateConfig { builder -> // 如果需要自定义模板
        //     builder.entity("/templates/entity.kt.ftl")
        //     // ... 其他模板
        // }
        .execute()
}