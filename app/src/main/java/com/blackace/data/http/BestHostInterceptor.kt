package com.blackace.data.http

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author: magicHeimdall
 * @create: 21/3/2023 4:47 PM
 */
class BestHostInterceptor : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val retryCount = AtomicInteger(0)
        while (retryCount.getAndAdd(1) <= 3) {
            runCatching {
                val response = proceed(chain)
                if (response != null && response.isSuccessful) {
                    return response
                } else {
                    HostManager.needReloadHost()
                }
                response?.body?.close()
                //先关闭旧请求
            }
        }

        throw IOException("Request Fail")
    }

    private fun proceed( chain: Interceptor.Chain): Response? {
        return try {
            val oldRequest = chain.request()
            val oldUrl = oldRequest.url
            val oldHost = oldUrl.scheme + "://" + oldUrl.host
            val newUrl = oldUrl.toString().replace(oldHost, HostManager.getBestHost())
            val newRequest = oldRequest.newBuilder().url(newUrl).build()
            chain.proceed(newRequest)
        } catch (e: Exception) {
            null
        }
    }

}
