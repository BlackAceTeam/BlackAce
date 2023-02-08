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


}
