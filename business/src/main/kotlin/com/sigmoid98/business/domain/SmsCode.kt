package com.sigmoid98.business.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 短信验证码表
 * </p>
 *
 * @author mybatis-plus code generator
 * @since 2025-06-09 21:22:27
 */
@TableName("sms_code")
class SmsCode : Serializable {

    /**
     * id
     */
    @TableId("id")
    var id: Long? = null

    /**
     * 手机号
     */
    @TableField("mobile")
    var mobile: String? = null

    /**
     * 验证码
     */
    @TableField("code")
    var code: String? = null

    /**
     * 用途|枚举[SmsCodeUseEnum]：REGISTER("0", "注册"), FORGET_PASSWORD("1", "忘记密码")
     */
    @TableField("use")
    var use: String? = null

    /**
     * 状态|枚举[SmsCodeStatusEnum]：USED("1", "已使用"), NOT_USED("0", "未使用")
     */
    @TableField("status")
    var status: String? = null

    /**
     * 创建时间
     */
    @TableField("created_at")
    var createdAt: LocalDateTime? = null

    /**
     * 修改时间
     */
    @TableField("updated_at")
    var updatedAt: LocalDateTime? = null

    override fun toString(): String {
        return "SmsCode{" +
        "id=" + id +
        ", mobile=" + mobile +
        ", code=" + code +
        ", use=" + use +
        ", status=" + status +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        "}"
    }
}
