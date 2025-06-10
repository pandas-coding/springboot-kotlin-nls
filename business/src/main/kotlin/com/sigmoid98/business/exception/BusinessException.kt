package com.sigmoid98.business.exception

class BusinessException(
    val e: BusinessExceptionEnum
) : RuntimeException(e.desc) {

    /**
     * 不写入堆栈信息，简化异常日志，提高性能
     */
    override fun fillInStackTrace(): Throwable? {
        return this
    }

}