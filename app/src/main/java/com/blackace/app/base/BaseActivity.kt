package com.blackace.app.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.blackace.R
import com.blackace.app.view.LoadingDialog
import com.google.android.material.snackbar.Snackbar

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16 下午9:35
 */
open class BaseActivity : AppCompatActivity() {

    protected lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    open fun setupToolbar(title: Int, navigationIconClick: (() -> Unit)? = { finish() }) {
        toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(title)

        setSupportActionBar(toolbar)

        if (navigationIconClick != null) {
            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                toolbar.setNavigationOnClickListener {
                    navigationIconClick.invoke()
                }
            }
        }

    }

    fun setToolbarTitle(id: Int? = null, msg: String? = null) {
        if (id != null) {
            supportActionBar?.setTitle(id)
        } else if (msg != null) {
            supportActionBar?.title = msg
        }
    }


    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog()
    }

    fun showLoadingDialog() {
        runCatching {
            if (!mLoadingDialog.isAdded) {
                mLoadingDialog.show(supportFragmentManager, "Loading")
            }
        }
    }

    fun dismissLoadingDialog() {
        runCatching {
            mLoadingDialog.dismiss()
        }
    }


    fun showSnackBar(id: Int) {
        showSnackBar(getString(id))
    }

    fun showSnackBar(msg: String) {
        val view = findViewById<View>(android.R.id.content)
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }
}
