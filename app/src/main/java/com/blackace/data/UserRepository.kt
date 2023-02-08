package com.blackace.data

import com.blackace.data.config.AceConfig
import com.blackace.data.entity.UserBean
import com.blackace.data.entity.http.BaseResult
import com.blackace.data.state.LoginState
import com.blackace.util.ext.log
import com.blackace.util.holder.ApiHolder
import kotlinx.coroutines.delay

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 9:02 下午
 */
object UserRepository {

    suspend fun login(username: String, password: String): String? {
        val result = ApiHolder.api.login(username, password)
        if (result.isSuccess() && result.result != null) {
            val data = result.result!!
            val userBean = UserBean(data.id, data.account, data.email, data.token)
            AceConfig.saveUser(userBean)
            return null
        }

        return result.msg

    }

    suspend fun register(username: String, email: String, password: String): String? {
        val result = ApiHolder.api.register(username, password, email)
        if (result.isSuccess() && result.result != null) {
            val data = result.result!!
            val userBean = UserBean(data.id, data.account, data.email, data.token)
            AceConfig.saveUser(userBean)
            return null
        }

        return result.msg
    }

    suspend fun changePassword(verify: String, newPass: String,account: String): String? {
        val result = ApiHolder.api.changePassword(verify, newPass,account)
        if (result.isSuccess()) {
            return null
        }
        return result.msg
    }

    suspend fun sendEmailVerify(account: String): String? {
        val result = ApiHolder.api.sendEmailVerify(account)
        if (result.isSuccess() && result.result != null) {
            return null
        }
        return result.msg
    }

}
