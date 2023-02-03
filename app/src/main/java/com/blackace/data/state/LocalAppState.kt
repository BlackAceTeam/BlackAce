package com.blackace.data.state

import com.blackace.data.entity.AppBean

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/14-下午6:24
 */
sealed interface LocalAppState {
    object Loading : LocalAppState

    data class Success(val list: List<AppBean>) : LocalAppState
}
