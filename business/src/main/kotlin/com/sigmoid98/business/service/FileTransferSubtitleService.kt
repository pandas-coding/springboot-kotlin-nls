package com.sigmoid98.business.service

import cn.hutool.core.util.IdUtil
import com.alibaba.fastjson2.JSONObject
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.sigmoid98.business.converter.FileTransferSubtitleConverter
import com.sigmoid98.business.domain.FileTransferSubtitle
import com.sigmoid98.business.extensions.list
import com.sigmoid98.business.extensions.mapRecords
import com.sigmoid98.business.mapper.FileTransferSubtitleMapper
import com.sigmoid98.business.req.FileTransferSubtitleQueryReq
import com.sigmoid98.business.req.GenSubtitleReq
import com.sigmoid98.business.resp.FileTransferSubtitleQueryResp
import com.sigmoid98.business.resp.PageResp
import com.sigmoid98.business.service.persistance.impl.FileTransferSubtitleServiceImpl
import com.sigmoid98.business.util.VodUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class FileTransferSubtitleService(
    @Value("\${temp.dir}") private val tempDir: String,
    @Resource private val fileTransferSubtitleMapper: FileTransferSubtitleMapper,
    @Resource private val fileTransferSubtitleServiceImpl: FileTransferSubtitleServiceImpl,
    @Resource private val fileTransferSubtitleConverter: FileTransferSubtitleConverter,
    @Resource private val vodUtil: VodUtil,
) {
    companion object {
        private val kLogger = KotlinLogging.logger {  }
    }

    @Transactional
    fun saveSubtitle(relayFileTransferId: Long, result: JSONObject) {
        val now = LocalDateTime.now()

        // 先清空已有的字幕记录防止重复记录
        val queryWrapper = KtQueryChainWrapper(fileTransferSubtitleMapper, FileTransferSubtitle())
            .eq(FileTransferSubtitle::fileTransferId, relayFileTransferId)
        fileTransferSubtitleMapper.delete(queryWrapper)

        val sentences = result.getJSONArray("Sentences")


        val subtitleList = sentences.mapIndexed { index, item ->
            val sentence = item as JSONObject
            val beginTime = sentence.getInteger("BeginTime")
            val endTime = sentence.getInteger("EndTime")
            val text = sentence.getString("Text")

            FileTransferSubtitle().apply {
                this.id = IdUtil.getSnowflakeNextId()
                this.fileTransferId = relayFileTransferId
                this.index = index + 1
                this.begin = beginTime
                this.end = endTime
                this.text = text
                this.createdAt = now
                this.updatedAt = now
            }
        }
        // 批量导入
        fileTransferSubtitleServiceImpl.saveBatch(subtitleList)
    }

    /**
     * 查询字幕信息
     */
    fun query(req: FileTransferSubtitleQueryReq): PageResp<FileTransferSubtitleQueryResp> {
        val page = Page<FileTransferSubtitle>(
            req.pagination.page.toLong(),
            req.pagination.size.toLong(),
        )

        val listPage = KtQueryChainWrapper(fileTransferSubtitleMapper, FileTransferSubtitle())
            .eq(FileTransferSubtitle::fileTransferId, req.fileTransferId)
            .orderByAsc(FileTransferSubtitle::index)
            .page(page)

        val dtoPage = listPage.mapRecords(fileTransferSubtitleConverter::toDto)
        return PageResp(
            total = dtoPage.total,
            list = dtoPage.list,
            pageNum = dtoPage.current.toInt(),
            pageSize = dtoPage.size.toInt(),
            pages = dtoPage.pages,
        )
    }

    /**
     * 生成字幕
     */
    fun genSubtitle(req: GenSubtitleReq): String {
        val fileTransferId = req.fileTransferId

        kLogger.info { "开始获取字幕" }
        val fileTransferSubtitleList = KtQueryChainWrapper(fileTransferSubtitleMapper, FileTransferSubtitle())
            .eq(FileTransferSubtitle::fileTransferId, fileTransferId)
            .list()

        kLogger.info { "格式化字幕文本" }
        val buffer = formatSubtitle(fileTransferSubtitleList)

        val fileSuffix = ".srt"
        return uploadFile(fileTransferId, buffer, fileSuffix)
    }

    /**
     * 上传文件内容到VOD并返回URL
     */
    fun uploadFile(fileTransferId: Long, buffer: StringBuffer, fillSuffix: String): String {
        val tempFile = File(tempDir, "$fileTransferId$fillSuffix")

        try {
            kLogger.info { "生成临时文件: ${tempFile.absolutePath}" }
            // 确保目录存在（mkdirs会创建所有不存在的父目录，更安全）
            File(tempDir).mkdirs()

            // 4. 使用Kotlin的扩展函数写入文件，并明确指定UTF-8编码
            tempFile.writeText(buffer.toString(), StandardCharsets.UTF_8)

            // 上传文件
            val url = vodUtil.uploadSubtitle(tempFile.absolutePath)
            kLogger.info { "上传临时文件成功：${url}" }

            return url
        } finally {
            if (tempFile.exists()) {
                kLogger.info { "删除临时文件: ${tempFile.absolutePath}" }
                tempFile.delete()
            }
        }
    }

    /**
     * 格式化字幕
     */
    fun formatSubtitle(list: List<FileTransferSubtitle>): StringBuffer {
        kLogger.info { "拼接字幕数据, 总行数: ${list.size}" }

        return StringBuffer().apply {
            val buffer = this
            list.forEachIndexed { index, subtitle ->
                buffer.appendLine(index.toString())

                val beginTime = convertMs(subtitle.begin?.toLong() ?: 0)
                val endTime = convertMs(subtitle.end?.toLong() ?: 0)
                buffer.appendLine("$beginTime --> $endTime")

                buffer.appendLine(subtitle.text)
                buffer.appendLine()
            }
        }.also { it ->
            kLogger.info { "拼接字幕完成, 字符数: ${it.length}" }
        }
    }

    fun convertMs(ms: Long): String {
        val instant = Instant.ofEpochMilli(ms)
        val zoneInGMTS = ZoneId.of("GMT+8")
        val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss,SSS")
        return instant.atZone(zoneInGMTS).format(dateTimeFormatter)
    }

    /**
     * 格式化字幕纯文本
     */
    fun formatText(list: List<FileTransferSubtitle>): String {
        kLogger.info { "拼接纯文本数据, 总行数: ${list.size}" }

        return buildString {
            list.forEach { item ->
                appendLine(item.text)
            }
        }.also {
            kLogger.info { "拼接纯文本完成, 字符数: ${it.length}" }
        }
    }
}