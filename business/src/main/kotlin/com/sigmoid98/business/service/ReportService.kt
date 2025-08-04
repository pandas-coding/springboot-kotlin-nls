package com.sigmoid98.business.service

import com.sigmoid98.business.mapper.custom.ReportMapperCustom
import com.sigmoid98.business.resp.StatisticDateResp
import com.sigmoid98.business.resp.StatisticResp
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

        val registerCountIn30Days = reportMapperCustom
            .queryRegisterCountIn30Days()
            .let(::fill30)
        val fileTransferCountIn30Days = reportMapperCustom
            .queryFileTransferCountIn30Days()
            .let(::fill30)
        val fileTransferSecondIn30Days = reportMapperCustom
            .queryFileTransferSecondIn30Days()
            .let(::fill30)
        val orderCountIn30Days = reportMapperCustom
            .queryOrderCountIn30Days()
            .let(::fill30)
        val orderAmountIn30Days = reportMapperCustom
            .queryOrderAmountIn30Days()
            .let(::fill30)

        return StatisticResp(
            onLineCount = onLineCount,
            registerCount = registerCount,
            fileTransferCount = fileTransferCount,
            fileTransferSecond = fileTransferSecond,
            orderCount = orderCount,
            orderAmount = orderAmount,
            registerCountList = registerCountIn30Days,
            fileTransferCountList = fileTransferCountIn30Days,
            fileTransferSecondList = fileTransferSecondIn30Days,
            orderCountList = orderCountIn30Days,
            orderAmountList = orderAmountIn30Days,
        )
    }

    /**
     * 补齐最近30天的数据。
     * 如果原始列表中某天没有数据，则用计数值为0的记录填充。
     * @param list 包含部分日期数据的列表
     * @return 包含完整30天数据的列表，按日期从远到近排序
     */
    fun fill30(list: List<StatisticDateResp>): List<StatisticDateResp> {
        // 1. 为了高效查找，将输入列表转换为一个以日期为键的Map。
        //    这避免了在循环中反复遍历列表，时间复杂度从 O(n*m) 降为 O(n+m)。
        val dataMap = list.associateBy { it.date }

        // 2. 使用现代的 java.time API 处理日期，比 java.util.Date 更健壮。
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM-dd")

        // 3. 使用范围表达式和map函数式地生成30天的列表，代码更具声明性。
        return (29 downTo 0).map { i ->
            val date = now.minusDays(i.toLong())
            val dateString = date.format(formatter)

            // 4. 使用 Elvis 操作符 (?:) 优雅地处理数据缺失的情况。
            //    如果 map 中存在该日期的数据，就使用它；否则，创建一个默认值为0的新实例。
            dataMap[dateString] ?: StatisticDateResp(dateString, 0)
        }
    }
}