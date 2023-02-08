package com.blackace.app.ui.account.profile

import androidx.lifecycle.MutableLiveData
import com.blackace.app.base.BaseViewModel
import com.blackace.data.UserRepository
import com.blackace.data.config.AceConfig
import com.blackace.data.state.ChangePassState
import com.blackace.data.state.SendEmailState
import com.blackace.data.state.UserState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 *
 * @author: magicHeimdall
 * @create: 6/2/2023 9:36 PM
 */
class ProfileViewModel : BaseViewModel() {

    val userState = MutableLiveData<UserState>()

    val actionState = MutableLiveData<ChangePassState>()

    val emailState = MutableLiveData<SendEmailState>(SendEmailState.Ready)

    init {
        loadUserState()
    }

    private fun loadUserState() {
        launchIO {
            val userBean = AceConfig.getUser()
            if (userBean == null) {
                userState.postValue(UserState.NoLogin)
            } else {
                userState.postValue(UserState.Login(userBean))
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
                actionState.postValue(ChangePassState.Success)
            }
        }
    }

    fun sendEmailVerify(account: String? = null) {

        if (emailState.value !is SendEmailState.Ready) {
            return
        }

        var realAccount = account
        if (realAccount == null) {
            realAccount = AceConfig.getUser()?.name ?: return
        }

        emailState.postValue(SendEmailState.Loading)
        launchIO {
            val msg = UserRepository.sendEmailVerify(realAccount)
            if (msg != null) {
                emailState.postValue(SendEmailState.Success(realAccount))
//                todo emailState.postValue(SendEmailState.Fail(msg))
            } else {
                emailState.postValue(SendEmailState.Success(realAccount))
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
