package com.sigmoid98.business.resp

/**
 * 获取上传文件权限的响应-文件为重复上传的文件
 */
data class GetRepeatFileUploadAuthResp(
    val fileUrl: String,
    val videoId: String,
)
