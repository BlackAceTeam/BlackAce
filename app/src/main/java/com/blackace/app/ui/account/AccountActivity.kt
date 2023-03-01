package com.blackace.app.ui.account

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.app.contract.ResultCodes
import com.blackace.data.state.ChangePassState
import com.blackace.databinding.ActivityAccountBinding
import com.blackace.util.ToastUtil

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 9:32 下午
 */
class AccountActivity : BaseActivity() {

    private val binding by viewBinding(ActivityAccountBinding::bind)

    private val viewModel by viewModels<AccountViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        setupToolbar(R.string.login)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.actionState.observe(this){
            when (it) {
                is ChangePassState.Loading -> {
                    showLoadingDialog()
                }

                is ChangePassState.Success -> {
                    dismissLoadingDialog()
                    ToastUtil.showToast(R.string.password_change_success)
                }

                is ChangePassState.Fail -> {
                    dismissLoadingDialog()
                    showSnackBar(it.msg)
                }
            }
        }
    }

    fun loginSuccess(){
        setResult(ResultCodes.LOGIN_SUCCESS)
        finish()
    }

}
