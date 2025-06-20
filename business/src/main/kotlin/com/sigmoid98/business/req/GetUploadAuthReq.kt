package com.sigmoid98.business.req

import jakarta.validation.constraints.NotBlank

/**
 * 获取上传文件权限的请求参数
 */
data class GetUploadAuthReq(
    /**
     * 文件名
     */
    @field:NotBlank(message = "[文件名] 不能为空")
    val name: String = "",
    /**
     * 文件标识, 对文件的md5值
     */
    @field:NotBlank(message = "[文件标识] 不能为空")
    val key: String = "",
)
