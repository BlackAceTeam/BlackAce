package com.blackace.util

import com.blackace.util.ext.hexStringToByte
import com.blackace.util.ext.log
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 *
 * @author: magicHeimdall
 * @create: 22/3/2023 3:08 PM
 */
object DesEncrypt {

    private val decryptCipher = Cipher.getInstance("DES")

    init {

        val key = getKey("google.com".toByteArray())
        decryptCipher.init(Cipher.DECRYPT_MODE, key)
    }

    fun decrypt(encryptText: String?): String {
        if (encryptText.isNullOrEmpty()) {
            return ""
        }
        return try {
            val result = decryptCipher.doFinal(encryptText.hexStringToByte())
            String(result)
        } catch (e: Exception) {
            e.printStackTrace()
            log(e)
            ""
        }
    }

    private fun getKey(arrBTmp: ByteArray): Key {
        // 创建一个空的8位字节数组（默认值为0）
        val arrB = ByteArray(8)
        // 将原始字节数组转换为8位
        var i = 0
        while (i < arrBTmp.size && i < arrB.size) {
            arrB[i] = arrBTmp[i]
            i++
        }
        // 生成密钥
        return SecretKeySpec(arrB, "DES")
    }
}
