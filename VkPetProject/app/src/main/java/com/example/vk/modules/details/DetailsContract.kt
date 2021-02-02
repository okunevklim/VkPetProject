package com.example.vk.modules.details

import com.example.vk.models.VkComment
import com.example.vk.models.VkPost
import com.example.vk.models.VkUserInfo
import com.example.vk.modules.base.IPresenter
import com.example.vk.modules.base.IView

interface IDetailsView : IView<IDetailsPresenter?> {
    fun showDetailsProgressBar()
    fun hideDetailsProgressBar()
    fun showAlertDialog(message: Int)
    fun getInputTextToString(): String?
    fun showSuccessfulCommentToast()
    fun setVkComments(vkComments: ArrayList<VkComment>, commentInfo: ArrayList<VkUserInfo>)
    fun clearInputText()
    fun showSnackBar()
    fun showInputField()
    fun hideInputField()
    fun activateSendButton()
    fun deactivateSendButton()
    fun setupRecycler()
    fun setNumberOfComments(numberOfComments: Long)
    fun isShimmerLoading(isLoading: Int)
    fun updatePosition(position: Int)
    fun updateAmountOfLikes(likes: Long, userLikes: Int)
    fun handleIsRefreshing(value: Int)
    fun handleLike(vkPost: VkPost)
}

interface IDetailsPresenter : IPresenter<IDetailsView?> {
    fun postComment(vkPost: VkPost)
    fun getVkComments(vkPost: VkPost)
    fun getUserCommentsInfo(commentsIDS: String, vkComments: ArrayList<VkComment>)
    fun setLike(type: String, vkPost: VkPost, itemID: Long)
    fun deleteLike(type: String, vkPost: VkPost, itemID: Long)
}