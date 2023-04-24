package com.blackace.app.ui.file

import androidx.lifecycle.MutableLiveData
import com.blackace.app.base.BaseViewModel
import com.blackace.data.LocalRepository
import com.blackace.data.entity.FileBean
import com.blackace.data.state.SimpleActionState

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/18 上午12:54
 */
class FileViewModel : BaseViewModel() {


    val fileListLiveData = MutableLiveData<SimpleActionState<List<FileBean>>>(SimpleActionState.Loading())


    fun loadFileList(path: String) {
        fileListLiveData.postValue(SimpleActionState.Loading())
        launchIO {
            val list = LocalRepository.listFile(path)
            fileListLiveData.postValue(SimpleActionState.Success(list))
        }
    }
}
