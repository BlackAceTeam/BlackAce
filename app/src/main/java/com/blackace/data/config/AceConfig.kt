package com.blackace.data.config

import com.blackace.data.entity.UserBean
import com.blackace.util.holder.GsonHolder
import com.tencent.mmkv.MMKV

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16-下午2:55
 */
object AceConfig {

    val mmkv = MMKV.defaultMMKV()

    fun getUser(): UserBean? {
        val userData = mmkv.decodeString("user_bean")
        return if (userData.isNullOrEmpty()) {
            null
        } else {
            GsonHolder.fromJson(userData)
        }
    }

    fun saveUser(bean: UserBean?) {
        if (bean == null) {
            mmkv.removeValueForKey("user_bean")
        } else {
            mmkv.encode("user_bean", GsonHolder.toJson(bean))
        }
    }
}
