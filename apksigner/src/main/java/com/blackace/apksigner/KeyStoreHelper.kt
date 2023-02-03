package com.blackace.apksigner


import com.android.apksig.ApkSigner
import com.android.apksig.PasswordRetriever
import com.android.apksig.SignerParams
import net.fornwall.apksigner.KeyStoreFileManager
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.Security

/**
 *
 * @author: magicHeimdall
 * @create: 5/1/2023 15:46 PM
 */
object KeyStoreHelper {
    private const val ENABLE_V1 = 2 shl 1
    private const val ENABLE_V2 = 2 shl 2
    private const val ENABLE_V3 = 2 shl 3

    fun loadKeyStore(jksFile: File, jksPass: String): KeyStore? {
        var keyStore: KeyStore? = null

        //加载bks
        try {
            keyStore = KeyStore.getInstance("BKS", BouncyCastleProvider())
            loadPass(keyStore, jksFile, jksPass)
            return keyStore
        } catch (ignore: Exception) {

        }

        //加载jks
        try {
            keyStore = KeyStoreFileManager.loadKeyStore(jksFile.absolutePath, jksPass.toCharArray())
            return keyStore
        } catch (ignore: Exception) {

        }

        //加载PCKS12
        try {
            keyStore = KeyStore.getInstance("PKCS12")
            loadPass(keyStore, jksFile, jksPass)
            return keyStore
        } catch (ignore: Exception) {

        }
        //还不行就等死吧
        return keyStore
    }

    private fun loadPass(keyStore: KeyStore?, file: File, pass: String) {
        FileInputStream(file).use { fis -> keyStore!!.load(fis, pass.toCharArray()) }
    }

    fun convert2Bks(keyStore: KeyStore, aliasPass: String, bksFile: File, bksPass: String): Boolean {
        try {
            FileOutputStream(bksFile).use { bksOutput ->
                val bksKeyStore = KeyStore.getInstance("BKS", BouncyCastleProvider())
                Security.addProvider(BouncyCastleProvider())
                bksKeyStore.load(null, bksPass.toCharArray())
                val aliases = keyStore.aliases()

                while (aliases.hasMoreElements()) {
                    val alias = aliases.nextElement()
                    if (keyStore.isCertificateEntry(alias)) {
                        val certificate = keyStore.getCertificate(alias)
                        bksKeyStore.setCertificateEntry(alias, certificate)

                    } else if (keyStore.isKeyEntry(alias)) {
                        val key = keyStore.getKey(alias, aliasPass.toCharArray())
                        val certificates = keyStore.getCertificateChain(alias)
                        bksKeyStore.setKeyEntry(alias, key, bksPass.toCharArray(), certificates)

                    }
                }
                bksKeyStore.store(bksOutput, bksPass.toCharArray())
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun signApk(bksPath: String, apkPath: String, outPath: String, pass: String, enableSign: Int) {

        val signers: MutableList<SignerParams> = ArrayList(1)

        val signerParams = SignerParams()
        signerParams.setKeystoreFile(bksPath)
        signerParams.setKeystorePasswordSpec("pass:$pass")
        signers.add(signerParams)

        val signerConfigs = ArrayList<ApkSigner.SignerConfig>(signers.size)
        var signerNumber = 0

        PasswordRetriever().use {
            for (signer in signers) {
                signerNumber++
                signer.name = "signer #$signerNumber"
                signer.loadPrivateKeyAndCerts(it)
                val v1SigBasename: String? = if (signer.v1SigFileBasename != null) {
                    signer.v1SigFileBasename
                } else if (signer.keystoreKeyAlias != null) {
                    signer.keystoreKeyAlias
                } else if (signer.keyFile != null) {
                    val keyFileName = File(signer.keyFile).name
                    val delimiterIndex = keyFileName.indexOf('.')
                    if (delimiterIndex == -1) {
                        keyFileName
                    } else {
                        keyFileName.substring(0, delimiterIndex)
                    }
                } else {
                    throw RuntimeException(
                        "Neither KeyStore key alias nor private key file available"
                    )
                }
                val signerConfig =
                    ApkSigner.SignerConfig.Builder(v1SigBasename, signer.privateKey, signer.certs).build()
                signerConfigs.add(signerConfig)
            }
        }

        val enableV1 = enableSign and ENABLE_V1 != 0
        val enableV2 = enableSign and ENABLE_V2 != 0
        val enableV3 = enableSign and ENABLE_V3 != 0

        val apkSignerBuilder: ApkSigner.Builder = ApkSigner.Builder(signerConfigs)
            .setInputApk(File(apkPath))
            .setOutputApk(File(outPath))
            .setOtherSignersSignaturesPreserved(false)
            .setV1SigningEnabled(enableV1)
            .setV2SigningEnabled(enableV2)
            .setV3SigningEnabled(enableV3)
        val apkSigner = apkSignerBuilder.build()
        apkSigner.sign()
    }
}
