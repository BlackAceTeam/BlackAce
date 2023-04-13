package com.blackace.data.http

import com.blackace.data.config.AceConfig
import com.blackace.util.DesEncrypt
import com.blackace.util.ext.log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author: magicHeimdall
 * @create: 21/3/2023 9:37 PM
 */
object HostManager {

    private val isReloaded = AtomicBoolean(false)

    private val needReloadHost = AtomicBoolean(false)

    private val fastHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()
    }


    private val configAddList = arrayOf("https://proj-ace.oss-cn-hongkong.aliyuncs.com/config/dev.cap")


    fun getBestHost(): String {
        log("isReloaded${isReloaded.get()},needReloadHost${needReloadHost.get()}")
        if (isReloaded.get() || !needReloadHost.get()) {
            //如果重新加载过了，
            //或者不需要重新加载
            // 那就走正常流程了
            log("直接返回host")
            return getLocalHost()
        }

        synchronized(this) {
            //加锁，如果没有加载过，就去加载
            if (!isReloaded.get()) {
                val lock = CountDownLatch(1)
                reloadHostList(lock)
                lock.await()
                //等待加载完成
            }
        }

        return getLocalHost()

    }

    fun needReloadHost() {
        log("需要加载新host，删除旧host")
        AceConfig.saveCustomHost("" to 0L)
        needReloadHost.set(true)
    }

    private fun getLocalHost(): String {
        log("获取host")
        val customHost = AceConfig.getCustomHost()
        if (customHost.first.isNotEmpty()) {
            if (customHost.second < System.currentTimeMillis()) {
                log("使用自定义host")
                return customHost.first
            } else {
                log("自定义host已过期")
            }
        }
        log("使用默认host")
        return "https://goolgostat.com"
    }


    private fun reloadHostList(lock: CountDownLatch) {
        log("刷新host")

        if (isReloaded.get()) {
            log("多次刷新，直接返回，使用默认host")
            needReloadHost.set(false)
            lock.countDown()
            return
        }
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            val hostConfig = getHostConfig(this)
            if (hostConfig.isNullOrEmpty()) {
                //todo
                return@launch
            }

            log("get hostConfig Success")
            log(hostConfig)
            val hostList = convertHostConfig(hostConfig)
            log("convert")
            val bestHost = getFastHost(scope, hostList)
            if (bestHost == null) {
                log("null")
                return@launch
            }
            log("get fastHost success")
            log(bestHost)
            AceConfig.saveCustomHost(bestHost)
            isReloaded.set(true)
        }.invokeOnCompletion {
            log("unlock")
            needReloadHost.set(false)
            lock.countDown()
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getHostConfig(scope: CoroutineScope): String? {
        val channel = Channel<String>(Channel.CONFLATED)
        val requestCount = AtomicInteger()
        configAddList.forEach {
            scope.async {
                val encryptResult = httpGet(it)
                val result = DesEncrypt.decrypt(encryptResult)
                if (result.isNotEmpty() && !channel.isClosedForSend) {
                    channel.send(result)
                    channel.close()
                }
                if (requestCount.addAndGet(1) == configAddList.size){
                    channel.close()
                    //请求全部完成的时候关闭队列，防止死锁
                }
            }
        }
        return channel.receiveCatching().getOrNull()
    }


    private fun convertHostConfig(config: String): List<Pair<String, Long>> {
        val list = arrayListOf<Pair<String, Long>>()
        config.split("\n").forEach {
            val hostWithExt = it.split(",")
            if (hostWithExt.size != 2) {
                return@forEach
            }
            val ext = hostWithExt[1].toLong()
            val extTime = if (ext == 0L) {
                0L
            } else {
                System.currentTimeMillis() + ext
            }
            list.add(hostWithExt[0] to extTime)
        }
        return list
    }


    private suspend fun getFastHost(
        scope: CoroutineScope,
        list: List<Pair<String, Long>>
    ): Pair<String, Long>? {
        val channel = Channel<Pair<String, Long>>(Channel.CONFLATED)
        val requestCount = AtomicInteger()

        log("获取速度最快host")
        list.forEach {
            scope.async {
                val result = httpGet("${it.first}/test")
                log(it.first+result)
                if (result == "success" && !channel.isClosedForSend) {
                    channel.send(it)
                    channel.close()
                }

                if (requestCount.addAndGet(1) == list.size){
                    log("全部失败")
                    channel.close()
                    //请求全部完成的时候关闭队列，防止死锁
                }
            }
        }
        return channel.receiveCatching().getOrNull()
    }


    /**
     * get请求
     */
    private fun httpGet(url: String): String {
        return try {
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            val call = fastHttpClient.newCall(request)
            val response = call.execute()
            response.body?.string() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

}
