package com.sigmoid98.business.job

import cn.hutool.core.util.IdUtil
import com.sigmoid98.business.service.FileTransferService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.slf4j.MDC
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DeleteVodJob(
    @Resource private val fileTransferService: FileTransferService,
) {

    companion object {
        private val kLogger = KotlinLogging.logger {  }
    }

    @Scheduled(cron = "0 0 1/5 * * ?")
    fun cron() {
        try {
            // 增加日志流水号
            MDC.put("LOG_ID", IdUtil.getSnowflakeNextIdStr())
            kLogger.info { "删除VOD批处理任务开始" }

            val startTime = System.currentTimeMillis()
            fileTransferService.deleteVodJob()
            kLogger.info { "删除VOD批处理任务结束. 耗时: ${System.currentTimeMillis() - startTime}" }
        } catch (ex: Exception) {
            kLogger.error(ex) { "删除VOD批处理任务异常" }
        }
    }

}