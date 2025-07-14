package com.sigmoid98.business.extensions

import com.baomidou.mybatisplus.extension.plugins.pagination.Page

/**
 * 为 Mybatis-Plus 的 Page 类扩展一个名为 'list' 的只读属性，
 * 它的值代理到原始的 'records' 属性。
 *
 * 用法:
 * val page: Page<User> = userMapper.selectPage(...)
 * val users = page.list // 直接通过 .list 访问，底层实际是 page.records
 */
val <T> Page<T>.list: List<T>
    get() = this.records

/**
 * 扩展 Mybatis-Plus 的 Page 对象，实现泛型转换功能。
 * 类似于 List.map()，可以将 Page<S> 转换为 Page<T>。
 *
 * @param S 原始数据类型 (如：数据库实体 FileTransfer)
 * @param T 目标数据类型 (如：响应 DTO FileTransferQueryResp)
 * @param converter 一个转换函数，定义了如何将一个 S 类型的对象转换为 T 类型的对象。
 * @return 一个新的、包含了转换后数据列表的 Page<T> 对象。
 */
fun <S, T> Page<S>.mapRecords(converter: (S) -> T): Page<T> =
    Page<T>(this.current, this.size, this.total)
        .also {
            val newRecords = this.records.map(converter)
            it.records = newRecords
        }
