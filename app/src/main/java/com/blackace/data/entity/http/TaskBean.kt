package com.blackace.data.entity.http

import com.google.gson.annotations.SerializedName

/**
 *
 * @author: magicHeimdall
 * @create: 4/1/2023 9:31 PM
 */
data class TaskBean(
    val apkName: String,
    val apkPkg: String,
    val apkSize: Int,
    @SerializedName("apkVer")
    val versionCode: Long,
    @SerializedName("apkVerName")
    val versionName: String,
    val clientTaskNo: String,
    val createTime: Long,
    @SerializedName("descr")
    val desc: String,
    val downloadUrl: String,
    val feature: String,
    val hide: Int,
    val icon: String,
    val id: Int,
    val libs: String,
    val md5: String,
    var status: Int,
    val taskNo: String,
    val type: Int,
    val userId: Int
) {
    companion object {
        const val STATE_WAIT = 0
        const val STATE_LOADING = 1
        const val STATE_SUCCESS = 2
        const val STATE_FAIL = -1
        const val STATE_APK_DELETE = 3
    }
}
