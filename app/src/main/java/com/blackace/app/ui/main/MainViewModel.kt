package com.blackace.app.ui.main

import androidx.lifecycle.MutableLiveData
import com.blackace.app.base.BaseViewModel
import com.blackace.data.ApkRepository
import com.blackace.data.AppRepository
import com.blackace.data.SignRepository
import com.blackace.data.TaskRepository
import com.blackace.data.config.AceConfig
import com.blackace.data.entity.http.VersionBean
import com.blackace.data.entity.db.SignBean
import com.blackace.data.entity.http.TaskBean
import com.blackace.data.state.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16 下午9:41
 */
class MainViewModel : BaseViewModel() {

    val userState = MutableLiveData<UserState>()

    val taskState = MutableLiveData<TaskListState>()

    val packageState = MutableLiveData<PackageState>()

    val installState = MutableLiveData<InstallState>()

    val updateAppState = MutableLiveData<UpdateAppState>()

    var taskStart: String = ""

    init {
        loadUserState(true)
        checkUpdate(false)
        loadSystemConfig()
    }

    fun loadUserState(needRefresh: Boolean = false) {
        launchIO {
            val userBean = AceConfig.getUser()
            if (userBean == null) {
                userState.postValue(UserState.NoLogin)
            } else {
                userState.postValue(UserState.Login(userBean))
                if (needRefresh) {
                    freshUser()
                }
            }
        }
    }

    private fun freshUser() {
        launchIO {
            val userBean = AppRepository.freshUser()
            if (userBean == null) {
                userState.postValue(UserState.NoLogin)
            } else {
                userState.postValue(UserState.Login(userBean))
            }
        }
    }

    private fun loadSystemConfig(){
        launchIO {
            AppRepository.loadSystemConfig()
        }
    }

    fun loadTaskList(start: String = taskStart) {
        launchIO {
            val result = TaskRepository.taskList(start)
            if (result.isSuccess() && result.result?.data != null) {
                taskStart = result.start
                if (start.isEmpty()) {
                    taskState.postValue(TaskListState.FirstSuccess(result.result!!.data!!, result.more == 1))
                } else {
                    taskState.postValue(TaskListState.MoreSuccess(result.result!!.data!!, result.more == 1))
                }
            } else {
                if (start.isEmpty()) {
                    taskState.postValue(TaskListState.FirstFail(result.msg))
                } else {
                    taskState.postValue(TaskListState.MoreFail(result.msg))
                }
            }
        }
    }

    fun updateState(list: List<Any?>?) {
        launchIO {
            val noCompleteList = list?.filter {
                (it is TaskBean) && (it.status == TaskBean.STATE_LOADING || it.status == TaskBean.STATE_WAIT)
            }?.map {
                (it as TaskBean).taskNo
            }

            if (noCompleteList.isNullOrEmpty()) {
                return@launchIO
            }

            delay(1000)

            val result = TaskRepository.taskQuery(noCompleteList.joinToString(","))
            val resultList = result.result?.data
            if (result.isSuccess() && resultList?.isNotEmpty() == true) {
                val originMap = LinkedHashMap<String, TaskBean>()

                list.forEach {
                    val bean = it as TaskBean
                    originMap[it.taskNo] = bean
                }
                resultList.forEach {
                    originMap[it.taskNo] = it
                }

                taskState.postValue(TaskListState.StateUpdate(originMap.values.toList()))
            } else if (!result.isSuccess()) {
                updateState(list)
            }

        }

    }

    fun taskDelete(list: List<TaskBean>) {
        launchIO {
            val result = TaskRepository.taskDelete(list)
            if (result.isSuccess()) {
                taskState.postValue(TaskListState.Delete)
            } else {
                taskState.postValue(TaskListState.SimpleFail(result.msg))
            }
        }
    }

    fun loadSignList(model: TaskBean) {
        packageState.postValue(PackageState.Loading)
        launchIO {
            val result = SignRepository.loadSignWithDefault()
            packageState.postValue(PackageState.LoadSignSuccess(model, result))
        }
    }

    fun packageApk(taskBean: TaskBean, signBean: SignBean, signModel: Int) {
        packageState.postValue(PackageState.Loading)
        launchIO {
            val path = TaskRepository.packageApk(signBean, taskBean, signModel)
            if (path.startsWith("/")) {
                packageState.postValue(PackageState.Success(taskBean.apkPkg, path, taskBean.apkName))
            } else {
                packageState.postValue(PackageState.Fail(path))
            }
        }
    }

    fun installApk(pkg: String, path: String, appName: String, checkSign: Boolean = true) {
        launchIO {
            installState.postValue(InstallState.Loading)
            val isSameSign = ApkRepository.isApkSignSame(pkg, path)
            if (checkSign && !isSameSign) {
                installState.postValue(InstallState.NeedUnInstall(pkg, path, appName))
                return@launchIO
            }
            ApkRepository.install(path)
        }
    }

    fun uninstall(pkg: String) {
        launchIO {
            ApkRepository.uninstall(pkg)
        }
    }

    /**
     * 检查更新
     * @param notify Boolean 是否需要提醒
     */
    fun checkUpdate(notify: Boolean) {
        launch {
            val pair = withContext(Dispatchers.IO) {
                AppRepository.checkUpdate(!notify)
            }
            val info = pair.first
            if (info != null) {
                updateAppState.postValue(UpdateAppState.Update(info))
            } else if (notify) {
                updateAppState.postValue(UpdateAppState.NoUpdate(pair.second))
            }

        }
    }

    private val isDownloadApk = AtomicBoolean(false)

    /**
     * 下载
     * @param versionInfo VersionInfo
     */
    fun downloadApk(versionInfo: VersionBean) {
        if (isDownloadApk.getAndSet(true)) {
            return
        }

        launch {
            AppRepository.downloadApk(versionInfo)
                .flowOn(Dispatchers.IO)
                .catch {
                    updateAppState.postValue(UpdateAppState.DownloadFail(it.message.toString()))
                    isDownloadApk.set(false)
                }.onCompletion {
                    isDownloadApk.set(false)
                }.collectLatest {
                    if (it.first == 100) {
                        updateAppState.postValue(UpdateAppState.Install(it.second.toString()))
                    } else {
                        updateAppState.postValue(UpdateAppState.Downloading(it.first))
                    }
                }
        }
    }


}
