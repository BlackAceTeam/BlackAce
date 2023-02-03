package com.blackace.util.ext

import android.util.Log
import com.blackace.BuildConfig

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/14-下午11:42
 */
fun log(any: Any?) {
    if (!BuildConfig.DEBUG) {
        return
    }

    if (any == null) {
        log("Object is Null")
    }
    val callStack = Thread.currentThread().stackTrace[3]
    val out = "[${callStack.methodName}(${callStack.fileName}:${callStack.lineNumber})]  $any"
    Log.e("AceLog",out)
}
