package com.sigmoid98.business.resp

data class CommonResp<T>(
    // 返回值类型
    var content: T? = null,
    // 业务上成功或失败
    var success: Boolean = true,
    // 返回信息
    var message: String? = null,
    // 返回错误信息详情
    var errorMessage: String? = null,
)
