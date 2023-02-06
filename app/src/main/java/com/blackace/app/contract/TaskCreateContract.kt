package com.blackace.app.contract

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.blackace.app.ui.local.LocalActivity

/**
 *
 * @author: magicHeimdall
 * @create: 6/2/2023 11:24 AM
 */
class TaskCreateContract : ActivityResultContract<Unit, Boolean>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(context, LocalActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?):Boolean {
        return resultCode == ResultCodes.CREATE_TASK
    }
}
