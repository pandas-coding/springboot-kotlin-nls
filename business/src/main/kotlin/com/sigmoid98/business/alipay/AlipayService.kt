package com.sigmoid98.business.alipay

import com.alibaba.fastjson2.JSON
import com.alipay.easysdk.factory.Factory
import com.alipay.easysdk.kernel.Config
import com.alipay.easysdk.kernel.util.ResponseChecker
import com.alipay.easysdk.payment.common.models.AlipayTradeCloseResponse
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import io.github.oshai.kotlinlogging.KotlinLogging

@Service
class AlipayService(
    @Resource private val alipayProperties: AlipayProperties,
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun pay(subject: String, outTradeNo: String, totalAmount: String): AlipayTradePagePayResponse {
        logger.info { "调用支付宝下单接口开始, subject: ${subject}, outTradeNo: ${outTradeNo}, totalAmount: $totalAmount" }

        // 1. 设置参数（全局只需设置一次）
        Factory.setOptions(getOptions())

        return runCatching {
            // 2. 发起API调用（以创建网站支付为例）
            val response = Factory.Payment.Page().optional("qr_pay_mode", "4")
                .pay(subject, outTradeNo, totalAmount, alipayProperties.returnUrl)
            // 3. 处理响应或异常
            if (!ResponseChecker.success(response)) {
                logger.error { "调用支付宝下单接口失败, 原因: ${JSON.toJSONString(response)}" }
                throw BusinessException(BusinessExceptionEnum.ALIPAY_ERROR)
            }
            response
        }.getOrElse { exception ->
            logger.error(exception) { "调用支付宝下单接口异常" }
            throw BusinessException(BusinessExceptionEnum.ALIPAY_ERROR)
        }
    }

    fun query(outTradeNo: String): AlipayTradeQueryResponse {
        Factory.setOptions(getOptions())
        return runCatching {
            val response = Factory.Payment.Common().query(outTradeNo)
            if (!ResponseChecker.success(response)) {
                logger.warn { "调用支付宝订单查询接口失败, 原因: ${JSON.toJSONString(response)}" }
            }
            response
        }.getOrElse { exception ->
            logger.error(exception) { "调用支付宝订单查询接口异常" }
            throw BusinessException(BusinessExceptionEnum.ALIPAY_ERROR)
        }
    }

    fun close(outTradeNo: String): AlipayTradeCloseResponse {
        Factory.setOptions(getOptions())
        return runCatching {
            val response = Factory.Payment.Common().close(outTradeNo)
            if (!ResponseChecker.success(response)) {
                logger.warn { "调用支付宝关闭订单接口失败, 原因: ${JSON.toJSONString(response)}" }
            }
            response
        }.getOrElse { exception ->
            logger.error(exception) { "调用支付宝关闭订单接口异常, 原因:" }
            throw BusinessException(BusinessExceptionEnum.ALIPAY_ERROR)
        }
    }

    fun getOptions() = Config().apply {
        protocol = "https"
        signType = "RSA2"
        gatewayHost = alipayProperties.gatewayHost
        appId = alipayProperties.appId
        merchantPrivateKey = alipayProperties.appPrivateKey
        //注：证书文件路径支持设置为文件系统中的路径或CLASS_PATH中的路径，优先从文件系统中加载，加载失败后会继续尝试从CLASS_PATH中加载
        // config.merchantCertPath = "<-- 请填写您的应用公钥证书文件路径，例如：/foo/appCertPublicKey_2019051064521003.crt -->";
        // config.alipayCertPath = "<-- 请填写您的支付宝公钥证书文件路径，例如：/foo/alipayCertPublicKey_RSA2.crt -->";
        // config.alipayRootCertPath = "<-- 请填写您的支付宝根证书文件路径，例如：/foo/alipayRootCert.crt -->";
        alipayPublicKey = alipayProperties.alipayPublicKey
        notifyUrl = alipayProperties.notifyUrl
        encryptKey = alipayProperties.encryptKey
    }
}