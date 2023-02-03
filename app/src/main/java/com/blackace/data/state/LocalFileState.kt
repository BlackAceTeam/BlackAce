package com.blackace.data.state

import com.blackace.data.entity.FileBean

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/18 上午12:55
 */
sealed interface LocalFileState {
    object Loading : LocalFileState

    data class Success(val list: List<FileBean>) : LocalFileState

}
