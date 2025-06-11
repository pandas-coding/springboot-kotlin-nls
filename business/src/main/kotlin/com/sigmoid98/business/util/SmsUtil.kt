package com.sigmoid98.business.util

import com.alibaba.fastjson.JSON
import com.aliyun.auth.credentials.Credential
import com.aliyun.auth.credentials.provider.StaticCredentialProvider
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import darabonba.core.client.ClientOverrideConfiguration
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture


@Component
class SmsUtil(
    @Value("\${sms.aliyun.signName}") private val signName: String,
    @Value("\${sms.aliyun.templateCode}") private val templateCode: String,
    @Value("\${sms.aliyun.accessKeyId}") private val accessKeyId: String,
    @Value("\${sms.aliyun.accessKeySecret}") private val accessKeySecret: String,
) {

    // Companion object to hold the logger instance
    companion object {
        private val logger = KotlinLogging.logger {} // Logger for SmsCodeService
    }

    /**
     * 发送验证码
     * 短信示例：【阿里云短信测试】您正在使用阿里云短信测试服务，体验验证码是：564655，如非本人操作，请忽略本短信！
     * @link https://next.api.aliyun.com/api/Dysmsapi/2017-05-25/SendSms?spm=api-workbench.API%20Document.0.0.5be349a5NCOHvS&tab=DEMO
     */
    fun sendCode(mobile: String, code: String): SendSmsResponse {
        logger.info { "发送验证码手机号: ${mobile}, 验证码: ${code}, 签名: ${signName}, 模板: $templateCode" }

        val map = hashMapOf("code" to code)

        val provider = StaticCredentialProvider.create(
            Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build()
        )

        val client: AsyncClient = AsyncClient.builder()
            .region("cn-shanghai") // Region ID
            .credentialsProvider(provider)
            .overrideConfiguration(
                ClientOverrideConfiguration.create()
                    .setEndpointOverride("dysmsapi.aliyuncs.com")
            )
            .build()

        var resp: SendSmsResponse?  // Kotlin中变量需要初始化

        try {
            val sendSmsRequest = SendSmsRequest.builder()
                .signName(signName)
                .templateCode(templateCode)
                .phoneNumbers(mobile)
                .templateParam(JSON.toJSONString(map))
                .build()

            val futureResponse: CompletableFuture<SendSmsResponse> = client.sendSms(sendSmsRequest)
            resp = futureResponse.get() // 同步获取结果

            logger.info { "${"短信发送结果：{}"} ${JSON.toJSONString(resp)}"}
            // logger.info("短信发送结果：${JSON.toJSONString(resp)}")

            // 假设 resp, resp.body, resp.body.code 不会是 null，如果API调用成功到这一步
            // 否则需要进行空安全检查，例如 resp?.body?.code
            if (resp.body.code != "OK" && resp.body.code != "isv.SMS_TEST_NUMBER_LIMIT") {
                throw BusinessException(BusinessExceptionEnum.SMS_CODE_SEND_ERROR)
            }

        } catch (e: Exception) { // Kotlin的异常捕获语法
            logger.error(e) { "发短信异常" }
            throw BusinessException(BusinessExceptionEnum.SMS_CODE_SEND_ERROR)
        } finally {
            client.close()
        }
        // 如果前面有异常抛出，这里不会执行。如果resp可能为null且没有异常抛出（理论上不应该），则需要处理
        return resp ?: throw BusinessException(BusinessExceptionEnum.SMS_CODE_SEND_ERROR)
    }

}