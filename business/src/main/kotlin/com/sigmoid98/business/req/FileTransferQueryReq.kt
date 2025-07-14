package com.sigmoid98.business.req

data class FileTransferQueryReq(
    val memberId: Long?,
    val lang: String?,
    val status: String?,
    val name: String?,
    val pagination: PageReq = PageReq(),
)
