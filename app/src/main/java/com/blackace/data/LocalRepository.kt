package com.blackace.data

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.blackace.data.entity.AppBean
import com.blackace.data.entity.FileBean
import com.blackace.data.entity.http.FeatureBean
import com.blackace.util.ext.*
import com.blackace.util.holder.ApiHolder
import com.blackace.util.holder.ContextHolder
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/14-下午10:51
 */
object LocalRepository {

    private val fileDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault()) }

    fun loadInstalledAppList(): List<AppBean> {
        val packageManager = ContextHolder.get().packageManager
        val installedApplications = packageManager.getInstalledPackages(0)

        val selfPkg = ContextHolder.get().packageName

        val appList = mutableListOf<AppBean>()

        installedApplications.forEach {

            if (it.packageName == selfPkg) {
                return@forEach
            }

            if ((it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
                return@forEach
            }

            appList.add(loadApkInfo(it))
        }
        return appList
    }

    fun listFile(path: String): List<FileBean> {
        val file = File(path)
        val backBean = FileBean(null, "..", null, "")

        if (file.isFile) {
            return listOf(backBean)
        }

        val list = mutableListOf<FileBean>()
        val dirList = TreeMap<String, FileBean>(String.CASE_INSENSITIVE_ORDER)
        val fileList = TreeMap<String, FileBean>(String.CASE_INSENSITIVE_ORDER)

        file.listFiles()?.forEach {
            if (it.isDirectory) {
                dirList[it.name] =
                    FileBean(it.absolutePath, it.name, null, timeToStr(it.lastModified()))
            } else {
                fileList[it.name] =
                    FileBean(it.absolutePath, it.name, it.size(), timeToStr(it.lastModified()))
            }

        }

        list.add(backBean)
        list.addAll(dirList.values)
        list.addAll(fileList.values)

        return list

    }

    private fun timeToStr(time: Long): String {
        return fileDateFormat.format(Date(time))
    }


    suspend fun loadFeatures(): Pair<String, List<FeatureBean>?> {
        return try {
            val result = ApiHolder.api.featureList()
            if (result.isSuccess() && (result.result?.data?.isNotEmpty() == true)) {
                "" to result.result!!.data
            } else {
                result.msg to null
            }
        } catch (e: Exception) {
            e.message.toString() to null
        }
    }

    fun loadApkInfo(apkPath: String): AppBean? {
        val packageInfo = ContextHolder.get().packageManager.getPackageArchiveInfo(apkPath, 0) ?: return null
        return loadApkInfo(packageInfo, apkPath)
    }

    private fun loadApkInfo(packageInfo: PackageInfo, apkPath: String? = null): AppBean {
        val packageManager = ContextHolder.get().packageManager
        val applicationInfo = packageInfo.applicationInfo
        if (!apkPath.isNullOrEmpty()){
            applicationInfo.sourceDir = apkPath
            applicationInfo.publicSourceDir = apkPath
        }
        val name = applicationInfo.loadLabel(packageManager).toString()
        val icon = applicationInfo.loadIcon(packageManager)
        val apkFile = File(apkPath ?: applicationInfo.sourceDir)
        val versionCode = packageInfo.realVersion().toString()

        val uri = Uri.fromFile(apkFile)

        return AppBean(
            name,
            packageInfo.packageName,
            packageInfo.versionName ?: "",
            versionCode,
            apkFile.size(),
            uri.toString(),
            !packageInfo.splitNames.isNullOrEmpty(),
            icon
        )
    }


    fun generateTaskBean(appBean: AppBean, feature: String): Map<String, String> {
        val uuid = UUID.randomUUID().toString()
        val parentFile = File(ContextHolder.get().externalCacheDir, "apks")
        parentFile.mkdir()
        val apkFile = File(parentFile, "$uuid.apk")
        ContextHolder.get().contentResolver.openInputStream(Uri.parse(appBean.source)).use { input ->
            BufferedOutputStream(FileOutputStream(apkFile)).use { output ->
                input?.copyTo(output)
            }
        }

        val map = mutableMapOf<String, String>()
        map["pkg"] = appBean.pkg
        map["apk_version_code"] = appBean.versionCode
        map["apk_version_name"] = appBean.version
        map["name"] = appBean.name
        map["md5"] = apkFile.md5()
        map["apk_size"] = apkFile.length().toString()
        map["libs"] = apkFile.libs()
        map["client_taskNo"] = uuid
        map["feature"] = feature
        map["content"] = generateComponentJson(apkFile)
        map["icon_url"] = appBean.icon.base64()

        return map
    }

    private fun generateComponentJson(apkFile: File): String {
        val packageInfo = ContextHolder.get().packageManager.getPackageArchiveInfo(
            apkFile.absolutePath,
            PackageManager.GET_ACTIVITIES or PackageManager.GET_PERMISSIONS
        ) ?: return "{}"

        val componentJson = JSONObject()
        val activities = JSONArray()
        packageInfo.activities?.forEach {
            activities.put(it.name)
        }
        val permissions = JSONArray()
        packageInfo.requestedPermissions?.forEach {
            permissions.put(it)
        }
        componentJson.put("permissions", permissions)
        componentJson.put("activities", activities)
        return componentJson.toString()
    }

}
