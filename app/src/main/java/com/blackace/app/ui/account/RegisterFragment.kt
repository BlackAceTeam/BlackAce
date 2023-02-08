package com.blackace.app.ui.account

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.R
import com.blackace.app.base.BaseFragment
import com.blackace.data.state.LoginState
import com.blackace.data.state.RegisterState
import com.blackace.databinding.FragmentRegisterBinding
import com.blackace.util.ext.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 11:59 PM
 */
class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    private val binding by viewBinding(FragmentRegisterBinding::bind)

    private val viewModel by activityViewModels<AccountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButton()
        initEdit()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.registerState.observe(viewLifecycleOwner) {
            when (it) {
                is RegisterState.Loading -> {
                    showLoadingDialog()
                }

                is RegisterState.Fail -> {
                    dismissLoadingDialog()
                    binding.layoutUsername.error = it.msg
                }

                is RegisterState.Success -> {
                    dismissLoadingDialog()
                    attachActivity<AccountActivity>().loginSuccess()
                }
            }
        }
    }

    private fun initEdit() {
        binding.layoutUsername.autoClearError()
        binding.layoutEmail.autoClearError()
        binding.layoutPassword.autoClearError()
        binding.layoutConfirm.autoClearError()
    }


    private fun initButton() {
        binding.btnRegister.setOnClickListener {
            if (checkEmail() && checkUserName() && checkPassword() && checkConfirm()) {
                viewModel.register(
                    binding.editUsername.text.toString(),
                    binding.editEmail.text.toString(),
                    binding.editPassword.text.toString()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        baseActivity().setToolbarTitle(R.string.register)
    }


    private fun checkEmail(): Boolean {
        val email = binding.editEmail.text.toString()
        if (!email.contains("@")) {
            binding.layoutEmail.isErrorEnabled = true
            binding.layoutEmail.error = getString(R.string.no_email)
            return false
        }
        return true
    }

    private fun checkUserName(): Boolean {
        val text = binding.editUsername.text.toString()
        return if (text.isEmpty()) {
            binding.layoutPassword.isErrorEnabled = true
            binding.layoutUsername.error = getString(R.string.username_must_no_empty)
            false
        } else {
            true
        }
    }

    private fun checkPassword(): Boolean {
        val text = binding.editPassword.text.toString()
        if (text.length < 6) {
            binding.layoutPassword.isErrorEnabled = true
            binding.layoutPassword.error = getString(R.string.password_must_6)
            return false
        }
        return true
    }

    private fun checkConfirm(): Boolean {
        val pass = binding.editPassword.text.toString()
        val confirm = binding.editConfirm.text.toString()
        if (pass != confirm) {
            binding.layoutConfirm.isErrorEnabled = true
            binding.layoutConfirm.error = getString(R.string.password_must_confirm)
            return false
        }
        return true
    }

}
