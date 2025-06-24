package com.sigmoid98.business.resp

/**
 * 获取上传文件权限的响应-文件为初次上传的文件
 */
data class GetUploadAuthResp(
    val uploadAuth: String,
    val uploadAddress: String,
    val videoId: String,
)
