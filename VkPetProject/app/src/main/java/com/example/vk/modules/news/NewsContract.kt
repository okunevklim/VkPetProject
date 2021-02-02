package com.example.vk.modules.news

import android.graphics.Bitmap
import com.example.vk.models.VkGroup
import com.example.vk.models.VkPost
import com.example.vk.models.VkUserInfo
import com.example.vk.modules.base.IPresenter
import com.example.vk.modules.base.IView

interface INewsView : IView<INewsPresenter?> {
    fun hideShimmerLayout()
    fun showShimmerLayout()
    fun hideErrorLayout()
    fun showErrorLayout()
    fun showAlertDialog(messageID: Int)
    fun onPostClick(
            vkPost: VkPost,
            name: String,
            photoURL: String,
            canPost: Int,
            numberOfComments: Long
    )

    fun onShareButtonClick(bitmap: Bitmap)
    fun handlePosts(updVkItems: List<VkPost>)
    fun setNewsInfo(vkPosts: List<VkPost>, users: ArrayList<VkUserInfo>, groups: ArrayList<VkGroup>)
    fun handleError()
    fun showSnackBar(title: Int, backgroundColor: Int, image: Int)
    fun stopRefreshing()
    fun handleProfiles(profiles: ArrayList<VkUserInfo>)
    fun handleGroups(groups: ArrayList<VkGroup>)
    fun handleLikedPosts(likedPosts: ArrayList<VkPost>)
}

interface INewsPresenter : IPresenter<INewsView?> {
    fun getUserNews(silentMode: Boolean)
    fun setLike(vkPost: VkPost)
    fun deleteLike(vkPost: VkPost)
    fun deletePost(vkPost: VkPost)
    fun hidePost(vkPost: VkPost)
    fun checkType(type: String): String
}