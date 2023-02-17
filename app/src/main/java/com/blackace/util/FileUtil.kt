package com.blackace.util

import java.io.File

/**
 *
 * @author: magicHeimdall
 * @create: 17/2/2023 10:51 AM
 */
object FileUtil {

    fun delete(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                delete(it)
            }
        }
        file.delete()
    }
}
