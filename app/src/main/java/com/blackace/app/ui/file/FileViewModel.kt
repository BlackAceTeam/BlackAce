package com.blackace.app.ui.file

import androidx.lifecycle.MutableLiveData
import com.blackace.app.base.BaseViewModel
import com.blackace.data.LocalRepository
import com.blackace.data.state.LocalFileState

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/18 上午12:54
 */
class FileViewModel : BaseViewModel() {


    val fileListLiveData = MutableLiveData<LocalFileState>(LocalFileState.Loading)


    fun loadFileList(path: String) {
        fileListLiveData.postValue(LocalFileState.Loading)
        launchIO {
            val list = LocalRepository.listFile(path)
            fileListLiveData.postValue(LocalFileState.Success(list))
        }
    }
}
