package com.sigmoid98.business.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable

/**
 * <p>
 * 示例
 * </p>
 *
 * @author mybatis-plus code generator
 * @since 2025-06-09 23:33:42
 */
@TableName("demo")
class Demo : Serializable {

    /**
     * id
     */
    @TableId("`id`")
    var id: Long? = null

    /**
     * 手机号
     */
    @TableField("`mobile`")
    var mobile: String? = null

    /**
     * 密码
     */
    @TableField("`password`")
    var password: String? = null

    override fun toString(): String {
        return "Demo{" +
                "id=" + id +
                ", mobile=" + mobile +
                ", password=" + password +
                "}"
    }
}