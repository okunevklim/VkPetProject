package com.example.vk.modules.liked

import android.graphics.Bitmap
import com.example.vk.models.VkPost
import com.example.vk.modules.base.IPresenter
import com.example.vk.modules.base.IView

interface ILikedView : IView<ILikedPresenter?> {
    fun onShareButtonClick(bitmap: Bitmap)
    fun onPostClick(
            vkPost: VkPost,
            name: String,
            photoURL: String,
            canPost: Int,
            numberOfComments: Long
    )

    fun hideShimmerLayout()
    fun showShimmerLayout()
    fun hideErrorLayout()
    fun showErrorLayout()
    fun showAlertDialog(messageID: Int)
    fun handleLike(vkPost: VkPost)
}

interface ILikedPresenter : IPresenter<ILikedView?> {
    fun setLike(vkPost: VkPost)
    fun deleteLike(vkPost: VkPost)
}