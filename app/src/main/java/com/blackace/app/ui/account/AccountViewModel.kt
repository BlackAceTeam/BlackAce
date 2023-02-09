package com.blackace.app.ui.account

import androidx.lifecycle.MutableLiveData
import com.blackace.app.base.BaseViewModel
import com.blackace.data.UserRepository
import com.blackace.data.config.AceConfig
import com.blackace.data.state.ChangePassState
import com.blackace.data.state.LoginState
import com.blackace.data.state.RegisterState
import com.blackace.data.state.SendEmailState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 9:37 下午
 */
class AccountViewModel : BaseViewModel() {

    val loginState = MutableLiveData<LoginState>()

    val registerState = MutableLiveData<RegisterState>()

    val actionState = MutableLiveData<ChangePassState>()

    val emailState = MutableLiveData<SendEmailState>(SendEmailState.Ready)

    fun login(username: String, password: String) {
        launchIO {
            loginState.postValue(LoginState.Loading)
            val msg = UserRepository.login(username, password)
            if (msg != null) {
                loginState.postValue(LoginState.Fail(msg))
            } else {
                loginState.postValue(LoginState.Success)
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        launchIO {
            registerState.postValue(RegisterState.Loading)
            val msg = UserRepository.register(username, email, password)
            if (msg != null) {
                registerState.postValue(RegisterState.Fail(msg))
            } else {
                registerState.postValue(RegisterState.Success)
            }
        }
    }


    fun changePassword(verify: String, newPass: String, account: String? = null) {
        var realAccount = account
        if (realAccount == null) {
            realAccount = AceConfig.getUser()?.name ?: return
        }
        actionState.postValue(ChangePassState.Loading)
        launchIO {
            val msg = UserRepository.changePassword(verify, newPass, realAccount)
            if (msg != null) {
                actionState.postValue(ChangePassState.Fail(msg))
            } else {
                actionState.postValue(ChangePassState.Success(newPass))
            }
        }
    }

    fun sendEmailVerify(account: String? = null) {

        if (emailState.value is SendEmailState.Wait || emailState.value is SendEmailState.Loading) {
            return
        }

        var realAccount = account
        if (realAccount == null) {
            realAccount = AceConfig.getUser()?.name ?: return
        }

        emailState.postValue(SendEmailState.Loading)
        launchIO {
            val result = UserRepository.sendEmailVerify(realAccount)
            if (result.isSuccess() && result.result != null) {
                emailState.postValue(SendEmailState.Success(result.result!!.email))
            } else {
                emailState.postValue(SendEmailState.Fail(result.msg))
            }

            delay(1000)

            var index = 59
            while (index > 0 && isActive) {
                emailState.postValue(SendEmailState.Wait(index))
                index--
                delay(1000)
            }

            emailState.postValue(SendEmailState.Ready)
        }
    }

    fun logout() {
        AceConfig.saveUser(null)
    }
}
