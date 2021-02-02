package com.example.vk.modules.news

import android.annotation.SuppressLint
import com.example.vk.R
import com.example.vk.base.VkApplication
import com.example.vk.di.module.Api
import com.example.vk.models.*
import com.example.vk.modules.base.Presenter
import com.example.vk.utils.Constants.VERSION_API
import com.example.vk.utils.PreferencesHelper.getRefreshToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NewsPresenter internal constructor() : Presenter<INewsView?>(), INewsPresenter {

    @Inject
    lateinit var api: Api

    init {
        VkApplication.component.inject(this)
    }

    override fun onViewAttached() {}
    override fun onViewDetached() {}

    @SuppressLint("CheckResult")
    override fun getUserNews(silentMode: Boolean) {
        if (!silentMode) {
            view?.showShimmerLayout()
        } else view?.hideShimmerLayout()
        api.getService().getUserNews(
                NEWS_FILTERS,
                RETURN_BANNED,
                AMOUNT_OF_POSTS,
                VERSION_API,
                getRefreshToken(),
                MAX_PHOTOS
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { newsResponse: VkGetNewsResponse?, _ ->
                    if (newsResponse != null && newsResponse.response?.items != null) {
                        val newsPosts = newsResponse.response.items
                        val ownerPosts = newsPosts.filter {
                            it.copyHistory == null
                        }
                        val updNews = ownerPosts.filter {
                            it.attachments?.get(0)?.type == "photo" || it.attachments == null
                        }
                        val profiles = newsResponse.response.profiles
                        val groups = newsResponse.response.groups
                        if (!groups.isNullOrEmpty() && !profiles.isNullOrEmpty()) {
                            view?.handleProfiles(profiles)
                            view?.handleGroups(groups)
                        }
                        val likedPosts = updNews.filter {
                            it.likes?.userLikes == 1
                        }
                        val likedItems = likedPosts.map {
                            VkPost(
                                    it.postID,
                                    it.date,
                                    it.text,
                                    null,
                                    it.sourceID,
                                    it.comments,
                                    it.likes,
                                    it.reposts,
                                    it.views,
                                    it.attachments,
                                    it.postType
                            )
                        }
                        val likedArray = arrayListOf<VkPost>()
                        likedArray.clear()
                        likedArray.addAll(likedItems)
                        view?.handleLikedPosts(likedArray)
                        val newItems = updNews.map {
                            VkPost(
                                    it.postID,
                                    it.date,
                                    it.text,
                                    null,
                                    it.sourceID,
                                    it.comments,
                                    it.likes,
                                    it.reposts,
                                    it.views,
                                    it.attachments,
                                    it.postType
                            )
                        }
                        newItems.sortedByDescending {
                            it.date
                        }
                        if (updNews.isNotEmpty()) {
                            handleSuccess(newItems, newsResponse.response)
                            view?.stopRefreshing()
                            view?.hideShimmerLayout()
                        }
                    } else {
                        if (!silentMode) {
                            view?.showErrorLayout()
                            view?.handleError()
                        }
                    }
                }
    }

    private fun handleSuccess(
            newItems: List<VkPost>,
            newsResponse: VkNewsResponse
    ) {
        view?.setNewsInfo(
                newItems,
                newsResponse.profiles ?: arrayListOf(),
                newsResponse.groups ?: arrayListOf()
        )

        view?.hideErrorLayout()
        view?.hideShimmerLayout()
    }

    @SuppressLint("CheckResult")
    override fun setLike(vkPost: VkPost) {
        api.getService().setLike(
                vkPost.postType ?: "post",
                vkPost.ownerId ?: 0,
                VERSION_API,
                getRefreshToken(),
                vkPost.id ?: 0
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { likeResponse: VkLikeInfoResponse?, _: Throwable? ->
                    val like = likeResponse?.response
                    if (like != null) {
                        getUserNews(true)
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
                getRefreshToken(),
                vkPost.id ?: 0
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { likeResponse: VkLikeInfoResponse?, _: Throwable? ->
                    val like = likeResponse?.response
                    if (like != null) {
                        getUserNews(true)
                    } else {
                        view?.showAlertDialog(R.string.alert_msg)
                    }
                }
    }

    @SuppressLint("CheckResult")
    override fun deletePost(vkPost: VkPost) {
        api.getService().deletePost(
                vkPost.ownerId ?: 0,
                vkPost.id ?: 0,
                VERSION_API,
                getRefreshToken()
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { postResponse: VkRemovedPostResponse?, _: Throwable? ->
                    if (postResponse?.response == 1) {
                        view?.showSnackBar(
                                R.string.post_hidden,
                                R.drawable.bg_rounded_successful_snackbar,
                                R.drawable.ic_successful_tick
                        )
                        getUserNews(true)
                    } else {
                        view?.showAlertDialog(R.string.alert_msg)
                    }
                }
    }

    @SuppressLint("CheckResult")
    override fun hidePost(vkPost: VkPost) {
        api.getService().hidePost(
                VERSION_API,
                getRefreshToken(),
                vkPost.id ?: 0,
                checkType(vkPost.postType ?: ""),
                vkPost.ownerId ?: 0
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { hiddenPostResponse: VkHiddenPostResponse?, _: Throwable? ->
                    if (hiddenPostResponse?.response == 1) {
                        view?.showSnackBar(
                                R.string.post_hidden,
                                R.drawable.bg_rounded_successful_snackbar,
                                R.drawable.ic_successful_tick
                        )
                        getUserNews(true)
                    } else {
                        view?.showAlertDialog(R.string.alert_msg)
                    }
                }
    }

    override fun checkType(type: String): String {
        var newType = ""
        when (type) {
            "post" -> {
                newType = "wall"
            }
            "wall_photo" -> {
                newType = "photo"
            }
            "photo_tag" -> {
                newType = "tag"
            }
            "audio" -> {
                newType = "audio"
            }
            "video" -> {
                newType = "video"
            }
        }
        return newType
    }

    companion object {
        const val AMOUNT_OF_POSTS = 20
        const val RETURN_BANNED = 1
        const val MAX_PHOTOS = 1
        const val NEWS_FILTERS = "post"
    }
}