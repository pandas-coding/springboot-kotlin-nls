package com.sigmoid98.business.alipay

import com.alipay.api.internal.util.AlipaySignature
import com.sigmoid98.business.extensions.getDecodedParameter
import com.sigmoid98.business.service.AfterPayService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/alipay")
class AlipayController(
    @Value("\${env}") private val env: String,
    @Resource private val alipayProperties: AlipayProperties,
    @Resource private val afterPayService: AfterPayService,
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/callback")
    fun alipayCallback(request: HttpServletRequest): String {
        val params = request.parameterMap.mapValues { (_, values) ->
            values.joinToString(",")
        }
        logger.info { "验签参数: $params" }

        // 验签确保请求是alipay发起的, 并且参数没有被篡改过
        val isVerified = AlipaySignature.rsaCheckV1(
            params,
            alipayProperties.alipayPublicKey,
            StandardCharsets.UTF_8.name(),
            "RSA2",
        )

        var skipVerify = false
        if (env != "prod") {
            logger.info { "不是生产环境, 跳过验签, 当前环境: $env" }
            skipVerify = true
        }

        return if (isVerified || skipVerify) {
            logger.info { "支付宝回调验签成功" }

            // 9. 使用扩展函数抽取重复的转码逻辑
            val outTradeNo = request.getDecodedParameter("out_trade_no")
            logger.info { "支付宝回调，本地订单号：$outTradeNo" }

            val tradeNo = request.getDecodedParameter("trade_no")
            val tradeStatus = request.getDecodedParameter("trade_status")
            val gmtPayment = request.getDecodedParameter("gmt_payment")

            val channelTime = LocalDateTime.parse(gmtPayment, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            if ("TRADE_SUCCESS" == tradeStatus) {
                logger.info { "支付成功，执行后续业务处理" }
                afterPayService.afterPaySuccess(orderNo = outTradeNo, channelTime = channelTime)
            }

            "success"

        } else {
            logger.warn { "支付宝回调验签失败, params: $params" }
            "failure"
        }
    }

    
}