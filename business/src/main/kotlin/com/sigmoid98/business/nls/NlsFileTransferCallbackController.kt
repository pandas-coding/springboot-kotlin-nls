package com.sigmoid98.business.nls

import com.alibaba.fastjson2.JSONObject
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import com.sigmoid98.business.service.FileTransferService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/file-transfer")
class NlsFileTransferCallbackController(
    @Resource private val fileTransferService: FileTransferService,
) {
    companion object {
        private val logger = KotlinLogging.logger {  }

        private val PATTERN_SERVER_ERR = Regex("5105[0-9]*")
        private val PATTERN_CLIENT_ERR = Regex("4105[0-9]*")
    }

    @PostMapping("/callback")
    fun callback(request: HttpServletRequest) {

        val result = runCatching {
            request.inputStream.readBytes().toString(Charsets.UTF_8)
        }.getOrElse { exception ->
            logger.error(exception) { "录音转换回调处理异常!" }
            throw BusinessException(BusinessExceptionEnum.FILETRANS_TRANS_ERROR)
        }

        logger.info { "录音转换回调结果: $result" }

        val jsonResult = JSONObject.parseObject(result)
        val taskId = jsonResult.getString("TaskId")
        val statusCode = jsonResult.getString("StatusCode")
        val statusText = jsonResult.getString("StatusText")
        logger.info { "录音转换回调结果: TaskId：${taskId}，StatusCode：${statusCode}，StatusText：${statusText}" }

        // 不管成功还是失败，都应该更新状态
        fileTransferService.afterTransfer(jsonResult)

        when {
            statusCode == "21050000" -> {
                logger.info { "录音文件识别成功！taskId: $taskId" }

                // System.out.println("RequestTime: " + jsonResult.getString("RequestTime"));
                // System.out.println("SolveTime: " + jsonResult.getString("SolveTime"));
                // System.out.println("BizDuration: " + jsonResult.getString("BizDuration"));
                // System.out.println("Result.Sentences.size: " +
                //     jsonResult.getJSONObject("Result").getJSONArray("Sentences").size());
                // for (int i = 0; i < jsonResult.getJSONObject("Result").getJSONArray("Sentences").size(); i++) {
                //     System.out.println("Result.Sentences[" + i + "].BeginTime: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("BeginTime"));
                //     System.out.println("Result.Sentences[" + i + "].EndTime: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("EndTime"));
                //     System.out.println("Result.Sentences[" + i + "].SilenceDuration: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("SilenceDuration"));
                //     System.out.println("Result.Sentences[" + i + "].Text: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("Text"));
                //     System.out.println("Result.Sentences[" + i + "].ChannelId: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("ChannelId"));
                //     System.out.println("Result.Sentences[" + i + "].SpeechRate: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("SpeechRate"));
                //     System.out.println("Result.Sentences[" + i + "].EmotionValue: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("EmotionValue"));
                // }
            }
            PATTERN_CLIENT_ERR.matches(statusCode) -> {
                logger.error { "录音文件识别失败！状态码以4开头表示客户端错误: StatusCode: $statusCode, StatusText: $statusText" }
            }
            PATTERN_SERVER_ERR.matches(statusCode) -> {
                logger.error { "录音文件识别失败！状态码以5开头表示服务端错误：StatusCode: $statusCode, StatusText: $statusText" }
            }
            else -> {
                logger.error { "录音文件识别失败！状态码: StatusCode: $statusCode, StatusText: $statusText" }
            }
        }
    }
}