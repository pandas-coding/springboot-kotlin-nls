package com.sigmoid98.business.req

/**
 * 通用分页请求基类
 */
data class PageReq(
    var page: Int = 1,
    var size: Int = 10,
)
