package com.blackace.data

import com.blackace.data.config.AceConfig
import com.blackace.data.entity.UserBean
import com.blackace.data.entity.http.BaseResult
import com.blackace.data.entity.http.EmailBean
import com.blackace.util.holder.ApiHolder

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 9:02 下午
 */
object UserRepository {

    suspend fun login(username: String, password: String): String? {
        try {
            val result = ApiHolder.api.login(username, password)
            if (result.isSuccess() && result.result != null) {
                val data = result.result!!
                val userBean = UserBean(data.account, data.email, data.token, data.registerTime)
                AceConfig.saveUser(userBean)
                return null
            }

            return result.msg
        } catch (e: Exception) {
            return e.message
        }

    }

    suspend fun register(username: String, email: String, password: String): String? {
        try {
            val result = ApiHolder.api.register(username, password, email)
            if (result.isSuccess() && result.result != null) {
                val data = result.result!!
                val userBean = UserBean(data.account, data.email, data.token, data.registerTime)
                AceConfig.saveUser(userBean)
                return null
            }

            return result.msg
        } catch (e: Exception) {
            return e.message
        }
    }

    suspend fun changePassword(verify: String, newPass: String, account: String): String? {
        try {
            val result = ApiHolder.api.changePassword(verify, newPass, account)
            if (result.isSuccess()) {
                return null
            }
            return result.msg
        } catch (e: Exception) {
            return e.message
        }
    }

    suspend fun sendEmailVerify(account: String): BaseResult<EmailBean> {
        return try {
            ApiHolder.api.sendEmailVerify(account)
        } catch (e: Exception) {
            BaseResult.fail(e.message.toString())
        }
    }

}
