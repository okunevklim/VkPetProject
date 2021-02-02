package com.example.vk.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vk.R
import com.example.vk.adapters.CategoryAdapter
import com.example.vk.databinding.FragmentListBinding
import com.example.vk.utils.InternetConnectionChecker
import com.example.vk.viewmodels.PostsViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ListFragment : Fragment() {

    private lateinit var postsViewModel: PostsViewModel
    private lateinit var binding: FragmentListBinding
    private val tabIcons = intArrayOf(
        R.drawable.ic_profile_svg,
        R.drawable.ic_news,
        R.drawable.ic_liked
    )
    private var wasTabLayoutInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager2.offscreenPageLimit = 3
        binding.viewPager2.isUserInputEnabled = false
        binding.viewPager2.adapter = CategoryAdapter(requireActivity())
        val sections = arrayOf(
            getString(R.string.section_profile),
            getString(R.string.section_news),
            getString(R.string.section_liked)
        )
        TabLayoutMediator(
            binding.tabLayout, binding.viewPager2, true, false
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = sections[position]
        }.attach()
        setupTabIcons()
        postsViewModel = ViewModelProvider(requireActivity())[PostsViewModel::class.java]
        InternetConnectionChecker.checkInternetConnection(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                postsViewModel.getLikedPostsLiveData().observe(viewLifecycleOwner, {
                    if (!it.isNullOrEmpty()) {
                        binding.tabLayout.getTabAt(2)?.view?.visibility = View.VISIBLE
                    } else {
                        hideSharedTab()
                    }
                })
                postsViewModel.updateLikedPosts()
            } else {
                hideSharedTab()
            }
        })

        if (!wasTabLayoutInitialized) {
            binding.viewPager2.currentItem = 1
            binding.tabLayout.getTabAt(1)?.select()
            binding.tabLayout.getTabAt(2)?.view?.visibility = View.VISIBLE
            wasTabLayoutInitialized = true
        }
    }

    private fun hideSharedTab() {
        if (binding.tabLayout.selectedTabPosition == 2) {
            binding.tabLayout.getTabAt(1)?.select()
            binding.tabLayout.getTabAt(2)?.view?.visibility = View.GONE
        } else binding.tabLayout.getTabAt(2)?.view?.visibility = View.GONE
    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
        binding.tabLayout.getTabAt(2)?.setIcon(tabIcons[2])
    }
}