package com.example.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VkPost(
    var id: Long?,
    val date: Long?,
    val text: String?,
    @SerializedName("copy_history")
    val copyHistory: ArrayList<Any>?,
    @SerializedName("owner_id")
    val ownerId: Long?,
    val comments: VkPostComments?,
    val likes: VkPostLikes?,
    val reposts: VkPostReposts?,
    val views: VkPostViews?,
    val attachments: ArrayList<VkPostAttachments>?,
    val postType: String?
) : Serializable