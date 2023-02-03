package com.blackace.app.ui.account

import androidx.lifecycle.MutableLiveData
import com.blackace.app.base.BaseViewModel
import com.blackace.data.UserRepository
import com.blackace.data.state.LoginState
import com.blackace.data.state.RegisterState
import com.blackace.util.ext.log

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 9:37 下午
 */
class AccountViewModel : BaseViewModel() {

    val loginState = MutableLiveData<LoginState>()

    val registerState = MutableLiveData<RegisterState>()

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
            log("register finish")
            log(msg)
            if (msg != null) {
                registerState.postValue(RegisterState.Fail(msg))
            } else {
                registerState.postValue(RegisterState.Success)
            }
        }
    }
}
