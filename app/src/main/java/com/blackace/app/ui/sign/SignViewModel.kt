package com.blackace.app.ui.sign

import androidx.lifecycle.MutableLiveData
import com.blackace.app.base.BaseViewModel
import com.blackace.data.SignRepository
import com.blackace.data.entity.db.SignBean
import com.blackace.data.state.SignManagerState
import com.blackace.data.state.SimpleActionState

/**
 *
 * @author: magicHeimdall
 * @create: 5/1/2023 2:44 PM
 */
class SignViewModel : BaseViewModel() {

    val signListState = MutableLiveData<SimpleActionState<List<SignBean>>>()

    val signManagerState = MutableLiveData<SignManagerState>()

    fun loadLocalSign() {
        signListState.postValue(SimpleActionState.Loading())
        launchIO {
            val list = SignRepository.loadSignList()
            signListState.postValue(SimpleActionState.Success(list))
        }
    }

    fun addSign(path: String, password: String, alias: String, aliasPass: String) {
        signManagerState.postValue(SignManagerState.Loading)
        launchIO {
            val pair = SignRepository.addSign(path, password, alias, aliasPass)
            if (pair.second != null) {
                signManagerState.postValue(SignManagerState.CreateSuccess(pair.second!!))
                loadLocalSign()
            } else {
                signManagerState.postValue(SignManagerState.CreateFail(pair.first))
            }
        }
    }

    fun removeSign(position: Int, signId: Int, signPath: String) {
        signManagerState.postValue(SignManagerState.Loading)
        launchIO {
            SignRepository.removeSign(signId, signPath)
            signManagerState.postValue(SignManagerState.Delete(position))
        }
    }

}
