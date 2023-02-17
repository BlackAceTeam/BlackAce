package com.blackace.data.config

import android.provider.Settings
import com.blackace.data.entity.UserBean
import com.blackace.data.entity.http.ConfigBean
import com.blackace.util.holder.ContextHolder
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

    fun getSystemConfig(): ConfigBean? {
        val configData = mmkv.decodeString("system_config")
        return if (configData.isNullOrEmpty()) {
            return null
        } else {
            return GsonHolder.fromJson(configData)
        }
    }

    fun saveSystemConfig(bean: ConfigBean?) {
        if (bean == null) {
            return
        }
        mmkv.encode("system_config", GsonHolder.toJson(bean))
    }

    fun getDeviceCode():String{
        val deviceCode = mmkv.decodeString("device_code")
        if (deviceCode.isNullOrEmpty()) {
            val androidID = Settings.System.getString(
                ContextHolder.get().contentResolver, Settings.Secure.ANDROID_ID
            )
            mmkv.encode("device_code",androidID)
            return androidID
        }

        return deviceCode
    }
}
