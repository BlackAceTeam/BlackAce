package com.blackace.app.contract

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.blackace.app.ui.account.profile.ProfileActivity

/**
 *
 * @author: magicHeimdall
 * @create: 6/2/2023 9:31 PM
 */
class ProfileActivityContract : ActivityResultContract<Unit, Int>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(context, ProfileActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }

    companion object {
        const val NOTHING = 0

        const val UPDATE_CONFIG = 1

    }
}
