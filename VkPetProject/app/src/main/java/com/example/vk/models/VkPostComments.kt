package com.example.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VkPostComments(
    val count: Long?,
    @SerializedName("can_post")
    val canPost: Int?
) : Serializable