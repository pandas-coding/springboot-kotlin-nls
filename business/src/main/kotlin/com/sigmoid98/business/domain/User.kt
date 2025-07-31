package com.sigmoid98.business.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable

/**
 * <p>
 * 用户
 * </p>
 *
 * @author mybatis-plus code generator
 * @since 2025-07-30 00:51:24
 */
@TableName("user")
class User : Serializable {

    /**
     * ID
     */
    @TableId("`id`")
    var id: Long? = null

    /**
     * 登录名
     */
    @TableField("`login_name`")
    var loginName: String? = null

    /**
     * 密码
     */
    @TableField("`password`")
    var password: String? = null

    override fun toString(): String {
        return "User{" +
                "id=" + id +
                ", loginName=" + loginName +
                ", password=" + password +
                "}"
    }
}