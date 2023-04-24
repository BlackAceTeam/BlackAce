package com.blackace.app.ui.local

import androidx.lifecycle.MutableLiveData
import com.blackace.app.base.BaseViewModel
import com.blackace.data.LocalRepository
import com.blackace.data.TaskRepository
import com.blackace.data.entity.AppBean
import com.blackace.data.entity.http.FeatureBean
import com.blackace.data.state.SimpleActionState

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16 下午9:42
 */
class LocalViewModel : BaseViewModel() {

    val appListLiveData = MutableLiveData<SimpleActionState<List<AppBean>>>(SimpleActionState.Loading())

    val featureState = MutableLiveData<SimpleActionState<Pair<List<FeatureBean>,AppBean>>>()

    val createTaskState = MutableLiveData<SimpleActionState<Unit>>()

    init {
        loadAppList()
    }

    private fun loadAppList() {
        appListLiveData.postValue(SimpleActionState.Loading())
        launchIO {
            val list = LocalRepository.loadInstalledAppList()
            appListLiveData.postValue(SimpleActionState.Success(list))
        }
    }

    fun loadFeatures(apkPath: String) {
        featureState.postValue(SimpleActionState.Loading())
        launchIO {
            val appBean = LocalRepository.loadApkInfo(apkPath)
            if (appBean != null) {
                loadFeatures(appBean)
            } else {
                featureState.postValue(SimpleActionState.Fail("Load Fail"))
            }
        }
    }

    fun loadFeatures(appBean: AppBean) {
        featureState.postValue(SimpleActionState.Loading())
        launchIO {
            val pair = LocalRepository.loadFeatures()
            if (pair.first.isNotEmpty() || pair.second.isNullOrEmpty()) {
                featureState.postValue(SimpleActionState.Fail(pair.first))
            } else {
                featureState.postValue(SimpleActionState.Success(pair.second!! to appBean))
            }
        }
    }

    fun createTask(appBean: AppBean, feature: String) {
        launchIO {
            createTaskState.postValue(SimpleActionState.Loading())
            val result = TaskRepository.createTask(appBean, feature)
            if (result.isSuccess()){
                createTaskState.postValue(SimpleActionState.Success(Unit))
            }else{
                createTaskState.postValue(SimpleActionState.Fail(result.msg))
            }
        }
    }
}
