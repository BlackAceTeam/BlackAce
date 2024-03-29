package com.blackace.util.ext

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/14-下午11:29
 */

const val B = 1.0

const val KB = B * 1024

const val MB = KB * 1024

const val GB = MB * 1024

fun File.size(): String {
    val size = this.length()
    val sizeStr = when {
        size > GB -> {
            formatSize(size / GB, "G")
        }
        size > MB -> {
            formatSize(size / MB, "M")
        }
        size > KB -> {
            formatSize(size / KB, "K")
        }
        else -> {
            formatSize(size.toDouble(), "B")
        }
    }

    return sizeStr
}

fun File.md5(): String {
    return try {
        val digest = MessageDigest.getInstance("MD5")
        val input = BufferedInputStream(FileInputStream(this))
        val bytes = ByteArray(1024)
        var len = input.read(bytes)
        while (len != -1) {
            digest.update(bytes, 0, len)
            len = input.read(bytes)
        }

        val result = digest.digest()
        byteToString(result)
    } catch (e: Exception) {
        ""
    }
}



fun formatSize(size: Double, end: String): String {
    return String.format("%.02f%s", size, end)
}
