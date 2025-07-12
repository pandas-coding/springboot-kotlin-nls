package com.sigmoid98.business.nls

import com.alibaba.fastjson2.JSONObject
import com.aliyuncs.CommonRequest
import com.aliyuncs.CommonResponse
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.IAcsClient
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.sigmoid98.business.enums.FileTransferLangEnum
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Component

@Component
class NlsUtil(
    @Resource private val nlsFileTransferProperties: NlsFileTransferProperties,
) {

    companion object {
        private val logger = KotlinLogging.logger {  }
    }

    /**
     * 发送识别请求
     */
    fun transfer(fileLink: String, appKey: String): CommonResponse {
        val client = runCatching { getClient() }
            .getOrElse { ex ->
                logger.error(ex) { "初始化NLS client失败" }
                throw BusinessException(BusinessExceptionEnum.FILETRANS_TRANS_ERROR)
            }

        // 设置请求参数
        val postRequest = CommonRequest().apply {
            sysMethod = MethodType.POST
            sysDomain = nlsFileTransferProperties.domain
            sysVersion = nlsFileTransferProperties.version
            sysAction = "SubmitTask"
            sysProduct = nlsFileTransferProperties.product
        }

        val taskObject = JSONObject().apply {
            // put("appkey", appKey)
            // put("file_link", fileLink)
            // put("version", nlsFileTransferProperties.taskVersion)
            put("file_link", fileLink)
            put("appkey", appKey)
            put("version", nlsFileTransferProperties.version)
            // 是否将大于16kHz采样率的音频进行自动降采样，默认为false，开启时需要设置version为“4.0”。
            put("enable_sample_rate_adaptive", true)
            // 允许单句话最大结束时间，秒 转 毫秒
            put("max_single_segment_time", 15 * 1000)

            val enableItn = when (appKey) {
                FileTransferLangEnum.LANG0.code,
                FileTransferLangEnum.LANG1.code,
                FileTransferLangEnum.LANG2.code -> true
                else -> false
            }
            put("enable_inverse_text_normalization", enableItn)

            // 暂不开启智能分轨，出现过SAMPLERATE_16K_SPKDIAR_NOT_SUPPORTED 16k音频不支持智能分轨
            put("auto_split", "false")
            put("enable_callback", true)
            put("callback_url", nlsFileTransferProperties.callback)
        }

        postRequest.putBodyParameter("Task", taskObject.toJSONString())
        logger.info { "发送识别请求参数：${JSONObject.toJSONString(postRequest)}" }
        return runCatching { client.getCommonResponse(postRequest) }
            .onSuccess { response ->
                logger.info { "发送识别请求结果：${JSONObject.toJSONString(response)}" }
            }
            .getOrElse { ex ->
                logger.error(ex) { "获取识别请求响应失败" }
                throw BusinessException(BusinessExceptionEnum.FILETRANS_TRANS_ERROR)
            }
    }

    /**
     * 查询识别任务状态
     */
    fun query(taskId: String): CommonResponse {
        val client = runCatching { getClient() }
            .getOrElse { ex ->
                logger.error(ex) { "初始化NLS client失败" }
                throw BusinessException(BusinessExceptionEnum.FILETRANS_TRANS_ERROR)
            }

        val getRequest = CommonRequest().apply {
            sysDomain = nlsFileTransferProperties.domain
            sysVersion = nlsFileTransferProperties.version
            sysAction = "GetTaskResult"
            sysProduct = nlsFileTransferProperties.product
            sysMethod = MethodType.GET
            putQueryParameter("TaskId", taskId)
        }

        logger.info { "查询识别结果请求参数: ${JSONObject.toJSONString(getRequest)}" }

        return runCatching { client.getCommonResponse(getRequest) }
            .onSuccess { response ->
                logger.info { "查询识别结果返回结果: ${JSONObject.toJSONString(response)}" }
            }
            .getOrElse { ex ->
                logger.error(ex) { "查询识别结果异常:" }
                throw BusinessException(BusinessExceptionEnum.FILETRANS_TRANS_ERROR)
            }

    }

    /**
     * 获取client对象
     * @throws com.aliyuncs.exceptions.ClientException
     */
    fun getClient(): IAcsClient {
        val regionId = nlsFileTransferProperties.regionId
        val endPointName = nlsFileTransferProperties.endpointName
        val product = nlsFileTransferProperties.product
        val domain = nlsFileTransferProperties.domain

        DefaultProfile.addEndpoint(endPointName, regionId, product, domain)
        val clientProfile = DefaultProfile.getProfile(
            regionId,
            nlsFileTransferProperties.accessKeyId,
            nlsFileTransferProperties.accessKeySecret,
        )
        return DefaultAcsClient(clientProfile)
    }
}