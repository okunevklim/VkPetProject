package com.example.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VkComment(
    val id: Long?,
    val date: Long?,
    val text: String?,
    @SerializedName("owner_id")
    val ownerID: Long?,
    val likes: VkCommentLikes?,
    @SerializedName("from_id")
    val fromID: Long?,
    val attachments: ArrayList<VkPostAttachments>?
) : Serializable

data class VkCommentLikes(
    val count: Long?,
    @SerializedName("user_likes")
    val userLikes: Int?,
    @SerializedName("can_like")
    val canLike: Int?,
) : Serializable