package com.sigmoid98.business.service

import com.sigmoid98.business.mapper.custom.ReportMapperCustom
import com.sigmoid98.business.resp.StatisticResp
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

@Service
class ReportService(
    @Resource private val reportMapperCustom: ReportMapperCustom,
) {

    fun queryStatistic(): StatisticResp {
        val onLineCount = reportMapperCustom.queryOnlineCount()

        return StatisticResp(
            onLineCount = onLineCount,
        )
    }
}