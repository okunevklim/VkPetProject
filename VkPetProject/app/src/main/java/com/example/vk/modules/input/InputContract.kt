package com.example.vk.modules.input

import com.example.vk.modules.base.IPresenter
import com.example.vk.modules.base.IView

interface IInputView : IView<IInputPresenter?> {
    fun showSuccessfulToast()
    fun showInputProgressBar()
    fun hideInputProgressBar()
    fun showPostAlertDialog(messageID: Int)
    fun closeInputFragment()
    fun setSendButton(image: Int, isEnabled: Boolean)
    fun handleIsRefreshing(value: Int)
}

interface IInputPresenter : IPresenter<IInputView?> {
    fun sendPost(text: String)
}