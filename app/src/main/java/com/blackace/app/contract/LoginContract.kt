package com.blackace.app.contract

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.blackace.app.ui.account.AccountActivity

/**
 *
 * @author: magicHeimdall
 * @create: 1/3/2023 4:50 PM
 */
class LoginContract : ActivityResultContract<Unit, Boolean>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(context, AccountActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == ResultCodes.LOGIN_SUCCESS
    }
}
