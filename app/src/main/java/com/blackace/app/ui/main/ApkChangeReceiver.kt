package com.blackace.app.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.blackace.data.state.InstallState

/**
 *
 * @author: magicHeimdall
 * @create: 1/3/2023 5:28 PM
 */
class ApkChangeReceiver(private val activity: MainActivity, private val viewModel: MainViewModel) :
    BroadcastReceiver() {

    private var apkPkg: String = ""
    private var apkPath: String = ""
    private var apkName: String = ""
    override fun onReceive(context: Context?, intent: Intent?) {
        val pkg = intent?.dataString ?: return
        if (!pkg.endsWith(apkPkg)) {
            return
        }
        if ("android.intent.action.PACKAGE_ADDED" == intent.action || "android.intent.action.PACKAGE_REPLACED" == intent.action) {
            activity.dismissLoadingDialog()
        }

        if (intent.action == "android.intent.action.PACKAGE_REMOVED") {
            viewModel.installState.postValue(InstallState.UninstallSuccess(apkPkg, apkPath, apkName))
        }
    }

    fun setApkInfo(pkg: String, path: String, name: String) {
        this.apkPkg = pkg
        this.apkPath = path
        this.apkName = name
    }


    fun registerInstallReceiver() {
        val filter = IntentFilter()
        filter.addAction("android.intent.action.PACKAGE_ADDED")
        filter.addAction("android.intent.action.PACKAGE_REPLACED")
        filter.addAction("android.intent.action.PACKAGE_REMOVED")
        filter.addDataScheme("package")
        activity.registerReceiver(this, filter)
    }
}
