package com.blackace.app.ui.sign

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.app.ui.file.FileDecoder
import com.blackace.app.ui.file.FileFragment
import com.blackace.app.ui.local.LocalActivity
import com.blackace.app.ui.local.LocalPagerAdapter
import com.blackace.app.ui.local.app.AppFragment
import com.blackace.data.state.SignManagerState
import com.blackace.databinding.ActivitySignManagerBinding
import com.blackace.databinding.DialogAddSignBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 *
 * @author: magicHeimdall
 * @create: 5/1/2023 2:30 PM
 */
class SignManagerActivity : BaseActivity(), FileDecoder {

    private val binding by viewBinding(ActivitySignManagerBinding::bind)

    private val viewModel by viewModels<SignViewModel>()

    private val fragmentList by lazy { arrayOf(FileFragment(), SignFragment()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_manager)
        setupToolbar(R.string.sign_manager)
        initTabLayout()
        initViewPager()
        initViewModel()
    }


    private fun initTabLayout() {
        val tagArray = intArrayOf(R.string.add_sign, R.string.local_sign)
        tagArray.forEach {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it))
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)?.select()
            }
        })
    }


    private fun initViewModel() {
        viewModel.signManagerState.observe(this) {
            when (it) {
                is SignManagerState.Loading -> {
                    showLoadingDialog()
                }

                is SignManagerState.CreateFail -> {
                    dismissLoadingDialog()
                    showSnackBar(it.msg)
                }

                else -> {
                    dismissLoadingDialog()
                }
            }
        }
    }


    override fun onBackPressed() {
        val canBack = fragmentList[binding.viewPager.currentItem].onBack()
        if (canBack) {
            finish()
        }
    }


    @SuppressLint("CheckResult")
    override fun fileClick(path: String) {
        if ((!path.endsWith(".jks")) && (!path.endsWith(".bks"))) {
            return
        }

        val binding = DialogAddSignBinding.inflate(layoutInflater)
        clearErrorWithChange(binding.passEdit, binding.passLayout)
        clearErrorWithChange(binding.aliasEdit, binding.aliasLayout)
        clearErrorWithChange(binding.aliasPassEdit, binding.aliasPassLayout)

        MaterialDialog(this).show {
            title(R.string.add_sign)
            customView(view = binding.root)
            positiveButton(R.string.add) {
                addSign(path, binding)
            }

            negativeButton(R.string.cancel) {
                dismiss()
            }
        }
    }

    private fun clearErrorWithChange(input: TextInputEditText, layout: TextInputLayout) {
        input.addTextChangedListener {
            layout.error = ""
        }
    }

    private fun addSign(path: String, binding: DialogAddSignBinding) {
        val password = binding.passEdit.text?.toString()
        if (password.isNullOrEmpty()) {
            binding.passLayout.error = getString(R.string.input_no_null)
            return
        }
        val alias = binding.aliasEdit.text?.toString()
        if (alias.isNullOrEmpty()) {
            binding.aliasLayout.error = getString(R.string.input_no_null)
            return
        }
        val aliasPass = binding.aliasPassEdit.text?.toString()
        if (aliasPass.isNullOrEmpty()) {
            binding.aliasPassEdit.error = getString(R.string.input_no_null)
            return
        }

        viewModel.addSign(path, password, alias, aliasPass)
    }

    companion object {

        fun start(activity: BaseActivity) {
            val intent = Intent(activity, SignManagerActivity::class.java)
            activity.startActivityForResult(intent, 101)
        }
    }
}
