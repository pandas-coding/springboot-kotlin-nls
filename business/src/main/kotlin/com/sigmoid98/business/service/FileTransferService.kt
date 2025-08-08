package com.sigmoid98.business.service

import cn.hutool.core.util.IdUtil
import com.alibaba.fastjson2.JSONObject
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.sigmoid98.business.context.LoginMemberContext
import com.sigmoid98.business.converter.FileTransferConverter
import com.sigmoid98.business.domain.FileTransfer
import com.sigmoid98.business.enums.FileTransferPayStatusEnum
import com.sigmoid98.business.enums.FileTransferStatusEnum
import com.sigmoid98.business.enums.OrderInfoOrderTypeEnum
import com.sigmoid98.business.exception.BusinessException
import com.sigmoid98.business.exception.BusinessExceptionEnum
import com.sigmoid98.business.extensions.list
import com.sigmoid98.business.extensions.mapRecords
import com.sigmoid98.business.mapper.FileTransferMapper
import com.sigmoid98.business.nls.NlsUtil
import com.sigmoid98.business.req.FileTransferPayReq
import com.sigmoid98.business.req.FileTransferQueryReq
import com.sigmoid98.business.req.OrderInfoPayReq
import com.sigmoid98.business.resp.FileTransferQueryResp
import com.sigmoid98.business.resp.OrderInfoPayResp
import com.sigmoid98.business.resp.PageResp
import com.sigmoid98.business.util.VodUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalUnit

