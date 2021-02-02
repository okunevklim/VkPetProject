package com.example.vk.modules.input

import android.annotation.SuppressLint
import com.example.vk.R
import com.example.vk.base.VkApplication
import com.example.vk.di.module.Api
import com.example.vk.models.VkCreatePostResponse
import com.example.vk.modules.base.Presenter
import com.example.vk.utils.Constants
import com.example.vk.utils.PreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class InputPresenter internal constructor() : Presenter<IInputView?>(), IInputPresenter {

    @Inject
    lateinit var api: Api

    init {
        VkApplication.component.inject(this)
    }

    override fun onViewAttached() {}
    override fun onViewDetached() {}

    @SuppressLint("CheckResult")
    override fun sendPost(text: String) {
        view?.showInputProgressBar()
        api.getService().createWallPost(
                text,
                Constants.VERSION_API,
                PreferencesHelper.getRefreshToken()
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { postResponse: VkCreatePostResponse?, _: Throwable? ->
                    val post = postResponse?.response
                    view?.handleIsRefreshing(if (post == null) 0 else 1)
                    view?.hideInputProgressBar()
                    if (post != null) {
                        view?.showSuccessfulToast()
                        view?.closeInputFragment()
                    } else {
                        view?.showPostAlertDialog(R.string.creating_post_failed)
                    }
                }
    }
}