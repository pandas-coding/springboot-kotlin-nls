package com.sigmoid98.business.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 语音识别表
 * </p>
 *
 * @author mybatis-plus code generator
 * @since 2025-06-24 23:52:41
 */
@TableName("file_transfer")
class FileTransfer : Serializable {

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
     * 名称
     */
    @TableField("`name`")
    var name: String? = null

    /**
     * 音频文件时长|秒
     */
    @TableField("`second`")
    var second: Int? = null

    /**
     * 金额|元，second*单价
     */
    @TableField("`amount`")
    var amount: BigDecimal? = null

    /**
     * 文件链接
     */
    @TableField("`audio`")
    var audio: String? = null

    /**
     * 文件签名md5
     */
    @TableField("`file_sign`")
    var fileSign: String? = null

    /**
     * 支付状态|枚举[FileTransferPayStatusEnum];
     */
    @TableField("`pay_status`")
    var payStatus: String? = null

    /**
     * 识别状态|枚举[FileTransferStatusEnum];
     */
    @TableField("`status`")
    var status: String? = null

    /**
     * 音频语言|枚举[FileTransferLangEnum]
     */
    @TableField("`lang`")
    var lang: String? = null

    /**
     * VOD|videoId
     */
    @TableField("`vod`")
    var vod: String? = null

    /**
     * 任务ID
     */
    @TableField("`task_id`")
    var taskId: String? = null

    /**
     * 转换状态码
     */
    @TableField("`trans_status_code`")
    var transStatusCode: Int? = null

    /**
     * 转换状态说明
     */
    @TableField("`trans_status_text`")
    var transStatusText: String? = null

    /**
     * 转换时间|开始转换的时间
     */
    @TableField("`trans_time`")
    var transTime: LocalDateTime? = null

    /**
     * 完成时间|录音文件识别完成的时间
     */
    @TableField("`solve_time`")
    var solveTime: LocalDateTime? = null

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
        return "FileTransfer{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", name=" + name +
                ", second=" + second +
                ", amount=" + amount +
                ", audio=" + audio +
                ", fileSign=" + fileSign +
                ", payStatus=" + payStatus +
                ", status=" + status +
                ", lang=" + lang +
                ", vod=" + vod +
                ", taskId=" + taskId +
                ", transStatusCode=" + transStatusCode +
                ", transStatusText=" + transStatusText +
                ", transTime=" + transTime +
                ", solveTime=" + solveTime +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "}"
    }
}