package com.example.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VkNewsInfo(
    @SerializedName("post_id")
    val postID: Long?,
    val date: Long?,
    val text: String?,
    @SerializedName("photo_100")
    val photoOneHundred: String?,
    @SerializedName("source_id")
    val sourceID: Long?,
    val comments: VkPostComments?,
    val likes: VkPostLikes?,
    val reposts: VkPostReposts?,
    val views: VkPostViews?,
    val attachments: ArrayList<VkPostAttachments>?,
    @SerializedName("post_type")
    val postType: String?,
    val type: String?,
    @SerializedName("copy_history")
    val copyHistory: ArrayList<VkCopyHistory>?,
    @SerializedName("owner_id")
    val ownerID: Long
) : Serializable