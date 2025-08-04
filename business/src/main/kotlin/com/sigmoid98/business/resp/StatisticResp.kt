package com.sigmoid98.business.resp

import java.math.BigDecimal

data class StatisticResp(
    /**
     * 实时在线
     */
    val onLineCount: Int?,

    /**
     * 注册人数
     */
    val registerCount: Int?,

    /**
     * 语音识别数
     */
    val fileTransferCount: Int?,

    /**
     * 语音识别时长(秒)
     */
    val fileTransferSecond: Int?,

    /**
     * 订单数
     */
    val orderCount: Int?,

    /**
     * 订单金额
     */
    val orderAmount: BigDecimal?,

    /**
     * 近30天注册人数
     */
    val registerCountList: List<StatisticDateResp>?,

    /**
     * 近30天语音识别数
     */
    val fileTransferCountList: List<StatisticDateResp>?,

    /**
     * 近30天语音识别时长(秒)
     */
    val fileTransferSecondList: List<StatisticDateResp>?,

    /**
     * 近30天订单数
     */
    val orderCountList: List<StatisticDateResp>?,

    /**
     * 近30天订单金额
     */
    val orderAmountList: List<StatisticDateResp>?,
)
