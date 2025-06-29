package com.sigmoid98.business.extensions

import java.awt.Graphics2D

/**
 * 扩展函数：自动释放 Graphics2D 的资源（模拟 try-with-resources）
 */
inline fun <R> Graphics2D.use(block: (Graphics2D) -> R): R {
    return try {
        block(this)
    } finally {
        this.dispose()
    }
}