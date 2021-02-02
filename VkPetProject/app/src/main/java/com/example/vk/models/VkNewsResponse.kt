package com.example.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VkNewsResponse(
    val items: ArrayList<VkNewsInfo>?,
    val profiles: ArrayList<VkUserInfo>?,
    val groups: ArrayList<VkGroup>?,
    @SerializedName("next_from")
    val nextFrom: String?
) : Serializable