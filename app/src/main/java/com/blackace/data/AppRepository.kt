package com.blackace.data

import android.content.pm.PackageInfo
import com.blackace.BuildConfig
import com.blackace.R
import com.blackace.data.config.AceConfig
import com.blackace.data.entity.UserBean
import com.blackace.data.entity.http.VersionBean
import com.blackace.data.entity.http.BaseResult
import com.blackace.data.entity.http.ConfigBean
import com.blackace.data.entity.http.EmailBean
import com.blackace.util.FileUtil
import com.blackace.util.ext.getString
import com.blackace.util.ext.log
import com.blackace.util.holder.ApiHolder
import com.blackace.util.holder.ContextHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 9:02 下午
 */
object AppRepository {

    suspend fun login(username: String, password: String): String? {
        try {
            val result = ApiHolder.api.login(username, password)
            if (result.isSuccess() && result.result != null) {
                val data = result.result!!
                val userBean = UserBean(data.account, data.email, data.token, data.registerTime)
                AceConfig.saveUser(userBean)
                return null
            }

            return result.msg
        } catch (e: Exception) {
            return e.message
        }

    }

    suspend fun register(username: String, email: String, password: String): String? {
        try {
            val result = ApiHolder.api.register(username, password, email)
            if (result.isSuccess() && result.result != null) {
                val data = result.result!!
                val userBean = UserBean(data.account, data.email, data.token, data.registerTime)
                AceConfig.saveUser(userBean)
                return null
            }

            return result.msg
        } catch (e: Exception) {
            return e.message
        }
    }

    suspend fun freshUser(): UserBean? {
        var userBean: UserBean? = null
        runCatching {
            val bean = ApiHolder.api.freshUser()
            val result = bean.result
            if (bean.isSuccess() && result != null) {
                userBean = UserBean(result.account, result.email, result.token, result.registerTime)
            }
        }
        AceConfig.saveUser(userBean)
        return userBean
    }

    suspend fun loadSystemConfig() {
        runCatching {
            val bean = ApiHolder.api.systemConfig()
        }
    }

    suspend fun changePassword(verify: String, newPass: String, account: String): String? {
        try {
            val result = ApiHolder.api.changePassword(verify, newPass, account)
            if (result.isSuccess()) {
                return null
            }
            return result.msg
        } catch (e: Exception) {
            return e.message
        }
    }

    suspend fun sendEmailVerify(account: String): BaseResult<EmailBean> {
        return try {
            ApiHolder.api.sendEmailVerify(account)
        } catch (e: Exception) {
            BaseResult.fail(e.message.toString())
        }
    }


    /**
     * 检查更新
     * @param ignore Boolean
     * @return Pair<VersionInfo?, String>
     */
    suspend fun checkUpdate(ignore: Boolean): Pair<VersionBean?, String> {

        var msg = ""
        var versionInfo: VersionBean? = null

        try {
            val info = ApiHolder.api.checkUpdate()
            log(info)
//            val result = VersionInfo("2.0.0", 11, "https://dl.coolapk.com/down?pn=com.coolapk.market&id=NDU5OQ&h=46bb9d98&from=from-web", "1.新增xxx", 2)
            val result = info.result
            //请求是否成功
            if (info.isSuccess() && result != null) {
                //获取到的版本是否大于当前版本
                if (result.version > BuildConfig.VERSION_CODE) {
                    versionInfo = result
                } else {
                    msg = getString(R.string.version_is_newest)
                }

            } else {
                msg = getString(R.string.version_is_newest)
            }

        } catch (e: Exception) {
            msg = getString(R.string.network_error) + e.message
        }

        return versionInfo to msg

    }

    /**
     * 使用DownloadManager下载更新
     * @param versionInfo VersionInfo
     */
    fun downloadApk(versionInfo: VersionBean): Flow<Pair<Int, String?>> {
        return flow {
            emit(0 to null)
            //更新一下界面

            val request = Request.Builder().url(versionInfo.url!!).get().build()
            val body = OkHttpClient().newCall(request).execute().body!!
            //构造body
            val size = body.contentLength()
            body.byteStream().use { input ->
                val file = File(
                    ContextHolder.get().getExternalFilesDir("apk"),
                    "APK-${versionInfo.versionName}.apk"
                )

                if (file.exists()) {
                    emit(100 to file.absolutePath)
                    //判断是不是下过了
                    return@flow
                } else {
                    FileUtil.delete(file)
                    file.parentFile?.mkdirs()
                    //删除旧的

                    val tmpAPK = File(file.absolutePath + ".tmp")
                    tmpAPK.outputStream().use { output ->
                        val byte = ByteArray(4_096)
                        var len = input.read(byte)
                        var count = 0
                        while (len != -1) {
                            output.write(byte, 0, len)
                            len = input.read(byte)
                            count += len
                            emit((count * 100L / size).toInt() to null)
                            //下载更新
                        }
                        val packageArchiveInfo: PackageInfo? =
                            ContextHolder.get().packageManager.getPackageArchiveInfo(
                                tmpAPK.absolutePath,
                                0
                            )

                        if (packageArchiveInfo == null) {
                            tmpAPK.delete()
                            throw Exception(getString(R.string.apk_invalid))
                        }

                        tmpAPK.renameTo(file)
                        emit(100 to file.absolutePath)
                    }
                }
            }
        }
    }

}
