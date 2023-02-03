package com.blackace.app

import android.app.Application
import com.blackace.util.holder.ContextHolder
import com.tencent.mmkv.MMKV

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16 下午9:38
 */
class AceApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        ContextHolder.init(this)
        MMKV.initialize(this)
        AceLoader.initSmartRefresh()
        AceLoader.initStateLayout()
    }
}
