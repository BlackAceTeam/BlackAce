package com.blackace.util.holder

import com.blackace.data.RetrofitManager
import com.blackace.data.http.ApiService

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 11:00 PM
 */
object ApiHolder {

    val api by lazy {
        RetrofitManager.create(ApiService::class.java)
    }
}
