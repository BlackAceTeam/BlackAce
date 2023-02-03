package com.blackace.app.contract

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/18 下午3:09
 */
class StoragePermissionContract : ActivityResultContract<Unit, Boolean>() {

    private val ACTION_REQUEST_PERMISSIONS =
        "androidx.activity.result.contract.action.REQUEST_PERMISSIONS"


    private val EXTRA_PERMISSIONS = "androidx.activity.result.contract.extra.PERMISSIONS"

    private val EXTRA_PERMISSION_GRANT_RESULTS =
        "androidx.activity.result.contract.extra.PERMISSION_GRANT_RESULTS"


    override fun createIntent(context: Context, input: Unit): Intent {
        var intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("package:${context.packageName}")
            if (intent.resolveActivity(context.packageManager) == null) {
                intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.action = ACTION_REQUEST_PERMISSIONS
            intent.putExtra(EXTRA_PERMISSIONS, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }

        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        if (resultCode != Activity.RESULT_OK) return false
        if (intent == null) return false
        val permissions = intent.getStringArrayExtra(EXTRA_PERMISSIONS)
        val grantResults = intent.getIntArrayExtra(EXTRA_PERMISSION_GRANT_RESULTS)
        if (grantResults == null || permissions == null) return false
        return grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
    }
}
