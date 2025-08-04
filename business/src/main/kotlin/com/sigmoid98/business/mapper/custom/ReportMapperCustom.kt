package com.sigmoid98.business.mapper.custom

import java.math.BigDecimal

interface ReportMapperCustom {

    fun queryOnlineCount(): Int?

    fun queryRegisterCount(): Int?

    fun queryFileTransferCount(): Int?

    fun queryFileTransferSecond(): Int?

    fun queryOrderCount(): Int?

    fun queryOrderAmount(): BigDecimal?


}