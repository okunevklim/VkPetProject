package com.example.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VkPostReposts(
    val count: Long?,
    @SerializedName("user_reposted")
    val userReposted: Int?
) : Serializable