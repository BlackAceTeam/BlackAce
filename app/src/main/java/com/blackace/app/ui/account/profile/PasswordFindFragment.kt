package com.blackace.app.ui.account.profile

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blackace.R
import com.blackace.data.state.SendEmailState
import com.blackace.databinding.DialogPasswordFindBinding
import com.blackace.util.ToastUtil
import com.blackace.util.ext.*
import com.google.android.material.snackbar.Snackbar
import java.io.PrintWriter
import java.io.StringWriter


/**
 *
 * @author: magicHeimdall
 * @create: 7/2/2023 3:41 PM
 */
class PasswordFindFragment : DialogFragment() {

    private lateinit var binding: DialogPasswordFindBinding

    private val viewModel by activityViewModels<ProfileViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =
            MaterialDialog(requireContext())
                .title(R.string.find_password)
                .customView(R.layout.dialog_password_find)
        binding = DialogPasswordFindBinding.bind(dialog.getCustomView())
        initView()
        initViewModel()
        return dialog
    }

    private fun initView() {
        binding.passLayout.autoClearError()
        binding.accountLayout.autoClearError()
        binding.verifyLayout.autoClearError {
            binding.btnSendVerify.show()
        }

        binding.btnVerify.setOnClickListener {
            val account = binding.account.text.toString()
            if (account.isEmpty()) {
                binding.accountLayout.isErrorEnabled = true
                binding.accountLayout.error = getString(R.string.username_must_no_empty)
                return@setOnClickListener
            }

            val verify = binding.verify.text.toString()
            if (verify.length != 6) {
                binding.verifyLayout.isErrorEnabled = true
                binding.verifyLayout.error = getString(R.string.verify_must_6)
                binding.btnSendVerify.hide()
                return@setOnClickListener
            }

            val newPass = binding.pass.text.toString()
            if (newPass.length < 6) {
                binding.passLayout.isErrorEnabled = true
                binding.passLayout.error = getString(R.string.password_must_6)
                return@setOnClickListener
            }

            viewModel.changePassword(verify, newPass,account)
            dismiss()
        }

        binding.btnSendVerify.setOnClickListener {
            val account = binding.account.text.toString()
            if (account.isEmpty()) {
                binding.accountLayout.isErrorEnabled = true
                binding.accountLayout.error = getString(R.string.username_must_no_empty)
                return@setOnClickListener
            }
            viewModel.sendEmailVerify(account)
        }
    }

    private fun initViewModel() {
        viewModel.emailState.observe(this) {
            when (it) {
                is SendEmailState.Wait -> {
                    binding.btnSendVerify.text = getString(R.string.resend, it.second)
                }

                is SendEmailState.Success -> {
                    dismissLoadingDialog()
                    ToastUtil.showToast(getString(R.string.verify_was_send, it.email))
                }

                is SendEmailState.Ready -> {
                    binding.btnSendVerify.setText(R.string.send_now)
                }


                is SendEmailState.Fail -> {
                    dismissLoadingDialog()
                    ToastUtil.showToast(it.msg)
                }

                is SendEmailState.Loading -> {
                    showLoadingDialog()
                }
            }
        }
    }

}
