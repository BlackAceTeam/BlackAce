package com.blackace.app.ui.account

import androidx.lifecycle.MutableLiveData
import com.blackace.app.base.BaseViewModel
import com.blackace.data.AppRepository
import com.blackace.data.config.AceConfig
import com.blackace.data.state.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 9:37 下午
 */
class AccountViewModel : BaseViewModel() {

    val loginState = MutableLiveData<SimpleActionState<Unit>>()

    val registerState = MutableLiveData<SimpleActionState<Unit>>()

    val changePassState = MutableLiveData<SimpleActionState<String>>()

    val emailState = MutableLiveData<SendEmailState>(SendEmailState.Ready)

    fun login(username: String, password: String) {
        launchIO {
            loginState.postValue(SimpleActionState.Loading())
            val msg = AppRepository.login(username, password)
            if (msg != null) {
                loginState.postValue(SimpleActionState.Fail(msg))
            } else {
                loginState.postValue(SimpleActionState.Success(Unit))
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        launchIO {
            registerState.postValue(SimpleActionState.Loading())
            val msg = AppRepository.register(username, email, password)
            if (msg != null) {
                registerState.postValue(SimpleActionState.Fail(msg))
            } else {
                registerState.postValue(SimpleActionState.Success(Unit))
            }
        }
    }


    fun changePassword(verify: String, newPass: String, account: String? = null) {
        var realAccount = account
        if (realAccount == null) {
            realAccount = AceConfig.getUser()?.name ?: return
        }
        changePassState.postValue(SimpleActionState.Loading())
        launchIO {
            val msg = AppRepository.changePassword(verify, newPass, realAccount)
            if (msg != null) {
                changePassState.postValue(SimpleActionState.Fail(msg))
            } else {
                changePassState.postValue(SimpleActionState.Success(newPass))
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
            val result = AppRepository.sendEmailVerify(realAccount)
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
