package com.blackace.app.base

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.blackace.R
import com.blackace.app.view.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import java.util.LinkedList

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

    private val mBackCallbacks by lazy {
        val list = LinkedList<Pair<Lifecycle, (() -> Boolean)>>()
        registerBackDispatcher(list)
        list
    }

    private fun registerBackDispatcher(list: LinkedList<Pair<Lifecycle, () -> Boolean>>) {
        onBackPressedDispatcher.addCallback {
            for (pair in list) {
                if (pair.first.currentState == Lifecycle.State.DESTROYED) {
                    continue
                }

                if (pair.second.invoke()) {
                    return@addCallback
                }
            }
            finish()
        }
    }

    fun addBackCallback(lifecycle: Lifecycle, atFirst: Boolean = false, callback: (() -> Boolean)) {
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }
        if (atFirst) {
            mBackCallbacks.addFirst(lifecycle to callback)
        } else {
            mBackCallbacks.add(lifecycle to callback)
        }
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                removeBackCallback(lifecycle)
            }
        })
    }

    fun removeBackCallback(lifecycle: Lifecycle) {
        for (index in mBackCallbacks.indices) {
            val pair = mBackCallbacks[index]
            if (pair.first == lifecycle) {
                mBackCallbacks.removeAt(index)
                return
            }
        }
    }

    override fun onPause() {
        super.onPause()
        dismissLoadingDialog()
    }
}
