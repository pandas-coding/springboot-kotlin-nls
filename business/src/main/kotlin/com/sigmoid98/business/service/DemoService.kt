package com.sigmoid98.business.service

import cn.hutool.core.bean.BeanUtil
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
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

        val query = LambdaQueryWrapper<Demo>()
            .eq(Demo::mobile, mobile)
            .orderByAsc(Demo::id)
        val list = demoMapper.selectList(query)

        return BeanUtil.copyToList(list, DemoQueryResp::class.java)
    }
}