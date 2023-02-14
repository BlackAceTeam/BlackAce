package com.blackace.app.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.app.contract.ProfileActivityContract
import com.blackace.app.contract.ResultCodes
import com.blackace.app.ui.about.AboutActivity
import com.blackace.app.ui.account.AccountActivity
import com.blackace.app.ui.sign.SignManagerActivity
import com.blackace.app.ui.web.WebActivity
import com.blackace.data.state.InstallState
import com.blackace.data.state.PackageState
import com.blackace.data.state.UserState
import com.blackace.databinding.ActivityMainBinding
import com.blackace.databinding.ViewMainNavigationHeaderBinding
import com.blackace.util.ext.log
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
        registerInstallReceiver()
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
                userinfoContract.launch(Unit)
            } else {
                AccountActivity.start(this)
            }
        }
        binding.navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.main_app_manager -> {

                    WebActivity.start(this, "https://www.baidu.com")
                }
                R.id.main_sign_manager -> {
                    SignManagerActivity.start(this)
                }
                R.id.main_about_app -> {
                    AboutActivity.start(this)
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
                    PackageFragment().show(supportFragmentManager, "PackageConfig")
                }

                is PackageState.Fail -> {
                    dismissLoadingDialog()
                    showSnackBar(it.msg)
                }

                is PackageState.Success -> {
                    dismissLoadingDialog()
                    MaterialDialog(this).show {
                        title(R.string.package_success)
                        message(text = getString(R.string.package_success_hint, it.path))
                        positiveButton(R.string.install) { _ ->
                            apkChangeReceiver.setApkInfo(it.pkg, it.path, it.appName)
                            viewModel.installApk(it.pkg, it.path, it.appName)
                        }
                        negativeButton(R.string.done)
                    }
                }
            }
        }

        viewModel.installState.observe(this) {
            when (it) {
                is InstallState.Loading -> {
                    showLoadingDialog()
                }

                is InstallState.UninstallSuccess -> {
                    viewModel.installApk(it.pkg,it.path,it.appName,false)
                }

                is InstallState.NeedUnInstall -> {
                    dismissLoadingDialog()
                    MaterialDialog(this).show {
                        title(R.string.install_tip)
                        message(R.string.need_uninstall_hint)
                        positiveButton(R.string.uninstall) { _ ->
                            viewModel.uninstall(it.pkg)
                        }
                        negativeButton(R.string.install_continue) { _ ->
                            viewModel.installApk(it.pkg, it.path, it.appName, false)
                        }
                    }
                }


                is InstallState.InstallFail -> {
                    dismissLoadingDialog()
                    MaterialDialog(this).show {
                        title(R.string.install_fail)
                        message(text = getString(R.string.install_fail_hint, it.error, it.path))
                        positiveButton(R.string.done)
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

    private val userinfoContract = registerForActivityResult(ProfileActivityContract()) {
        if (it == ProfileActivityContract.UPDATE_CONFIG) {
            viewModel.loadUserState()
            AccountActivity.start(this)
        }
    }

    private fun registerInstallReceiver() {
        val filter = IntentFilter()
        filter.addAction("android.intent.action.PACKAGE_ADDED")
        filter.addAction("android.intent.action.PACKAGE_REPLACED")
        filter.addAction("android.intent.action.PACKAGE_REMOVED")
        filter.addDataScheme("package")
        registerReceiver(apkChangeReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(apkChangeReceiver)
    }

    private val apkChangeReceiver = object : BroadcastReceiver() {

        private var apkPkg: String = ""
        private var apkPath: String = ""
        private var apkName: String = ""
        override fun onReceive(context: Context?, intent: Intent?) {
            val pkg = intent?.dataString ?: return
            if (!pkg.endsWith(apkPkg)) {
                return
            }
            if ("android.intent.action.PACKAGE_ADDED" == intent.action || "android.intent.action.PACKAGE_REPLACED" == intent.action) {
                dismissLoadingDialog()
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
    }
}
