package com.example.vk.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vk.models.VkGroup
import com.example.vk.models.VkPost
import com.example.vk.models.VkUserInfo

class PostsViewModel : ViewModel() {

    private lateinit var likedPostsUserInfo: VkUserInfo
    private val likedPostsLiveData: MutableLiveData<ArrayList<VkPost>>
            by lazy { MutableLiveData<ArrayList<VkPost>>() }
    private val likedPostsProfilesLiveData: MutableLiveData<ArrayList<VkUserInfo>>
            by lazy { MutableLiveData<ArrayList<VkUserInfo>>() }
    private val likedPostsGroupsLiveData: MutableLiveData<ArrayList<VkGroup>>
            by lazy { MutableLiveData<ArrayList<VkGroup>>() }
    private val likedPostsUserInfoLiveData: MutableLiveData<VkUserInfo>
            by lazy { MutableLiveData<VkUserInfo>() }
    private val isRefreshingLiveData: MutableLiveData<Int>
            by lazy { MutableLiveData<Int>() }
    private val likedPosts = arrayListOf<VkPost>()
    private val likedPostsProfiles = arrayListOf<VkUserInfo>()
    private val likedPostsGroups = arrayListOf<VkGroup>()
    private var isRefreshing = 0

    fun getLikedPostsLiveData(): LiveData<ArrayList<VkPost>> {
        return likedPostsLiveData
    }

    fun getIsRefreshingLiveData(): LiveData<Int> {
        return isRefreshingLiveData
    }

    fun updateLikedPosts() {
        likedPostsLiveData.value = likedPosts
    }

    private fun updateIsRefreshingProfilePosts() {
        isRefreshingLiveData.value = isRefreshing
    }

    fun handleIsRefreshing(value: Int) {
        isRefreshing = value
        updateIsRefreshingProfilePosts()
    }

    fun handleLikedPosts(posts: ArrayList<VkPost>?) {
        val idArray = ArrayList<Long>()
        var d = 0
        while (d < likedPosts.size) {
            if (!idArray.contains(likedPosts[d].id)) {
                idArray.add(likedPosts[d].id ?: 0)
            }
            d++
        }
        if (!posts.isNullOrEmpty()) {
            var c = 0
            while (c < posts.size) {
                if (!idArray.contains(posts[c].id)) {
                    likedPosts.add(posts[c])
                } else {
                    val element = likedPosts.find {
                        idArray.contains(posts[c].id)
                    }
                    element to posts[c]
                }
                c++
            }
        }
        updateLikedPosts()
    }

    fun handleLike(post: VkPost) {
        if (likedPosts.contains(post)) {
            likedPosts.remove(post)
        } else {
            likedPosts.add(post.apply {
                likes?.apply {
                    count = count ?: 0 + 1
                    userLikes = 1
                }
            })

        }
        updateLikedPosts()
    }

    /**Methods for the own Information*/

    private fun updateLikedPostsUserInfo() {
        likedPostsUserInfoLiveData.value = likedPostsUserInfo
        val userInfo = arrayListOf<VkUserInfo>()
        userInfo.add(likedPostsUserInfo)
        handleProfiles(userInfo)
    }

    fun handleLikedProfilePostsUserInfo(userInfo: VkUserInfo?) {
        if (userInfo != null) {
            likedPostsUserInfo = userInfo
        }
        updateLikedPostsUserInfo()
    }

    /**Methods for the Users and Groups Head-Info*/

    fun getLikedPostsProfileInfoLiveData(): LiveData<ArrayList<VkUserInfo>> {
        return likedPostsProfilesLiveData
    }

    fun getLikedPostsGroupInfoLiveData(): LiveData<ArrayList<VkGroup>> {
        return likedPostsGroupsLiveData
    }

    private fun updateLikedPostsProfilesInfo() {
        likedPostsProfilesLiveData.value = likedPostsProfiles
    }

    private fun updateLikedPostsGroupsInfo() {
        likedPostsGroupsLiveData.value = likedPostsGroups
    }

    fun handleProfiles(profiles: ArrayList<VkUserInfo>) {
        likedPostsProfiles.addAll(profiles)
        updateLikedPostsProfilesInfo()
    }

    fun handleGroups(groups: ArrayList<VkGroup>) {
        likedPostsGroups.addAll(groups)
        updateLikedPostsGroupsInfo()
    }
}