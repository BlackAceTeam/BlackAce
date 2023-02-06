package com.blackace.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.afollestad.materialdialogs.MaterialDialog
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.app.contract.ResultCodes
import com.blackace.app.ui.account.AccountActivity
import com.blackace.app.ui.local.LocalActivity
import com.blackace.app.ui.sign.SignManagerActivity
import com.blackace.data.config.AceConfig
import com.blackace.data.entity.http.TaskBean
import com.blackace.data.state.PackageState
import com.blackace.data.state.TaskListState
import com.blackace.data.state.UserState
import com.blackace.databinding.ActivityMainBinding
import com.blackace.databinding.ItemMainTaskBinding
import com.blackace.databinding.ViewMainNavigationHeaderBinding
import com.blackace.util.ext.hide
import com.blackace.util.ext.log
import com.blackace.util.ext.show
import com.blackace.util.ext.showConfirmDialog
import com.drake.brv.listener.ItemDifferCallback
import com.drake.brv.utils.*


/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16 下午9:41
 */
class MainActivity : BaseActivity() {

    private val binding by viewBinding(ActivityMainBinding::bind)

    private val viewModel by viewModels<MainViewModel>()

    private var isLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar(R.string.app_name, null)
        initDrawer()
        initViewModel()
    }

    private fun initDrawer() {
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mDrawerToggle = ActionBarDrawerToggle(this, binding.root, toolbar, R.string.open, R.string.close)
        mDrawerToggle.syncState()

        binding.root.addDrawerListener(mDrawerToggle)

        val headerBinding = ViewMainNavigationHeaderBinding.bind(binding.navigation.getHeaderView(0))
        headerBinding.root.setOnClickListener {
            if (isLogin) {
                AceConfig.saveUser(null)
                viewModel.loadUserState()
            } else {
                AccountActivity.start(this)
            }
        }
        binding.navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.main_app_manager -> {

                }
                R.id.main_sign_manager -> {
                    SignManagerActivity.start(this)
                }
            }
            true
        }
        observeUserState(headerBinding)
    }

    private fun observeUserState(headerBinding: ViewMainNavigationHeaderBinding) {
        viewModel.userState.observe(this) {
            when (it) {
                is UserState.NoLogin -> {
                    headerBinding.tvEmail.setText(R.string.click_login)
                    headerBinding.tvUsername.setText(R.string.no_login)
                    isLogin = false
                }

                is UserState.Login -> {
                    headerBinding.tvUsername.text = it.bean.name
                    headerBinding.tvEmail.text = it.bean.email
                    isLogin = true
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel.packageState.observe(this) {
            when (it) {
                is PackageState.Loading -> {
                    showLoadingDialog()
                }

                is PackageState.LoadSignSuccess -> {
                    dismissLoadingDialog()
                    PackageFragment().show(supportFragmentManager,"PackageConfig")
                }

                is PackageState.Fail -> {
                    dismissLoadingDialog()
                    showSnackBar(it.msg)
                }

                is PackageState.Success -> {
                    dismissLoadingDialog()
                    MaterialDialog(this).show {
                        title(R.string.package_success)
                        message(text = getString(R.string.package_success_hint,it.path))
                        positiveButton(R.string.install){_->
                            viewModel.installApk(it.path)
                        }
                        negativeButton(R.string.done)
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ResultCodes.LOGIN_SUCCESS) {
            viewModel.loadUserState()
        }
    }
}
