package com.blackace.app.ui.main

import androidx.lifecycle.MutableLiveData
import com.blackace.app.base.BaseViewModel
import com.blackace.data.ApkRepository
import com.blackace.data.SignRepository
import com.blackace.data.TaskRepository
import com.blackace.data.config.AceConfig
import com.blackace.data.entity.db.SignBean
import com.blackace.data.entity.http.TaskBean
import com.blackace.data.state.InstallState
import com.blackace.data.state.PackageState
import com.blackace.data.state.TaskListState
import com.blackace.data.state.UserState
import kotlinx.coroutines.delay

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

    var taskStart: String = ""

    init {
        loadUserState()
    }

    fun loadUserState() {
        launchIO {
            val userBean = AceConfig.getUser()
            if (userBean == null) {
                userState.postValue(UserState.NoLogin)
            } else {
                userState.postValue(UserState.Login(userBean))
            }

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

    fun launchApk(pkg: String) {
        launchIO {
            ApkRepository.launch(pkg)
        }
    }
}
