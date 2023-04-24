package com.blackace.app.ui.local

import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.app.contract.ResultCodes
import com.blackace.app.ui.file.FileDecoder
import com.blackace.app.ui.file.FileFragment
import com.blackace.app.ui.local.app.AppFragment
import com.blackace.app.ui.local.task.TaskCreateFragment
import com.blackace.data.state.SimpleActionState
import com.blackace.databinding.ActivityLocalBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16 下午9:41
 */
class LocalActivity : BaseActivity(), FileDecoder {

    private val binding by viewBinding(ActivityLocalBinding::bind)

    private val viewModel by viewModels<LocalViewModel>()

    private val fragmentList by lazy { arrayOf(AppFragment(), FileFragment()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local)
        setupToolbar(R.string.add_task)
        initTabLayout()
        initViewPager()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.featureState.observe(this) {
            when (it) {
                is SimpleActionState.Loading -> {
                    showLoadingDialog()
                }

                is SimpleActionState.Success -> {
                    dismissLoadingDialog()
                    TaskCreateFragment().show(supportFragmentManager,"TaskCreate")
                }

                is SimpleActionState.Fail -> {
                    dismissLoadingDialog()
                    showSnackBar(it.msg)
                }
            }
        }

        viewModel.createTaskState.observe(this) {
            when (it) {
                is SimpleActionState.Loading -> {
                    showLoadingDialog()
                }

                is SimpleActionState.Success -> {
                    dismissLoadingDialog()
                    setResult(ResultCodes.CREATE_TASK)
                    finish()
                }

                is SimpleActionState.Fail -> {
                    dismissLoadingDialog()
                    showSnackBar(it.msg)
                }
            }
        }
    }

    private fun initTabLayout() {
        val tagArray = intArrayOf(R.string.local_application, R.string.local_file)
        tagArray.forEach {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it))
        }
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.setCurrentItem(tab?.position ?: 0, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun initViewPager() {
        binding.viewPager.adapter = LocalPagerAdapter(this, fragmentList)
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)?.select()
            }
        })
    }

    override fun fileClick(path: String) {
        if (path.endsWith(".apk")) {
            viewModel.loadFeatures(path)
        }
    }

}
