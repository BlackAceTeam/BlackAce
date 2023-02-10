package com.blackace.app.ui.about

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.BuildConfig
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.databinding.ActivityAboutBinding

/**
 *
 * @author: magicHeimdall
 * @create: 10/2/2023 4:52 PM
 */
class AboutActivity : BaseActivity() {

    private val binding by viewBinding(ActivityAboutBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setupToolbar(R.string.app_name)
        initName()
    }

    @SuppressLint("SetTextI18n")
    private fun initName() {
        binding.tvTitle.text =
            "${getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
    }

    companion object {


        fun start(activity: BaseActivity) {
            val intent = Intent()
            intent.setClass(activity, AboutActivity::class.java)
            activity.startActivity(intent)
        }

    }
}
