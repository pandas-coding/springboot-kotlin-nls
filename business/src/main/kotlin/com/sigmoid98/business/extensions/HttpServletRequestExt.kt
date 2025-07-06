package com.sigmoid98.business.extensions

import jakarta.servlet.http.HttpServletRequest
import java.nio.charset.StandardCharsets

fun HttpServletRequest.getDecodedParameter(name: String): String =
    String(this.getParameter(name)
        .toByteArray(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)