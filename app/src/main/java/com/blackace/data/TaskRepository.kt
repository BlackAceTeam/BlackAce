package com.blackace.data

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Environment
import com.blackace.R
import com.blackace.apksigner.KeyStoreHelper
import com.blackace.data.config.AceConfig
import com.blackace.data.entity.AppBean
import com.blackace.data.entity.db.SignBean
import com.blackace.data.entity.http.BaseListBean
import com.blackace.data.entity.http.BaseResult
import com.blackace.data.entity.http.MoreBean
import com.blackace.data.entity.http.TaskBean
import com.blackace.data.http.HttpManager
import com.blackace.util.ext.getString
import com.blackace.util.holder.ApiHolder
import com.blackace.util.holder.ContextHolder
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 *
 * @author: magicHeimdall
 * @create: 4/1/2023 8:59 PM
 */
object TaskRepository {

    suspend fun createTask(appBean: AppBean, feature: String): BaseResult<Any> {
        return try {
            val bean = LocalRepository.generateTaskBean(appBean, feature)
            ApiHolder.api.taskCreate(bean)
        } catch (e: Exception) {
            BaseResult.fail(e)
        }
    }

    suspend fun taskList(start: String): MoreBean<BaseListBean<TaskBean>> {
        if (AceConfig.getUser() == null) {
            return MoreBean.fail(getString(R.string.login_pls))
        }
        return try {
            val result = ApiHolder.api.taskList(start)
            checkLocalApk(result)
            result
        } catch (e: Exception) {
            return MoreBean.fail(e)
        }
    }

    suspend fun taskQuery(taskNo: String): BaseResult<BaseListBean<TaskBean>> {
        return try {
            val result = ApiHolder.api.taskQuery(taskNo)
            result
        } catch (e: Exception) {
            BaseResult.fail(e.message.toString())
        }
    }

    private fun checkLocalApk(result: BaseResult<BaseListBean<TaskBean>>) {
        result.result?.data?.forEach {
            val apkFile = File(ContextHolder.get().externalCacheDir, "apks/${it.clientTaskNo}.apk")
            if (!apkFile.exists()) {
                it.status = TaskBean.STATE_APK_DELETE
            }
        }
    }

    suspend fun taskDelete(list: List<TaskBean>): BaseResult<Any> {
        return try {
            val taskNo = list.map { it.taskNo }.joinToString(",")
            val result = ApiHolder.api.taskDelete(taskNo)
            if (result.isSuccess()){
                list.forEach {
                    val apkFile = File(ContextHolder.get().externalCacheDir, "apks/${it.clientTaskNo}.apk")
                    apkFile.delete()
                }
            }
            result
        } catch (e: Exception) {
            BaseResult.fail(e.message.toString())
        }
    }

    suspend fun packageApk(signBean: SignBean, taskBean: TaskBean, signModel: Int): String {
        val tmpApk = File(ContextHolder.get().externalCacheDir, "apks/${UUID.randomUUID()}.apk")
        val originApk = File(ContextHolder.get().externalCacheDir, "apks/${taskBean.clientTaskNo}.apk")
        if (!originApk.exists()) {
            return getString(R.string.apk_was_delete)
        }
        return try {
            val request = Request.Builder().url(taskBean.downloadUrl).get().build()
//            val request = Request.Builder().url("").get().build()
            val body = HttpManager.pureOkhttp.newCall(request).execute().body!!
            FileOutputStream(tmpApk).use { output ->
                body.byteStream().use { input ->
                    input.copyTo(output)
                }
            }
            //download tmp apk
            val zipFile = ZipFile(tmpApk)
            val zipParams = ZipParameters()
            zipParams.fileNameInZip = "assets/app.apk"
            zipFile.addFile(originApk, zipParams)
            //repack apk

            if (checkSignExists(signBean)) {
                return getString(R.string.sign_file_was_delete)
            }
            val outPath = generateApkPath(taskBean)
            KeyStoreHelper.signApk(signBean.path, tmpApk.absolutePath, outPath, signBean.password, signModel)
            //sign

            outPath
        } catch (e: Exception) {
            e.printStackTrace()
            e.message.toString()
        } finally {
            tmpApk.delete()
        }
    }

    private fun checkSignExists(signBean: SignBean): Boolean {
        val file = File(signBean.path)
        return if (file.exists()) {
            false
        } else if (signBean.id == -1) {
            val input = ContextHolder.get().assets.open("blackace.jks")
            input.use {
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            false
        } else {
            true
        }
    }

    private fun generateApkPath(bean: TaskBean): String {
        val name = "${bean.apkName}-${bean.versionName}-${System.currentTimeMillis() / 1000}.apk"
        return if (VERSION.SDK_INT >= VERSION_CODES.R) {
            File(
                Environment.getExternalStorageDirectory(),
                "Download/$name"
            ).absolutePath
        } else {
            File(ContextHolder.get().getExternalFilesDir("apk"), name).absolutePath
        }
    }
}
