package com.sigmoid98.business.mapper.custom

import com.sigmoid98.business.resp.StatisticDateResp
import java.math.BigDecimal

interface ReportMapperCustom {

    fun queryOnlineCount(): Int?

    fun queryRegisterCount(): Int?

    fun queryFileTransferCount(): Int?

    fun queryFileTransferSecond(): Int?

    fun queryOrderCount(): Int?

    fun queryOrderAmount(): BigDecimal?

    fun queryRegisterCountIn30Days(): List<StatisticDateResp>

    fun queryFileTransferCountIn30Days(): List<StatisticDateResp>

    fun queryFileTransferSecondIn30Days(): List<StatisticDateResp>

    fun queryOrderCountIn30Days(): List<StatisticDateResp>

    fun queryOrderAmountIn30Days(): List<StatisticDateResp>

}