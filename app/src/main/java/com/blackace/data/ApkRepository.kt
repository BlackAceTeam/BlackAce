package com.blackace.data

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.blackace.util.holder.ContextHolder
import java.io.File

/**
 *
 * @author: magicHeimdall
 * @create: 13/2/2023 9:22 PM
 */
object ApkRepository {

    fun isApkSignSame(pkg: String, path: String): Boolean {
        val oldSign = getApkSignCode(pkg)
        if (oldSign == 0) {
            //未安装
            return true
        }
        val newSign = getApkSignCode(path)
        return oldSign == newSign
    }

    fun install(path: String): String? {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri =
                    FileProvider.getUriForFile(ContextHolder.get(), "com.blackace.fileprovider", File(path))
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
            } else {
                intent.setDataAndType(
                    Uri.parse("file://$path"),
                    "application/vnd.android.package-archive"
                )
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextHolder.get().startActivity(intent)
        } catch (e: Exception) {
            return e.message
        }
        return null
    }

    fun uninstall(pkg: String) {
        runCatching {
            val packageURI = Uri.parse("package:$pkg")
            val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
            uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextHolder.get().startActivity(uninstallIntent)
        }
    }

    fun launch(pkg: String) {
        runCatching {
            val manager: PackageManager = ContextHolder.get().packageManager
            val intent = manager.getLaunchIntentForPackage(pkg)
            intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextHolder.get().startActivity(intent)
        }
    }

    private fun getApkSignCode(pkgOrPath: String): Int {
        return try {
            val pm = ContextHolder.get().packageManager
            val info = if (pkgOrPath.startsWith("/")) {
                pm.getPackageArchiveInfo(pkgOrPath, PackageManager.GET_SIGNATURES)
            } else {
                pm.getPackageInfo(pkgOrPath, PackageManager.GET_SIGNATURES)
            }
            val sign = info!!.signatures[0]
            sign.hashCode()
        } catch (e: Exception) {
            0
        }
    }

}
