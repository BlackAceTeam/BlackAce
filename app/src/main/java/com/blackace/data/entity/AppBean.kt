package com.blackace.data.entity

import android.graphics.drawable.Drawable

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/14-下午9:59
 */
data class AppBean(
    var name: String,
    val pkg: String,
    var version: String,
    var versionCode: String,
    val size: String,
    val source: String,
    val isSplitApk: Boolean,
    val icon: Drawable?
)
