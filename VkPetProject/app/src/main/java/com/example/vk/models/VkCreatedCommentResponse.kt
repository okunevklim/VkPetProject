package com.example.vk.models

import com.google.gson.annotations.SerializedName

data class VkCreatedCommentResponse(
    @SerializedName("comment_id")
    val commentID: Int?,
    @SerializedName("parents_stack")
    val parentsStack: ArrayList<Long>?
)