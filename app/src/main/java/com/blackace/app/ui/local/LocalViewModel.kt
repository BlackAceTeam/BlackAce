package com.blackace.app.ui.local

import androidx.lifecycle.MutableLiveData
import com.blackace.app.base.BaseViewModel
import com.blackace.data.LocalRepository
import com.blackace.data.TaskRepository
import com.blackace.data.entity.AppBean
import com.blackace.data.state.CreateTaskState
import com.blackace.data.state.FeatureState
import com.blackace.data.state.LocalAppState

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16 下午9:42
 */
class LocalViewModel : BaseViewModel() {

    val appListLiveData = MutableLiveData<LocalAppState>(LocalAppState.Loading)

    val featureState = MutableLiveData<FeatureState>()

    val createTaskState = MutableLiveData<CreateTaskState>()

    init {
        loadAppList()
    }

    private fun loadAppList() {
        appListLiveData.postValue(LocalAppState.Loading)
        launchIO {
            val list = LocalRepository.loadInstalledAppList()
            appListLiveData.postValue(LocalAppState.Success(list))
        }
    }

    fun loadFeatures(apkPath: String) {
        featureState.postValue(FeatureState.Loading)
        launchIO {
            val appBean = LocalRepository.loadApkInfo(apkPath)
            if (appBean != null) {
                loadFeatures(appBean)
            } else {
                featureState.postValue(FeatureState.Fail("Load Fail"))
            }
        }
    }

    fun loadFeatures(appBean: AppBean) {
        featureState.postValue(FeatureState.Loading)
        launchIO {
            val pair = LocalRepository.loadFeatures()
            if (pair.first.isNotEmpty() || pair.second.isNullOrEmpty()) {
                featureState.postValue(FeatureState.Fail(pair.first))
            } else {
                featureState.postValue(FeatureState.Success(pair.second!!, appBean))
            }
        }
    }

    fun createTask(appBean: AppBean, feature: String) {
        launchIO {
            createTaskState.postValue(CreateTaskState.Loading)
            val result = TaskRepository.createTask(appBean, feature)
            if (result.isSuccess()){
                createTaskState.postValue(CreateTaskState.Success)
            }else{
                createTaskState.postValue(CreateTaskState.Fail(result.msg))
            }
        }
    }
}
