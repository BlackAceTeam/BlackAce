package com.blackace.util.ext

import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Base64
import androidx.core.graphics.drawable.toBitmapOrNull
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipFile

/**
 *
 * @author: magicHeimdall
 * @create: 3/1/2023 2:47 PM
 */
fun PackageInfo.realVersion(): Long {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        longVersionCode
    } else {
        versionCode.toLong()
    }
}

fun Drawable?.base64(): String {
    try {
        val bitmap = this?.toBitmapOrNull() ?: return ""
        val byteArray = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray)
        return "data:image/png;base64," + Base64.encodeToString(byteArray.toByteArray(), Base64.DEFAULT)
    } catch (e: Exception) {
        return ""
    }
}


fun File.libs(): String {
    val zipFile = ZipFile(this)
    val libSet = HashSet<String>()
    val entries = zipFile.entries()
    while (entries.hasMoreElements()) {
        val element = entries.nextElement()
        if (!element.name.startsWith("lib") || element.name.endsWith("/")) {
            continue
        }
        val endIndex = element.name.indexOf('/', 4)
        val arch = element.name.substring(4, endIndex)
        libSet.add(arch)
    }

    return libSet.joinToString(",")

}
