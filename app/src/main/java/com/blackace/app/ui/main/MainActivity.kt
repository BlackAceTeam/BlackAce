package com.blackace.app.ui.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.app.contract.*
import com.blackace.app.ui.about.AboutActivity
import com.blackace.app.ui.sign.SignManagerActivity
import com.blackace.app.ui.web.WebActivity
import com.blackace.data.ApkRepository
import com.blackace.data.config.AceConfig
import com.blackace.data.entity.http.VersionBean
import com.blackace.data.state.InstallState
import com.blackace.data.state.PackageState
import com.blackace.data.state.UpdateAppState
import com.blackace.data.state.UserState
import com.blackace.databinding.ActivityMainBinding
import com.blackace.databinding.ViewMainNavigationHeaderBinding
import com.blackace.util.ToastUtil.showToast
import com.drake.brv.utils.*


/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16 下午9:41
 */
class MainActivity : BaseActivity() {

    private val binding by viewBinding(ActivityMainBinding::bind)

    private val viewModel by viewModels<MainViewModel>()

    private var mUpdateDialog: MaterialDialog? = null

    private var isLogin = false

    private val apkChangeReceiver by lazy { ApkChangeReceiver(this, viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar(R.string.app_name, null)
        initDrawer()
        initViewModel()
        registerBackCallback()
        apkChangeReceiver.registerInstallReceiver()
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
                loginContract.launch(Unit)
            }
        }
        binding.navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.main_app_manager -> {
                    if (isLogin) {
                        webContract.launch(WebViewParam(AceConfig.getAppManagerUrl(), false))
                    } else {
                        showToast(R.string.login_pls)
                        loginContract.launch(Unit)
                    }
                }
                R.id.main_sign_manager -> {
                    SignManagerActivity.start(this)
                }
                R.id.main_about_app -> {
                    AboutActivity.start(this)
                }
                R.id.main_chat -> {
                    startBrowser("https://t.me/blackacepro")
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

        viewModel.updateAppState.observe(this) {
            when (it) {
                is UpdateAppState.NoUpdate -> {
                    showSnackBar(it.msg)
                }

                is UpdateAppState.Update -> {
                    showUpdateDialog(it.versionInfo)
                }

                is UpdateAppState.Downloading -> {
                    mUpdateDialog?.positiveButton(text = "${it.progress}%")
                }

                is UpdateAppState.DownloadFail -> {
                    mUpdateDialog?.positiveButton(R.string.re_download)
                    showToast(it.msg)
                }

                is UpdateAppState.Install -> {
                    mUpdateDialog?.clearPositiveListeners()
                    mUpdateDialog?.positiveButton(R.string.install) { _ ->
                        ApkRepository.install(it.path)
                    }
                    ApkRepository.install(it.path)
                }
            }
        }
        viewModel.installState.observe(this) {
            when (it) {
                is InstallState.Loading -> {
                    showLoadingDialog()
                }

                is InstallState.UninstallSuccess -> {
                    viewModel.installApk(it.pkg, it.path, it.appName, false)
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

    @Suppress("DEPRECATION")
    @SuppressLint("CheckResult")
    private fun showUpdateDialog(info: VersionBean) {
        mUpdateDialog = MaterialDialog(this).show {
            title(text = getString(R.string.new_version, info.versionName))
            message(text = info.content)
            noAutoDismiss()
            if (info.isForceUpdate()) {
                //            if (true) {
                cancelable(false)
                cancelOnTouchOutside(false)
            } else {
                cancelable(true)
                neutralButton(R.string.update_next) {
                    dismiss()
                }
            }

            positiveButton(R.string.update_now) {
                viewModel.downloadApk(info)
            }

            negativeButton(R.string.browser_download) {
                startBrowser(info.url)
                if (!info.isForceUpdate()) {
                    dismiss()
                }
            }
        }
    }


    private fun startBrowser(url: String?) {
        if (url.isNullOrEmpty()) {
            showSnackBar("Url is Empty")
            return
        }
        runCatching {
            val uri = Uri.parse(url)
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = uri
            startActivity(intent)
        }.onFailure {
            showSnackBar(getString(R.string.open_fail))
        }
    }

    private fun registerBackCallback() {
        addBackCallback(lifecycle) {
            if (binding.root.isOpen) {
                binding.root.close()
                return@addBackCallback true
            } else {
                return@addBackCallback false
            }
        }
    }

    //用户信息界面
    private val userinfoContract = registerForActivityResult(ProfileActivityContract()) {
        if (it == ProfileActivityContract.UPDATE_CONFIG) {
            viewModel.loadUserState()
            loginContract.launch(Unit)
        }
    }

    //登录注册界面
    private val loginContract = registerForActivityResult(LoginContract()) {
        if (it) {
            viewModel.loadUserState()
        }
    }

    //web界面
    private val webContract = registerForActivityResult(OpenWebContract()) {}

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(apkChangeReceiver)
        mUpdateDialog?.dismiss()
    }

}
