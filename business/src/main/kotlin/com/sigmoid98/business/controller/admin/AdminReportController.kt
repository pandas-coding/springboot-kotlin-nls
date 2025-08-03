package com.sigmoid98.business.controller.admin

import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.resp.StatisticResp
import com.sigmoid98.business.service.ReportService
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/report")
class AdminReportController(
    @Resource private val reportService: ReportService,
) {

    @GetMapping("/query-statistic")
    fun queryStatistic(): CommonResp<StatisticResp> {
        val resp = reportService.queryStatistic()
        return CommonResp(content = resp)
    }
}