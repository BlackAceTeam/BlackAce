package com.blackace.util.ext

import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author: magicHeimdall
 * @create: 8/2/2023 5:24 PM
 */
fun Date.str(format: String = "yyyy-MM-dd-hh:mm:ss"): String {
    val simpleFormatter = SimpleDateFormat(format, Locale.getDefault())
    return simpleFormatter.format(this)
}

fun nowStr(): String {
    val date = Date()
    return date.str()
}

fun Long.date(format:String = "yyyy-MM-dd-hh:mm:ss"): String {
    val date = Date(this)
    return date.str(format)
}
