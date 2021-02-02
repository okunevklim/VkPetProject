package com.example.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VkGroup(
    val id: Long?,
    val name: String?,
    @SerializedName("photo_200")
    val photoTwoHundred: String?,
    val type: String?
) : Serializable