package com.sigmoid98.business.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.sigmoid98.business.converter.DemoConvertor
import com.sigmoid98.business.domain.Demo
import com.sigmoid98.business.mapper.DemoMapper
import com.sigmoid98.business.mapper.custom.DemoMapperCustom
import com.sigmoid98.business.req.DemoQueryReq
import com.sigmoid98.business.resp.DemoQueryResp
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

@Service
class DemoService {

    @Resource
    lateinit var demoMapperCustom: DemoMapperCustom

    @Resource
    lateinit var demoMapper: DemoMapper

    fun count(): Int {
        return demoMapperCustom.count()
    }

    fun query(req: DemoQueryReq): List<DemoQueryResp> {
        val mobile = req.mobile

        if (mobile.isBlank()) {
            throw Exception("mobile is blank")
        }

        // warning 使用MyBatis-Plus 搭配 Kotlin 的 LambdaQueryWrapper 构建查询时
        // Kotlin 的 Lambda 表达式编译后命名方式（如 query$lambda$0）无法被 OGNL 正常识别和处理，
        // 导致在动态 SQL 解析时 MyBatis 读取 sqlSegment 属性失败
        // @link https://baomidou.com/guides/wrapper/#kotlin%E4%B8%AD%E4%BD%BF%E7%94%A8wrapper
        val list = KtQueryChainWrapper(demoMapper, Demo::class.java)
            .eq(Demo::mobile, mobile)
            .orderByAsc(Demo::id)
            .list()

        return DemoConvertor.INSTANCE.demosToDemoQueryResps(list)
    }
}
