package com.sigmoid98.business.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.aliyun.oss.OSSClient
import com.aliyun.vod.upload.impl.UploadVideoImpl
import com.aliyun.vod.upload.req.UploadVideoRequest
import com.aliyun.vod.upload.resp.UploadVideoResponse
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.http.FormatType
import com.aliyuncs.profile.DefaultProfile
import com.aliyuncs.vod.model.v20170321.*
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class VodUtil(
    @Value("\${vod.accessKeyId}") private val accessKeyId: String,
    @Value("\${vod.accessKeySecret}") private val accessKeySecret: String,
    @Value("\${filetrans.audio.price:0.2}") private val filetransAudioPrice: BigDecimal,
    @Value("\${vod.acsRegionId}") private val vodAcsRegionId: String,
) {

    companion object {
        private val logger = KotlinLogging.logger {} // Logger for SmsCodeService
    }

    /**
     * 使用AK初始化VOD客户端
     * @throws com.aliyuncs.exceptions.ClientException
     */
    fun initVodClient(): DefaultAcsClient {
        // 点播服务接入区域，国内请填cn-shanghai，其他区域请参考文档[点播中心](~~98194~~)
        val profile = DefaultProfile.getProfile("cn-shanghai", accessKeyId, accessKeySecret)
        val client = DefaultAcsClient(profile)
        return client
    }

    /**
     * 获取视频上传地址和凭证
     * @throws com.aliyuncs.exceptions.ClientException
     */
    fun createUploadVideo(vodClient: DefaultAcsClient, uploadFilename: String): CreateUploadVideoResponse {
        return vodClient.getAcsResponse(CreateUploadVideoRequest().apply {
            fileName = uploadFilename
            title = uploadFilename
            sysReadTimeout = 1000
            sysConnectTimeout = 1000
        })
    }

    /**
     * 使用上传凭证和地址初始化OSS客户端（注意需要先Base64解码并Json Decode再传入）
     */
    fun initOssClient(uploadAuth: JSONObject, uploadAddress: JSONObject): OSSClient {
        val endpoint = uploadAddress.getString("Endpoint")
        val accessKeyId = uploadAuth.getString("AccessKeyId")
        val accessKeySecret = uploadAuth.getString("AccessKeySecret")
        val securityToken = uploadAuth.getString("SecurityToken")
        return OSSClient(endpoint, accessKeyId, accessKeySecret, securityToken)
    }

    /**
     * 简单上传
     */
    fun uploadLocalFile(ossClient: OSSClient, uploadAddress: JSONObject, inputStream: InputStream) {
        val bucketName  = uploadAddress.getString("Bucket")
        val objectName = uploadAddress.getString("FileName")
        // 单文件上传
        ossClient.putObject(bucketName, objectName, inputStream)
    }

    /**
     * 上传本地文件
     */
    fun uploadLocalFile(title: String, fileFullPath: String): UploadVideoResponse {
        val request = UploadVideoRequest(accessKeyId, accessKeySecret, title, fileFullPath).apply {
            partSize = 2 * 1024 * 1024
            taskNum = 1
        }

        val uploader = UploadVideoImpl()
        val response = uploader.uploadVideo(request)
        logger.info { "RequestId=${response.requestId}" }
        if (!response.isSuccess) {
            logger.error {
                """
                    视频上传失败:
                    VideoId=${response.videoId}
                    ErrorCode=${response.code}
                    ErrorMessage=${response.message}
                """.trimIndent()
            }
        }
        if (response.isSuccess) {
            logger.info {
                """
                    视频上传成功:
                    VideoId=${response.videoId}
                """.trimIndent()
            }
        }
        return response
    }

    /**
     * 上传本地文件
     */
    fun uploadLocalFile(ossClient: OSSClient, uploadAddress: JSONObject, localFile: String) {
        val bucketName = uploadAddress.getString("Bucket")
        val objectName = uploadAddress.getString("FileName")
        logger.info {
            """
                bucketName: $bucketName
                objectName: $objectName
            """.trimIndent()
        }
        val file = File(localFile)
        // 单文件上传
        ossClient.putObject(bucketName, objectName, file)
    }


    /**
     * 刷新上传凭证
     */
    fun refreshUploadVideo(vodClient: DefaultAcsClient): RefreshUploadVideoResponse {
        val request = RefreshUploadVideoRequest().apply {
            acceptFormat = FormatType.JSON
            videoId = "VideoId"
            sysReadTimeout = 1000
            sysConnectTimeout = 1000
        }
        return vodClient.getAcsResponse(request)
    }

    /**
     * 获取源文件信息
     */
    fun getMezzanineInfo(toGetVideoId: String): GetMezzanineInfoResponse {
        val request = GetMezzanineInfoRequest().apply {
            videoId = toGetVideoId
            authTimeout =  10L
        }

        logger.info { "获取VOD源文件信息请求: ${JSON.toJSONString(request)}" }
        return initVodClient().getAcsResponse(request).also {
            logger.info { "获取VOD源文件信息返回: ${JSON.toJSONString(it)}" }
        }
    }

    /**
     * 获取播放凭证
     */
    fun getVideoPlayAuth(client: DefaultAcsClient, toGetVideoId: String): GetVideoPlayAuthResponse {
        val request = GetVideoPlayAuthRequest().apply {
            videoId = toGetVideoId
        }
        return client.getAcsResponse(request)
    }

    /**
     * 获取视频信息
     */
    fun getVideoInfo(toGetVideoId: String): GetVideoInfoResponse {
        val request = GetVideoInfoRequest().apply {
            videoId = toGetVideoId
        }

        return initVodClient().getAcsResponse(request).also {
            logger.info { "获取视频信息: ${JSON.toJSONString(it)}" }
        }
    }

    /**
     * 查询视频转码摘要信息
     */
    fun getTranscodeSummary(toGetVideoId:  String): GetTranscodeSummaryResponse {
        val request = GetTranscodeSummaryRequest().apply {
            videoIds = toGetVideoId
        }

        return initVodClient().getAcsResponse(request)
    }

    /**
     * 获取辅助媒资上传地址和凭证
     */
    fun getUploadAttachedMediaAuthSrt(fullPath: String): CreateUploadAttachedMediaResponse {
        val file = File(fullPath)

        val request = CreateUploadAttachedMediaRequest().apply {
            title = file.name
            fileName = fullPath
            mediaExt = "src"
            businessType = "subtitle"
        }

        return initVodClient().getAcsResponse(request)
    }

    /**
     * 搜索媒资信息
     * @link https://help.aliyun.com/document_detail/86044.htm?spm=a2c4g.11186623.0.0.7893715cVg2YeH#doc-api-vod-SearchMedia
     */
    fun searchByTitle(title: String): SearchMediaResponse {
        val request = SearchMediaRequest().apply {
            fields = "Title,CoverURL,Status"
            match = "Status in ('Normal', 'Transcoding') and Title = '${title}'"
            pageNo = 1
            pageSize = 1
            sortBy = "CreationTime:Desc"
        }
        return initVodClient().getAcsResponse(request)
    }

    fun searchByCreationTime(start: String, end: String): SearchMediaResponse {
        val request = SearchMediaRequest().apply {
            fields = "Title,CoverURL,Status"
            match = "CreationTime = (${start},${end})"
            pageNo = 1
            pageSize = 10
            sortBy = "CreationTime:desc"
        }
        return initVodClient().getAcsResponse(request)
    }

    /**
     * 删除视频 \
     * 支持传入多个视频ID，多个用逗号分隔
     */
    fun deleteVideo(toDeleteVideoIds: String): DeleteVideoResponse {
        val request = DeleteVideoRequest().apply {
            videoIds = toDeleteVideoIds
        }
        return initVodClient().getAcsResponse(request).also {
            logger.info { "删除视频结果: ${JSON.toJSONString(it)}" }
        }
    }

    /**
     * 上传字幕
     */
    fun uploadSubtitle(subtitleFullPath: String): String {
        val response = runCatching {
            getUploadAttachedMediaAuthSrt(subtitleFullPath)
        }.onSuccess { response ->
            logger.info { "获取辅助媒资地址和凭证: ${JSON.toJSONString(response)}" }
        }.getOrElse { ex ->
            throw BusinessException(BusinessExceptionEnum.GEN_SUBTITLE_ERROR)
        }

        runCatching {
            val uploadAuth = decodeBase64(response.uploadAuth)
            val uploadAddress = decodeBase64(response.uploadAddress)
            val ossClient = initOssClient(uploadAuth, uploadAddress)
            uploadLocalFile(ossClient, uploadAddress, subtitleFullPath)
        }.getOrElse {
            throw BusinessException(BusinessExceptionEnum.GEN_SUBTITLE_ERROR)
        }

        return response.fileURL
    }

    /**
     * base64解码
     */
    fun decodeBase64(uploadAuth: String): JSONObject =
        JSONObject.parseObject(Base64.decodeBase64(uploadAuth), JSONObject::class.java)

    /**
     * 计算音频转字幕金额
     */
    fun calcAmount(videoId: String): BigDecimal {
        val videoInfo = runCatching {
            getVideoInfo(videoId)
        }.getOrElse { ex ->
            logger.error(ex) { "计算音频转字幕应收金额异常" }
            throw BusinessException(BusinessExceptionEnum.FILETRANS_CAL_AMOUNT_ERROR)
        }

        val durationInSeconds = videoInfo.video.duration
        logger.info { "视频: ${videoId}, 时长: ${durationInSeconds}, 单价: $filetransAudioPrice" }
        if (durationInSeconds <= 0f) {
            throw BusinessException(BusinessExceptionEnum.FILETRANS_CAL_AMOUNT_ERROR)
        }

        return BigDecimal(durationInSeconds.toString())
            .multiply(filetransAudioPrice)
            .divide(60.toBigDecimal(), 2, RoundingMode.HALF_UP)
            .let { amount ->
                if (amount.compareTo(BigDecimal.ZERO) == 0) {
                    BigDecimal("0.01")
                } else {
                    amount
                }
            }
    }

}