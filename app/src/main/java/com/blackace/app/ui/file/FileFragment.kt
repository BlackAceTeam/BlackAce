package com.blackace.app.ui.file

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.fragment.app.commit
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.R
import com.blackace.app.base.BaseFragment
import com.blackace.app.contract.StoragePermissionContract
import com.blackace.databinding.FragmentFileBinding
import com.blackace.util.ext.hide


/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/17 下午11:55
 */
class FileFragment : BaseFragment(R.layout.fragment_file) {

    private val binding by viewBinding(FragmentFileBinding::bind)

    private var hasPermission = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
        initView()
        initData()
    }


    override fun onStart() {
        super.onStart()
        if (!hasPermission){
            checkPermission()
            initData()
        }
    }

    private fun initView() {
        binding.requestBtn.setOnClickListener {
            requestStoragePermissionContract.launch(Unit)
        }
    }

    private fun initData() {
        if (hasPermission) {
            hideRequestView()
            nextFragment(Environment.getExternalStorageDirectory().absolutePath)
        }
    }

    private fun hideRequestView() {
        binding.requestBtn.hide()
        binding.requestHint.hide()
    }

    private fun nextFragment(path: String) {
        val fragment = FileChildFragment.create(path)
        childFragmentManager.commit {
            add(R.id.fragmentFile, fragment)
            addToBackStack(path)
        }
    }


    private fun checkPermission(){
        hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }else{
            true
        }
    }

    override fun onBack(): Boolean {
        val count = childFragmentManager.backStackEntryCount

        return if (count > 1) {
            childFragmentManager.popBackStack()
            false
        } else {
            true
        }
    }

    private val requestStoragePermissionContract =
        registerForActivityResult(StoragePermissionContract()) {
            if (it) {
                hasPermission = true
                initData()
            }
        }
}
