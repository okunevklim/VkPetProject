package com.example.vk.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vk.modules.liked.LikedFragment
import com.example.vk.modules.news.NewsFragment
import com.example.vk.modules.profile.ProfileFragment

class CategoryAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return COUNT_NUMBER
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragment()
            1 -> NewsFragment()
            2 -> LikedFragment()
            else -> throw IllegalArgumentException("invalid position=$position")
        }
    }

    companion object {
        const val COUNT_NUMBER = 3
    }
}