@Service
class FileTransferService(
    @Resource private val fileTransferMapper: FileTransferMapper,
    @Resource private val vodUtil: VodUtil,
    @Resource private val loginMemberContext: LoginMemberContext,
    @Resource private val orderInfoService: OrderInfoService,
    @Resource private val nlsUtil: NlsUtil,
    @Resource private val fileTransferConverter: FileTransferConverter,
    @Resource private val fileTransferSubtitleService: FileTransferSubtitleService,
) {
    companion object {
        private val logger = KotlinLogging.logger {} // Logger

        private const val ALIYUN_TRANS_SUCCESS_CODE = "21050000"
    }

    fun pay(req: FileTransferPayReq): OrderInfoPayResp {
        if (req.amount == null) {
            throw Exception("pay amount cannot be null")
        }

        val now = LocalDateTime.now()

        // get video info
        val videoInfo = vodUtil.getVideoInfo(req.vod)
        val duration = videoInfo.video.duration
        logger.info { "计算视频费用, 视频: ${req.vod}, 时长: $duration" }
        val seconds = duration.toInt()
        val toSaveId = IdUtil.getSnowflakeNextId()

        val fileTransfer = FileTransfer().apply {
            id = toSaveId
            memberId = loginMemberContext.id
            name = req.name
            second = seconds
            amount = req.amount
            audio = req.audio
            fileSign = req.fileSign
            payStatus = FileTransferPayStatusEnum.I.code
            status = FileTransferStatusEnum.INIT.code
            lang = req.lang
            vod = req.vod
            createdAt = now
            updatedAt = now
        }
        fileTransferMapper.insert(fileTransfer)

        // 保存订单信息
        // 订单表的info关联的是file_transfer的id
        val orderInfoPayReq = OrderInfoPayReq(
            orderType = OrderInfoOrderTypeEnum.FILE_TRANSFER_PAY.code,
            info = toSaveId.toString(),
            amount = req.amount,
            channel = req.channel!!,
            desc = "语音识别付费",
        )
        return orderInfoService.pay(orderInfoPayReq)
    }

    /**
     * 支付成功后处理
     */
    fun afterPaySuccess(id: Long) {
        val now = LocalDateTime.now()

        // 更新语音识别支付和初始状态
        val updateFileTransferSuccess = KtUpdateChainWrapper(fileTransferMapper, FileTransfer())
            .set(FileTransfer::payStatus, FileTransferPayStatusEnum.S.code)
            .set(FileTransfer::status, FileTransferStatusEnum.SUBTITLE_INIT.code)
            .set(FileTransfer::updatedAt, now)
            .eq(FileTransfer::id, id)
            .update()
        if (!updateFileTransferSuccess) {
            logger.error { "更新语音识别支付和初始状态失败, 停止发起语音识别任务" }
            return
        }

        logger.info { "发起语音识别任务" }
        val savedFileTransfer = fileTransferMapper.selectById(id)
            ?: throw BusinessException(BusinessExceptionEnum.FILETRANS_NOT_FOUNT)

        val commonResponse = nlsUtil.transfer(
            fileLink = savedFileTransfer.audio!!,
            appKey = savedFileTransfer.lang!!,
        )

        when (commonResponse.httpStatus) {
            200 -> {
                val result = JSONObject.parseObject(commonResponse.data)
                val statusCode = result.getInteger("StatusCode")
                val statusText = result.getString("StatusText")
                val taskId = result.getString("TaskId")

                when (statusText) {
                    "SUCCESS" -> {
                        logger.info { "录音文件识别请求成功响应: ${result.toJSONString()}" }

                        // 5. 再次更新数据库，记录任务ID和状态
                        logger.info { "更新语音识别状态为: 生成字幕中" }
                        val updateSucceed = KtUpdateChainWrapper(fileTransferMapper, FileTransfer())
                            .eq(FileTransfer::id, id)
                            .set(FileTransfer::status, FileTransferStatusEnum.SUBTITLE_PENDING.code)
                            .set(FileTransfer::updatedAt, now)
                            .set(FileTransfer::taskId, taskId)
                            .set(FileTransfer::transTime, now)
                            .set(FileTransfer::transStatusCode, statusCode)
                            .set(FileTransfer::transStatusText, statusText)
                            .update()
                        if (!updateSucceed) {
                            logger.error { "更新fileTrans失败" }
                        }
                    }
                    else -> {
                        logger.error { "录音文件识别请求失败: ${result.toJSONString()}" }
                        throw BusinessException(BusinessExceptionEnum.FILETRANS_TRANS_ERROR)
                    }
                }
            }
            else -> logger.warn { "语音识别任务响应失败, 响应: $commonResponse" }
        }


    }

    fun afterTransfer(jsonResult: JSONObject) {
        val now = LocalDateTime.now()

        val taskId = jsonResult.getString("TaskId")
        val statusCode = jsonResult.getInteger("StatusCode")
        val statusText = jsonResult.getString("StatusText")

        val isSucceed = statusCode?.toString() == ALIYUN_TRANS_SUCCESS_CODE

        val updateFileTransferSucceed =
            KtUpdateChainWrapper(baseMapper = fileTransferMapper, entity = FileTransfer())
                .set(FileTransfer::updatedAt, now)
                .set(FileTransfer::transStatusCode, statusCode)
                .set(FileTransfer::transStatusText, statusText)
                .also {
                    when (isSucceed) {
                        true -> {
                            val instant = Instant.ofEpochMilli(jsonResult.getLong("SolveTime"))
                            val solveTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                            // val solveTime = LocalDateTime.parse(jsonResult.getString("SolveTime"))
                            it.set(FileTransfer::solveTime, solveTime)
                            it.set(FileTransfer::status, FileTransferStatusEnum.SUBTITLE_SUCCESS.code)
                        }
                        false -> {
                            it.set(FileTransfer::status, FileTransferStatusEnum.SUBTITLE_FAILURE.code)
                        }
                    }
                }
                .eq(FileTransfer::taskId, taskId)
                .eq(FileTransfer::status, FileTransferStatusEnum.SUBTITLE_PENDING.code)
                .update()
        if (!updateFileTransferSucceed) {
            logger.info { "更新失败, 不保存字幕表: taskId=${taskId}, 状态=${FileTransferStatusEnum.SUBTITLE_PENDING.code}/${FileTransferStatusEnum.SUBTITLE_PENDING.code}" }
            return
        }

        when (statusCode.toString()) {
            "21050000" -> {
                val list = KtQueryChainWrapper(fileTransferMapper, FileTransfer())
                    .eq(FileTransfer::taskId, taskId)
                    .list()
                if (list.isNullOrEmpty()) {
                    logger.error { "无法查找到指定task的FileTransfer记录, taskId: $taskId" }
                    throw Exception("无法查找到")
                }

                // 保存字幕表
                val savedFileTransfer = list.first()
                val result = jsonResult.getJSONObject("Result")
                fileTransferSubtitleService.saveSubtitle(savedFileTransfer.id!!, result)
            }
        }
    }

    /**
     * 查询语音识别任务
     */
    fun query(req: FileTransferQueryReq): PageResp<FileTransferQueryResp> {
        val page = Page<FileTransfer>(
            req.pagination.page.toLong(),
            req.pagination.size.toLong(),
        )

        val pagedList = KtQueryChainWrapper(fileTransferMapper, FileTransfer())
            .also {
                when {
                    req.memberId != null -> it.eq(FileTransfer::memberId, req.memberId)
                    !req.lang.isNullOrEmpty() -> it.eq(FileTransfer::lang, req.lang)
                    !req.status.isNullOrEmpty() -> it.eq(FileTransfer::status, req.status)
                    !req.name.isNullOrEmpty() -> it.like(FileTransfer::name, "%${req.name}%")
                }
            }
            .orderByDesc(FileTransfer::id)
            .page(page)
        val dtoPage = pagedList.mapRecords(fileTransferConverter::toDto)

        return PageResp(
            total = dtoPage.total,
            list = dtoPage.list,
            pageNum = dtoPage.current.toInt(),
            pageSize = dtoPage.size.toInt(),
            pages = dtoPage.pages,
        )
    }

    /**
     * 删除过期的VOD任务
     */
    fun deleteVodJob() {
        // 使用 runCatching 来优雅地处理顶层异常，替代外层 try-catch
        runCatching {
            // 1. 使用 java.time.LocalDate 和 DateTimeFormatter 处理日期
            // 这种方式类型安全、不可变，是现代Java/Kotlin推荐的做法
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            // 2. 计算并格式化日期字符串
            // 使用 minusDays 方法更具可读性，字符串模板让拼接更简洁
            val startStr = "${today.minusDays(30).format(formatter)}T00:00:00Z"
            val endStr = "${today.minusDays(15).format(formatter)}T00:00:00Z"

            logger.info { "删除过期VOD，查询列表日期范围：$startStr, $endStr" }

            // 3. 查询媒体列表
            // 使用 ?. 安全调用操作符，如果 searchByCreationTime 返回 null，整个表达式会返回 null
            // Kotlin会自动将 getMediaList() 转换为 mediaList 属性访问
            val mediaList = vodUtil.searchByCreationTime(startStr, endStr).mediaList

            // 4. 遍历并删除媒体
            // 使用 forEach 扩展函数配合安全调用 ?.
            // 如果 mediaList 为 null 或为空，循环体不会执行，代码更简洁
            mediaList?.forEach { media ->
                // 同样使用 runCatching 处理循环内部的单个删除异常
                runCatching {
                    // 将 getMediaId() 转换为 mediaId 属性访问
                    vodUtil.deleteVideo(media.mediaId)
                    logger.info { "成功删除VOD: ${media.mediaId}" } // 建议增加成功日志
                }.onFailure { e ->
                    // onFailure 块只在发生异常时执行
                    logger.error(e) { "删除过期VOD异常: ${media.mediaId}" }
                }
            }

        }.onFailure { e ->
            // 当顶层 runCatching 块中出现任何异常时，会在此处捕获
            logger.error(e) { "删除过期VOD任务执行异常" }
        }
    }
}