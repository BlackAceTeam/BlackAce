package com.blackace.data.entity.http

import com.google.gson.annotations.SerializedName

/**
 *
 * @author: magicHeimdall
 * @create: 19/12/2022 3:21 PM
 */
data class FeatureBean(
    @SerializedName("descr")
    val desc: String,
    @SerializedName("featureKey")
    val key: String,

    val title: String,
) {
    var isCheck: Boolean = false
}
