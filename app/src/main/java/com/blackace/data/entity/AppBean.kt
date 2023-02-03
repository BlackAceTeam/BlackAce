package com.blackace.data.entity

import android.graphics.drawable.Drawable

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/14-下午9:59
 */
data class AppBean(
    val name: String,
    val pkg: String,
    val version: String,
    val versionCode: String,
    val size: String,
    val source: String,
    val icon: Drawable?
)
