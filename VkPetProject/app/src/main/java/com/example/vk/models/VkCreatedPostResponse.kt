package com.example.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VkCreatedPostResponse(
    @SerializedName("post_id")
    val postID: Long?
) : Serializable