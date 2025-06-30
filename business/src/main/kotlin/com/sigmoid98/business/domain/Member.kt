package com.sigmoid98.business.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 会员表
 * </p>
 *
 * @author mybatis-plus code generator
 * @since 2025-06-30 00:38:00
 */
@TableName("member")
class Member : Serializable {

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

    /**
     * 昵称
     */
    @TableField("`name`")
    var name: String? = null

    /**
     * 创建时间
     */
    @TableField("`created_at`")
    var createdAt: LocalDateTime? = null

    /**
     * 修改时间
     */
    @TableField("`updated_at`")
    var updatedAt: LocalDateTime? = null

    override fun toString(): String {
        return "Member{" +
                "id=" + id +
                ", mobile=" + mobile +
                ", password=" + password +
                ", name=" + name +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "}"
    }
}