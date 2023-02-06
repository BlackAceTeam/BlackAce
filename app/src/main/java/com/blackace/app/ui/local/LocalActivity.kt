package com.blackace.app.ui.local

import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.app.contract.ResultCodes
import com.blackace.app.ui.file.FileDecoder
import com.blackace.app.ui.file.FileFragment
import com.blackace.app.ui.local.app.AppFragment
import com.blackace.data.entity.AppBean
import com.blackace.data.entity.http.FeatureBean
import com.blackace.data.state.CreateTaskState
import com.blackace.data.state.FeatureState
import com.blackace.databinding.ActivityLocalBinding
import com.blackace.databinding.ItemDialogFeatureBinding
import com.drake.brv.BindingAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16 下午9:41
 */
class LocalActivity : BaseActivity(), FileDecoder {

    private val binding by viewBinding(ActivityLocalBinding::bind)

    private val viewModel by viewModels<LocalViewModel>()

    private val fragmentList by lazy { arrayOf(AppFragment(), FileFragment()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local)
        setupToolbar(R.string.add_task)
        initTabLayout()
        initViewPager()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.featureState.observe(this) {
            when (it) {
                is FeatureState.Loading -> {
                    showLoadingDialog()
                }

                is FeatureState.Success -> {
                    dismissLoadingDialog()
                    showFeatureListDialog(it.appBean, it.list)
                }

                is FeatureState.Fail -> {
                    dismissLoadingDialog()
                    showSnackBar(it.msg)
                }
            }
        }

        viewModel.createTaskState.observe(this) {
            when (it) {
                is CreateTaskState.Loading -> {
                    showLoadingDialog()
                }

                is CreateTaskState.Success -> {
                    dismissLoadingDialog()
                    setResult(ResultCodes.CREATE_TASK)
                    finish()
                }

                is CreateTaskState.Fail -> {
                    dismissLoadingDialog()
                    showSnackBar(it.msg)
                }
            }
        }
    }

    private fun initTabLayout() {
        val tagArray = intArrayOf(R.string.local_application, R.string.local_file)
        tagArray.forEach {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it))
        }
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.setCurrentItem(tab?.position ?: 0, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun initViewPager() {
        binding.viewPager.adapter = LocalPagerAdapter(this, fragmentList)
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)?.select()
            }
        })
    }

    override fun fileClick(path: String) {
        if (path.endsWith(".apk")) {
            viewModel.loadFeatures(path)
        }
    }

    private fun showFeatureListDialog(appBean: AppBean, featureList: List<FeatureBean>) {

        featureAdapter.models = featureList

        MaterialDialog(this).show {
            title(text = appBean.name)
            icon(drawable = appBean.icon)
            message(R.string.feature)
            customListAdapter(featureAdapter)
            positiveButton(R.string.create_task) {
                val selectFeature = featureList.filter { it.isCheck }.joinToString(",") { it.key }
                viewModel.createTask(appBean, selectFeature)
            }
            negativeButton(R.string.cancel)
        }
    }

    private val featureAdapter by lazy {
        BindingAdapter().apply {
            addType<FeatureBean>(R.layout.item_dialog_feature)

            onBind {
                val binding = getBinding<ItemDialogFeatureBinding>()
                val bean = getModel<FeatureBean>()
                binding.tvDesc.text = bean.desc
                binding.tvTitle.text = bean.title
                binding.checkbox.isChecked = bean.isCheck
            }

            onChecked { position, checked, _ ->
                val model = getModel<FeatureBean>(position)
                model.isCheck = checked
                notifyItemChanged(position)
            }

            onClick(R.id.featureParent) {
                val checked = getModel<FeatureBean>().isCheck
                setChecked(bindingAdapterPosition, !checked)
            }

            onClick(R.id.checkbox) {
                setChecked(bindingAdapterPosition, !getModel<FeatureBean>().isCheck)
            }
        }
    }

}
