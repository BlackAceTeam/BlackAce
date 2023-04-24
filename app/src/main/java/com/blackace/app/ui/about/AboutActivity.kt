package com.blackace.app.ui.about

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.BuildConfig
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.data.entity.AboutActionBean
import com.blackace.databinding.ActivityAboutBinding
import com.blackace.databinding.ItemAboutActionBinding
import com.blackace.util.ext.startBrowser
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup

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
        initRecyclerView()
    }


    @SuppressLint("SetTextI18n")
    private fun initName() {
        binding.tvSubTitle.text =
            "Version ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
    }

    private fun initRecyclerView() {
        val actionList = listOf(
            AboutActionBean(R.drawable.github,R.string.about_github,"https://github.com/BlackAceTeam/BlackAce"),
            AboutActionBean(R.drawable.telegram,R.string.about_telegram,"https://t.me/blackacepro"),
        )

        binding.recyclerView.linear().setup {
            addType<AboutActionBean>(R.layout.item_about_action)
            onBind {
                val binding = getBinding<ItemAboutActionBinding>()
                val bean = getModel<AboutActionBean>()
                binding.ivIcon.setImageResource(bean.icon)
                binding.tvTitle.setText(bean.title)
            }

            onClick(R.id.action_parent) {
                val bean = getModel<AboutActionBean>()
                startBrowser(bean.url)
            }

        }.models = actionList
    }

    companion object {

        fun start(activity: BaseActivity) {
            val intent = Intent()
            intent.setClass(activity, AboutActivity::class.java)
            activity.startActivity(intent)
        }

    }
}
