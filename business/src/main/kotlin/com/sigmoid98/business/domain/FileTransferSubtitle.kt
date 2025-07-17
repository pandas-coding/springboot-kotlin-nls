package com.sigmoid98.business.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.time.LocalDateTime

/**
 * <p>
 * 语音识别字幕
 * </p>
 *
 * @author mybatis-plus code generator
 * @since 2025-07-17 22:38:46
 */
@TableName("file_transfer_subtitle")
class FileTransferSubtitle : Serializable {

    /**
     * id
     */
    @TableId("`id`")
    var id: Long? = null

    /**
     * 录音转换ID
     */
    @TableField("`file_transfer_id`")
    var fileTransferId: Long? = null

    /**
     * 索引号
     */
    @TableField("`index`")
    var index: Int? = null

    /**
     * 开始时间，毫秒
     */
    @TableField("`begin`")
    var begin: Int? = null

    /**
     * 结束时间，毫秒
     */
    @TableField("`end`")
    var end: Int? = null

    /**
     * 字幕
     */
    @TableField("`text`")
    var text: String? = null

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
        return "FileTransferSubtitle{" +
                "id=" + id +
                ", fileTransferId=" + fileTransferId +
                ", index=" + index +
                ", begin=" + begin +
                ", end=" + end +
                ", text=" + text +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "}"
    }
}