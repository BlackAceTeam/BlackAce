package com.blackace.data.state

import com.blackace.data.entity.AppBean
import com.blackace.data.entity.http.FeatureBean

/**
 *
 * @author: magicHeimdall
 * @create: 19/12/2022 3:49 PM
 */
sealed interface FeatureState {
    object Loading : FeatureState

    data class Success(val list: List<FeatureBean>,val appBean: AppBean):FeatureState

    data class Fail(val msg:String):FeatureState
}
