package com.example.vk.modules.profile

import android.annotation.SuppressLint
import com.example.vk.R
import com.example.vk.base.VkApplication
import com.example.vk.di.module.Api
import com.example.vk.models.*
import com.example.vk.modules.base.Presenter
import com.example.vk.utils.Constants.VERSION_API
import com.example.vk.utils.PreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProfilePresenter internal constructor() : Presenter<IProfileView?>(), IProfilePresenter {

    private lateinit var disposable: Disposable

    @Inject
    lateinit var api: Api

    init {
        VkApplication.component.inject(this)
    }

    override fun onViewAttached() {}
    override fun onViewDetached() {}

    override fun getUserPosts(silentMode: Boolean) {
        if (!silentMode) {
            view?.showShimmerLayout()
        }
        disposable = api.getService().getUserPosts(
                AMOUNT_OF_POSTS,
                PreferencesHelper.getRefreshToken(),
                VERSION_API
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response: ApiResponse?, _ ->
                    if (response != null && response.response?.items != null) {
                        val profilePosts = response.response.items
                        val updProfilePosts = profilePosts.filter {
                            it.copyHistory == null && (it.attachments?.get(0)?.type == "photo" || it.attachments == null)
                        }
                        val likedPosts = updProfilePosts.filter {
                            it.likes?.userLikes == 1
                        }
                        val posts = updProfilePosts.sortedByDescending {
                            it.date
                        }
                        val likedItems = arrayListOf<VkPost>()
                        likedItems.clear()
                        likedItems.addAll(likedPosts)
                        view?.handleLikedPosts(likedItems)
                        if (posts.isEmpty()) {
                            if (!silentMode) {
                                view?.showErrorLayout()
                                view?.hideShimmerLayout()
                            }
                        } else {
                            if (silentMode) {
                                view?.handlePosts(updProfilePosts, false)
                            } else if (!silentMode) {
                                view?.handlePosts(updProfilePosts, true)
                            }
                        }
                    } else {
                        if (!silentMode) {
                            view?.handleError()
                        }
                    }
                }
    }

    @SuppressLint("CheckResult")
    override fun loadUserInfo() {
        view?.showShimmerLayout()
        api.getService().getUserInfo(
                PROFILE_FIELDS,
                VERSION_API,
                PreferencesHelper.getRefreshToken()
        ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { infoResponse: VkUserInfoResponse?, _: Throwable? ->
                    if (infoResponse?.response?.isNotEmpty() == true) {
                        val userInfo = infoResponse.response[0]
                        view?.handleUserInfo(userInfo)
                        view?.handleLikedProfilePostsUserInfo(userInfo)
                        val cities = infoResponse.response[0].career
                        val citiesIDs = cities?.map { it.cityID ?: 0 }
                        if (citiesIDs != null) {
                            loadCitiesByID(citiesIDs)
                        }
                    } else {
                        view?.showAlertDialog(R.string.alert_msg)
                    }
                }
    }

    @SuppressLint("CheckResult")
    override fun loadCitiesByID(cityID: List<Int>) {
        api.getService().getCitiesByID(
                cityID,
                VERSION_API,
                PreferencesHelper.getRefreshToken()
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { cityResponse: VkCityResponse?, _: Throwable? ->
                    if (cityResponse?.response?.isNotEmpty() == true) {
                        val cities = cityResponse.response
                        view?.setCareerCities(cities)
                    } else {
                        view?.showAlertDialog(R.string.alert_msg)
                    }
                }
    }


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
                        getUserPosts(true)
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
                        getUserPosts(true)
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
                PreferencesHelper.getRefreshToken()
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { postResponse: VkRemovedPostResponse?, _: Throwable? ->
                    if (postResponse?.response == 1) {
                        getUserPosts(true)
                        view?.showSuccessfulSnackBar()
                    } else {
                        view?.showAlertDialog(R.string.alert_msg)
                    }
                }
    }

    companion object {
        const val AMOUNT_OF_POSTS: Long = 30
        const val PROFILE_FIELDS =
                "id, deactivated, domain, first_name, last_name, photo_max_orig, about, bdate, city, country, career, universities, schools, followers_count, last_seen, sex, photo_100"
    }
}