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
        val registerCount = reportMapperCustom.queryRegisterCount()
        val fileTransferCount = reportMapperCustom.queryFileTransferCount()
        val fileTransferSecond = reportMapperCustom.queryFileTransferSecond()
        val orderCount = reportMapperCustom.queryOrderCount()
        val orderAmount = reportMapperCustom.queryOrderAmount()

        return StatisticResp(
            onLineCount = onLineCount,
            registerCount = registerCount,
            fileTransferCount = fileTransferCount,
            fileTransferSecond = fileTransferSecond,
            orderCount = orderCount,
            orderAmount = orderAmount,
        )
    }
}