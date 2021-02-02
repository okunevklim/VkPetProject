package com.example.vk.modules.profile

import android.graphics.Bitmap
import com.example.vk.models.VkCityName
import com.example.vk.models.VkPost
import com.example.vk.models.VkUserInfo
import com.example.vk.modules.base.IPresenter
import com.example.vk.modules.base.IView

interface IProfileView : IView<IProfilePresenter?> {
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
    fun handlePosts(updVkItems: List<VkPost>, needToScrollUp: Boolean)
    fun showToast()
    fun setCareerCities(cities: List<VkCityName>)
    fun handleError()
    fun setUserInfo(userInfo: VkUserInfo)
    fun showSuccessfulSnackBar()
    fun setCities(cities: List<VkCityName>)
    fun checkInternetConnection()
    fun handleUserInfo(userInfo: VkUserInfo)
    fun handleLikedPosts(posts: ArrayList<VkPost>)
    fun handleLikedProfilePostsUserInfo(userInfo: VkUserInfo)
}

interface IProfilePresenter : IPresenter<IProfileView?> {
    fun getUserPosts(silentMode: Boolean)
    fun loadUserInfo()
    fun loadCitiesByID(cityID: List<Int>)
    fun setLike(vkPost: VkPost)
    fun deleteLike(vkPost: VkPost)
    fun deletePost(vkPost: VkPost)
}