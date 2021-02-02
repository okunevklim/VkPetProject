package com.example.vk.network

import com.example.vk.models.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface VKService {

    @GET("wall.get")
    fun getUserPosts(
        @Query("count") count: Long,
        @Query("access_token") accessToken: String,
        @Query("v") vkVersionApi: Double
    ): Single<ApiResponse>

    @GET("users.get")
    fun getUserInfo(
        @Query("fields") fields: String,
        @Query("v") vkVersionApi: Double,
        @Query("access_token") accessToken: String
    ): Single<VkUserInfoResponse>

    @GET("database.getCitiesById")
    fun getCitiesByID(
        @Query("city_ids") city_ids: List<Int>,
        @Query("v") vkVersionApi: Double,
        @Query("access_token") accessToken: String
    ): Single<VkCityResponse>

    @GET("wall.getComments")
    fun getComments(
        @Query("owner_id") ownerID: Long,
        @Query("post_id") postID: Long,
        @Query("need_likes") needLikes: Int,
        @Query("count") count: Long,
        @Query("sort") sort: String,
        @Query("preview_length") previewLength: Int,
        @Query("v") vkVersionApi: Double,
        @Query("access_token") accessToken: String
    ): Single<VkCommentResponse>

    @GET("users.get")
    fun getCommentUserInfo(
        @Query("user_ids") userID: String,
        @Query("fields") fields: String,
        @Query("v") vkVersionApi: Double,
        @Query("access_token") accessToken: String
    ): Single<VkUserInfoResponse>

    @GET("wall.createComment")
    fun createCommentToPost(
        @Query("owner_id") ownerID: Long,
        @Query("post_id") postID: Long,
        @Query("message") message: String,
        @Query("v") vkVersionApi: Double,
        @Query("access_token") accessToken: String
    ): Single<VkCreateCommentResponse>

    @GET("wall.post")
    fun createWallPost(
        @Query("message") message: String,
        @Query("v") vkVersionApi: Double,
        @Query("access_token") accessToken: String
    ): Single<VkCreatePostResponse>

    @GET("likes.add")
    fun setLike(
        @Query("type") type: String,
        @Query("owner_id") ownerID: Long,
        @Query("v") vkVersionApi: Double,
        @Query("access_token") accessToken: String,
        @Query("item_id") itemID: Long
    ): Single<VkLikeInfoResponse>

    @GET("likes.delete")
    fun deleteLike(
        @Query("type") type: String,
        @Query("owner_id") ownerID: Long,
        @Query("v") vkVersionApi: Double,
        @Query("access_token") accessToken: String,
        @Query("item_id") itemID: Long
    ): Single<VkLikeInfoResponse>

    @GET("newsfeed.get")
    fun getUserNews(
        @Query("filters") filters: String,
        @Query("return_banned") returnBanned: Int,
        @Query("count") count: Int,
        @Query("v") vkVersionApi: Double,
        @Query("access_token") accessToken: String,
        @Query("max_photos") maxPhotos: Int
    ): Single<VkGetNewsResponse>

    @GET("wall.delete")
    fun deletePost(
        @Query("owner_id") ownerID: Long,
        @Query("post_id") postID: Long,
        @Query("v") vkVersionApi: Double,
        @Query("access_token") accessToken: String
    ): Single<VkRemovedPostResponse>

    @GET("newsfeed.ignoreItem")
    fun hidePost(
        @Query("v") vkVersionApi: Double,
        @Query("access_token") accessToken: String,
        @Query("item_id") itemID: Long,
        @Query("type") postType: String,
        @Query("owner_id") ownerID: Long
    ): Single<VkHiddenPostResponse>
}