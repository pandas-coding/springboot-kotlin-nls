package com.sigmoid98.business.resp

/**
 * 分页响应
 */
class PageResp<T>(
    val total: Long,      // 总记录数
    val list: List<T>,    // 当前页数据列表
    val pageNum: Int,     // 当前页码
    val pageSize: Int,    // 每页数量
    val pages: Long,      // 总页数
)
