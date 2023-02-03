package com.blackace.data.http

import com.blackace.data.config.AceConfig
import com.blackace.util.ext.log
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response

/**
 *
 * @author: magicHeimdall
 * @create: 3/1/2023 9:07 PM
 */
class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val body = request.body
        val user = AceConfig.getUser()

        if (request.method == "GET" && user != null) {
            val url = request.url.newBuilder().addQueryParameter("_key", user.token).build()
            request = request.newBuilder().url(url).build()
        } else if (body is FormBody && user != null) {
            val newBody = FormBody.Builder()
            for (index in 0 until body.size) {
                newBody.addEncoded(body.encodedName(index), body.encodedValue(index))
            }
            newBody.addEncoded("_key", user.token)
            request = request.newBuilder().method(request.method, newBody.build()).build()
        }

        return chain.proceed(request)
    }
}
