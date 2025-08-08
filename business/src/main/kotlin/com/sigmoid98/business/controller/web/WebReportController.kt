package com.sigmoid98.business.controller.web

import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.resp.StatisticResp
import com.sigmoid98.business.service.ReportService
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/report")
class WebReportController(
    @Resource private val reportService: ReportService,
) {

    /**
     * 首页数字统计
     */
    @GetMapping("/query-statistic")
    fun queryStatistic(): CommonResp<StatisticResp> {
        val resp = reportService.queryStatistic()
        return CommonResp(content = resp)
    }

}