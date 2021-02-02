package com.example.vk.modules.liked

import android.annotation.SuppressLint
import com.example.vk.R
import com.example.vk.base.VkApplication
import com.example.vk.di.module.Api
import com.example.vk.models.VkLikeInfoResponse
import com.example.vk.models.VkPost
import com.example.vk.modules.base.Presenter
import com.example.vk.utils.Constants.VERSION_API
import com.example.vk.utils.PreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LikedPresenter internal constructor() : Presenter<ILikedView?>(), ILikedPresenter {

    @Inject
    lateinit var api: Api

    init {
        VkApplication.component.inject(this)
    }

    override fun onViewAttached() {}
    override fun onViewDetached() {}

    @SuppressLint("CheckResult")
    override fun setLike(vkPost: VkPost) {
        api.getService().setLike(
                vkPost.postType ?: "post",
                vkPost.ownerId ?: 0,
                VERSION_API,
                PreferencesHelper.getRefreshToken(),
                vkPost.id ?: 0
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { likeResponse: VkLikeInfoResponse?, _: Throwable? ->
                    val like = likeResponse?.response
                    if (like != null) {
                        view?.handleLike(vkPost)
                    } else {
                        view?.showAlertDialog(R.string.alert_msg)
                    }
                }
    }

    @SuppressLint("CheckResult")
    override fun deleteLike(vkPost: VkPost) {
        api.getService().deleteLike(
                vkPost.postType ?: "post",
                vkPost.ownerId ?: 0,
                VERSION_API,
                PreferencesHelper.getRefreshToken(),
                vkPost.id ?: 0
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { likeResponse: VkLikeInfoResponse?, _: Throwable? ->
                    val like = likeResponse?.response
                    if (like != null) {
                        view?.handleLike(vkPost)
                    } else {
                        view?.showAlertDialog(R.string.alert_msg)
                    }
                }
    }
}