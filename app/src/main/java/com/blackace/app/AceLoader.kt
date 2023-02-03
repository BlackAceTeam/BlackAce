package com.blackace.app

import android.widget.TextView
import com.blackace.R
import com.blackace.util.ext.log
import com.drake.statelayout.StateConfig
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *
 * @author: magicHeimdall
 * @create: 4/1/2023 8:44 PM
 */
object AceLoader {

    fun initSmartRefresh() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> MaterialHeader(context) }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> ClassicsFooter(context) }
    }

    fun initStateLayout() {
        StateConfig.apply {
            emptyLayout = R.layout.state_empty // 配置全局的空布局
            errorLayout = R.layout.state_error // 配置全局的错误布局
            loadingLayout = R.layout.state_loading // 配置全局的加载中布局

            onEmpty {
                if (it != null) {
                    findViewById<TextView>(R.id.tvMsg).text = it.toString()
                }
            }

            onError {
                if (it != null) {
                    findViewById<TextView>(R.id.tvMsg).text = it.toString()
                }
            }
        }
    }
}
