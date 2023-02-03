package com.blackace.app.ui.local

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blackace.app.base.BaseFragment

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/18 上午12:13
 */
class LocalPagerAdapter(activity: FragmentActivity,private val fragmentList: Array<BaseFragment>) : FragmentStateAdapter(activity) {


    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}
