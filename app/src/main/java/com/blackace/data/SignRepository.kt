package com.blackace.data

import com.blackace.R
import com.blackace.data.entity.db.SignBean
import com.blackace.apksigner.KeyStoreHelper
import com.blackace.util.getString
import com.blackace.util.holder.ContextHolder
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 *
 * @author: magicHeimdall
 * @create: 5/1/2023 2:46 PM
 */
object SignRepository {

    fun loadSignList(): List<SignBean> {
        return database.signDao().all()
    }

    fun loadSignWithDefault(): List<SignBean> {
        val list = mutableListOf<SignBean>()

        val bksFile = File(ContextHolder.get().filesDir, "sign/blackace.bks")
        if(!bksFile.exists()){
            bksFile.parentFile?.mkdirs()
            ContextHolder.get().assets.open("blackace.bks").copyTo(FileOutputStream(bksFile))
        }
        list.add(SignBean(-1, getString(R.string.default_sign), bksFile.absolutePath, "blackace", "blackace", "blackace"))
        list.addAll(database.signDao().all())
        return list
    }

    fun addSign(path: String, password: String, alias: String, aliasPass: String): Pair<String, SignBean?> {
        val jksFile = File(path)
        val keyStore =
            KeyStoreHelper.loadKeyStore(jksFile, password) ?: return getString(R.string.sign_pass_error) to null
        val bksFile = File(ContextHolder.get().filesDir, "sign/${UUID.randomUUID()}.bks")
        bksFile.parentFile?.mkdirs()

        val result = KeyStoreHelper.convert2Bks(keyStore, aliasPass, bksFile, password)
        if (!result) {
            bksFile.delete()
            return getString(R.string.sign_alias_pass_error) to null
        }

        val bean = SignBean(0, jksFile.nameWithoutExtension, bksFile.absolutePath, password, alias, aliasPass)
        database.signDao().insert(bean)
        return "" to bean
    }

    fun removeSign(signId: Int, signPath: String) {
        database.signDao().delete(signId)
        File(signPath).delete()
    }
}
