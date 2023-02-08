package com.blackace.app.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *@author: magicHeimdall
 *@create: 13/12/2022-11:52 上午
 */
open class BaseViewModel : ViewModel() {


    fun launch(block: suspend () -> Unit) {
        viewModelScope.launch {
            block.invoke()
        }
    }

    fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block.invoke(this)
        }
    }

    suspend fun <T> withIOContext(block: () -> T): T {
        return withContext(Dispatchers.IO) {
            block.invoke()
        }
    }
}
