package com.blackace.app.ui.sign

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.R
import com.blackace.app.base.BaseFragment
import com.blackace.data.entity.db.SignBean
import com.blackace.data.state.SignListState
import com.blackace.data.state.SignManagerState
import com.blackace.databinding.FragmentSignBinding
import com.blackace.databinding.ItemSignManagerBinding
import com.blackace.util.ext.showConfirmDialog
import com.drake.brv.utils.*

/**
 *
 * @author: magicHeimdall
 * @create: 5/1/2023 2:42 PM
 */
class SignFragment : BaseFragment(R.layout.fragment_sign) {

    private val binding by viewBinding(FragmentSignBinding::bind)

    private val viewModel by activityViewModels<SignViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.loadLocalSign()
        viewModel.signListState.observe(viewLifecycleOwner) {
            when (it) {
                is SignListState.Loading -> {
                    binding.stateView.showLoading()
                }

                is SignListState.Success -> {
                    if (it.list.isEmpty()) {
                        binding.stateView.showEmpty(getString(R.string.no_sign))
                    } else {
                        binding.recyclerView.models = it.list
                        binding.stateView.showContent()
                    }
                }
            }
        }

        viewModel.signManagerState.observe(viewLifecycleOwner) {
            when (it) {
                is SignManagerState.CreateSuccess -> {
                    binding.recyclerView.addModels(listOf(it.bean))
                }

                is SignManagerState.Delete -> {
                    binding.recyclerView.mutable.removeAt(it.position)
                    if (binding.recyclerView.models.isNullOrEmpty()) {
                        binding.stateView.showEmpty(getString(R.string.no_sign))
                    } else {
                        binding.recyclerView.adapter?.notifyItemRemoved(it.position)
                    }
                }
                else -> {}
            }
        }

    }

    private fun initRecyclerView() {
        binding.recyclerView.grid(2).setup {
            addType<SignBean>(R.layout.item_sign_manager)
            onBind {
                val model = getModel<SignBean>()
                val binding = getBinding<ItemSignManagerBinding>()
                binding.tvTitle.text = model.name
                binding.tvAlias.text = model.aliasName
            }

            onClick(R.id.ivMenu) {
                val binding = getBinding<ItemSignManagerBinding>()
                showMenu(bindingAdapterPosition, binding.ivMenu, getModel())
            }
        }
    }

    private fun showMenu(position: Int, view: View, bean: SignBean) {
        val menu = PopupMenu(requireContext(), view)
        menu.inflate(R.menu.menu_sign_action)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sign_manager_delete -> {
                    showConfirmDialog(R.string.delete_sign, R.string.delete_sign_hint) {
                        viewModel.removeSign(position, bean.id, bean.path)
                    }
                }
            }
            true
        }
        menu.show()
    }
}
