package com.sigmoid98.business.service.persistance.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.sigmoid98.business.domain.FileTransferSubtitle
import com.sigmoid98.business.mapper.FileTransferSubtitleMapper
import org.springframework.stereotype.Service

@Service
class FileTransferSubtitleServiceImpl : ServiceImpl<FileTransferSubtitleMapper, FileTransferSubtitle>()