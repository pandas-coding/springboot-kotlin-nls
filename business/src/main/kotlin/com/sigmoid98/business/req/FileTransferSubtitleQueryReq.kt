package com.sigmoid98.business.req

data class FileTransferSubtitleQueryReq(
    val fileTransferId: Long,
    // 分页参数
    val pagination: PageReq = PageReq(),
)
