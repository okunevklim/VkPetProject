package com.example.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VkPostLikes(
    var count: Long?,
    @SerializedName("user_likes")
    var userLikes: Int?,
    @SerializedName("can_like")
    val canLike: Int?
) : Serializable