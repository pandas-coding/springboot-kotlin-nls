package com.sigmoid98.business.extensions

/**
 * 用于转换Result中的错误类型
 */
inline fun <T> Result<T>.mapError(transform: (Throwable) -> Throwable): Result<T>
    = fold(
        onSuccess =  { this },
        onFailure = { Result.failure(transform(it)) }
    )