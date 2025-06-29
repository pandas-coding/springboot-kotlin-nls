package com.sigmoid98.business.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * <p>
 * 订单信息表
 * </p>
 *
 * @author mybatis-plus code generator
 * @since 2025-06-30 00:38:00
 */
@TableName("order_info")
class OrderInfo : Serializable {

    /**
     * id
     */
    @TableId("`id`")
    var id: Long? = null

    /**
     * 订单号
     */
    @TableField("`order_no`")
    var orderNo: String? = null

    /**
     * 下单时间
     */
    @TableField("`order_at`")
    var orderAt: LocalDateTime? = null

    /**
     * 订单类型|枚举[OrderInfoOrderTypeEnum]
     */
    @TableField("`order_type`")
    var orderType: String? = null

    /**
     * 订单信息|根据订单类型，存放不同的信息
     */
    @TableField("`info`")
    var info: String? = null

    /**
     * 会员|id
     */
    @TableField("`member_id`")
    var memberId: Long? = null

    /**
     * 订单金额(元)
     */
    @TableField("`amount`")
    var amount: BigDecimal? = null

    /**
     * 支付时间|本地时间
     */
    @TableField("`pay_at`")
    var payAt: LocalDateTime? = null

    /**
     * 支付通道|枚举[OrderInfoChannelEnum]
     */
    @TableField("`channel`")
    var channel: String? = null

    /**
     * 通道时间|支付通道返回的时间
     */
    @TableField("`channel_at`")
    var channelAt: LocalDateTime? = null

    /**
     * 交易状态|枚举[OrderInfoStatusEnum]
     */
    @TableField("`status`")
    var status: String? = null

    /**
     * 订单描述
     */
    @TableField("`desc`")
    var desc: String? = null

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
        return "OrderInfo{" +
                "id=" + id +
                ", orderNo=" + orderNo +
                ", orderAt=" + orderAt +
                ", orderType=" + orderType +
                ", info=" + info +
                ", memberId=" + memberId +
                ", amount=" + amount +
                ", payAt=" + payAt +
                ", channel=" + channel +
                ", channelAt=" + channelAt +
                ", status=" + status +
                ", desc=" + desc +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "}"
    }
}