package com.sigmoid98.business.enums

enum class FileTransferStatusEnum(
    private val code: String,
    private val desc: String,
) {
    INIT("I", "初始"),
    SUBTITLE_INIT("SI", "待生成字幕"),
    SUBTITLE_PENDING("SP", "生成字幕中"),
    SUBTITLE_SUCCESS("SS", "生成字幕成功"),
    SUBTITLE_FAILURE("SF", "生成字幕失败"),
    DELETED("D", "失效"),
    ;


    companion object {
        // 预先构建一个 code 到 枚举实例的映射，提高查找效率
        private val codeToEnumMap = entries.associateBy { it.code }

        /**
         * 根据 code 获取枚举实例
         */
        fun fromCode(code: String): FileTransferStatusEnum? {
            return codeToEnumMap[code]
        }
    }
}