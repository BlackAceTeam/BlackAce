package com.blackace.app.ui.web

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.databinding.ActivityWebBinding

/**
 *
 * @author: magicHeimdall
 * @create: 10/2/2023 4:32 PM
 */

class WebActivity : BaseActivity() {

    private val binding by viewBinding(ActivityWebBinding::bind)

    private var url = ""

    private var uploadFileCallback: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        setupToolbar(R.string.app_name)
        url = intent.getStringExtra(INTENT_URL) ?: ""
        initWeb()
        initBack()
    }

    private fun initBack() {
        onBackPressedDispatcher.addCallback {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                finish()
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun initWeb() {
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        binding.webView.webViewClient = mWebViewClient
        binding.webView.webChromeClient = mChromeClient
        binding.webView.loadUrl(url)
    }


    private val mChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            setToolbarTitle(msg = title.toString())
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            uploadFileCallback = filePathCallback
            fileChooserParams?.let {
                selectFile.launch(arrayOf("*/*"))
            }

            return true
        }
    }

    private val mWebViewClient = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url != null) {
                binding.webView.loadUrl(url)
            }
            return true
        }
    }


    private val selectFile = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
        if (it != null) {
            uploadFileCallback?.onReceiveValue(it.toTypedArray())
        }
    }


    companion object {

        private const val INTENT_URL = "IntentUrl"

        private const val REQUEST_CODE = 109

        fun start(activity: BaseActivity, url: String?) {
            val intent = Intent()
            intent.setClass(activity, WebActivity::class.java)
            intent.putExtra(INTENT_URL, url)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }

    }
}