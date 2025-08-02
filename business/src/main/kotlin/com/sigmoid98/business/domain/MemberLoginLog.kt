package com.sigmoid98.business.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 会员登录日志表
 * </p>
 *
 * @author mybatis-plus code generator
 * @since 2025-08-02 23:00:19
 */
@TableName("member_login_log")
class MemberLoginLog : Serializable {

    /**
     * id
     */
    @TableId("`id`")
    var id: Long? = null

    /**
     * 会员ID
     */
    @TableField("`member_id`")
    var memberId: Long? = null

    /**
     * 登录时间
     */
    @TableField("`login_time`")
    var loginTime: LocalDateTime? = null

    /**
     * 登录token
     */
    @TableField("`token`")
    var token: String? = null

    /**
     * 心跳次数
     */
    @TableField("`heart_count`")
    var heartCount: Int? = null

    /**
     * 最后心跳时间
     */
    @TableField("`last_heart_time`")
    var lastHeartTime: LocalDateTime? = null

    override fun toString(): String {
        return "MemberLoginLog{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", loginTime=" + loginTime +
                ", token=" + token +
                ", heartCount=" + heartCount +
                ", lastHeartTime=" + lastHeartTime +
                "}"
    }
}