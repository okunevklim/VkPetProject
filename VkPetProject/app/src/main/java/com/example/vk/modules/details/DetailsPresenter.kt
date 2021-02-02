package com.example.vk.modules.details

import android.annotation.SuppressLint
import com.example.vk.R
import com.example.vk.base.VkApplication
import com.example.vk.di.module.Api
import com.example.vk.models.*
import com.example.vk.modules.base.Presenter
import com.example.vk.utils.Constants.VERSION_API
import com.example.vk.utils.PreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DetailsPresenter internal constructor() : Presenter<IDetailsView?>(), IDetailsPresenter {

    @Inject
    lateinit var api: Api

    init {
        VkApplication.component.inject(this)
    }

    override fun onViewAttached() {}
    override fun onViewDetached() {}

    @SuppressLint("CheckResult")
    override fun postComment(vkPost: VkPost) {
        view?.showDetailsProgressBar()
        api.getService().createCommentToPost(
                vkPost.ownerId ?: 0,
                vkPost.id ?: 0,
                view?.getInputTextToString() ?: "",
                VERSION_API,
                PreferencesHelper.getRefreshToken()
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { commentResponse: VkCreateCommentResponse?, _: Throwable? ->
                    val comment = commentResponse?.response
                    view?.hideDetailsProgressBar()
                    if (comment == null) {
                        view?.showAlertDialog(R.string.alert_post_comment_msg)
                    } else {
                        view?.handleIsRefreshing(1)
                        view?.clearInputText()
                        getVkComments(vkPost)
                        view?.showSuccessfulCommentToast()
                    }
                }
    }

    @SuppressLint("CheckResult")
    override fun getVkComments(vkPost: VkPost) {
        view?.isShimmerLoading(1)
        view?.showDetailsProgressBar()
        api.getService().getComments(
                vkPost.ownerId ?: POST_ID_ZERO,
                vkPost.id ?: POST_ID_ZERO,
                NEED_LIKES,
                AMOUNT_OF_COMMENTS,
                SORT_TYPE,
                PREVIEW_LENGTH,
                VERSION_API,
                PreferencesHelper.getRefreshToken()
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response: VkCommentResponse?, _: Throwable? ->
                    val vkComments = response?.response?.items
                    view?.isShimmerLoading(0)
                    if (vkComments != null) {
                        var i = 0
                        val textComments = arrayListOf<VkComment>()
                        textComments.clear()
                        while (i < vkComments.size) {
                            if (vkComments[i].attachments == null) {
                                textComments.add(vkComments[i])
                                view?.updatePosition(i)
                            }
                            i++
                        }
                        if (vkComments.isNullOrEmpty()) {
                            view?.hideDetailsProgressBar()
                        } else {
                            view?.setNumberOfComments(textComments.size.toLong())
                            val commentsIDS = textComments.map { it.fromID ?: 0 }
                            val stringCommentsIDS = commentsIDS.joinToString(separator = ",")
                            getUserCommentsInfo(stringCommentsIDS, textComments)
                        }
                    } else {
                        view?.hideDetailsProgressBar()
                        view?.showAlertDialog(R.string.alert_comments_msg)
                    }

                }
    }

    @SuppressLint("CheckResult")
    override fun getUserCommentsInfo(commentsIDS: String, vkComments: ArrayList<VkComment>) {
        view?.showDetailsProgressBar()
        api.getService().getCommentUserInfo(
                commentsIDS,
                COMMENTS_USER_INFO,
                VERSION_API,
                PreferencesHelper.getRefreshToken()
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response: VkUserInfoResponse?, _: Throwable? ->
                    val commentInfo = response?.response
                    if (commentInfo.isNullOrEmpty()) {
                        view?.hideDetailsProgressBar()
                        view?.showAlertDialog(R.string.alert_msg)
                    } else {
                        view?.setVkComments(vkComments, commentInfo)
                        view?.hideDetailsProgressBar()
                    }
                }
    }

    @SuppressLint("CheckResult")
    override fun setLike(type: String, vkPost: VkPost, itemID: Long) {
        api.getService().setLike(
                type,
                vkPost.ownerId ?: 0,
                VERSION_API,
                PreferencesHelper.getRefreshToken(),
                itemID
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { likeResponse: VkLikeInfoResponse?, _: Throwable? ->
                    val like = likeResponse?.response
                    view?.handleLike(vkPost)
                    view?.handleIsRefreshing(1)
                    if (like != null) {
                        if (type == "comment") {
                            getVkComments(vkPost)
                        } else {
                            view?.updateAmountOfLikes(like.likes, 1)
                        }
                    } else {
                        view?.showAlertDialog(R.string.alert_msg)
                    }
                }
    }

    @SuppressLint("CheckResult")
    override fun deleteLike(type: String, vkPost: VkPost, itemID: Long) {
        api.getService().deleteLike(
                type,
                vkPost.ownerId ?: 0,
                VERSION_API,
                PreferencesHelper.getRefreshToken(),
                itemID
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { likeResponse: VkLikeInfoResponse?, _: Throwable? ->
                    val like = likeResponse?.response
                    view?.handleLike(vkPost)
                    view?.handleIsRefreshing(1)
                    if (like != null) {
                        if (type == "comment") {
                            getVkComments(vkPost)
                        } else {
                            view?.updateAmountOfLikes(like.likes, 0)
                        }
                    } else {
                        view?.showAlertDialog(R.string.alert_msg)
                    }
                }
    }

    companion object {
        const val POST_ID_ZERO: Long = 0
        const val NEED_LIKES = 1
        const val AMOUNT_OF_COMMENTS: Long = 100
        const val SORT_TYPE = "desc"
        const val PREVIEW_LENGTH = 0
        const val COMMENTS_USER_INFO = "first_name, last_name, photo_max_orig"
    }
}