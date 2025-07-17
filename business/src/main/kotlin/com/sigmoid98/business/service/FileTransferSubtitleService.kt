package com.sigmoid98.business.service

import cn.hutool.core.util.IdUtil
import com.alibaba.fastjson2.JSONObject
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.sigmoid98.business.domain.FileTransferSubtitle
import com.sigmoid98.business.mapper.FileTransferSubtitleMapper
import com.sigmoid98.business.service.persistance.impl.FileTransferSubtitleServiceImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class FileTransferSubtitleService(
    @Resource private val fileTransferSubtitleMapper: FileTransferSubtitleMapper,
    @Resource private val fileTransferSubtitleServiceImpl: FileTransferSubtitleServiceImpl,
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

}