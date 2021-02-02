package com.example.vk.models

import com.google.gson.annotations.SerializedName

data class VkCopyHistory(
    val id: Long?,
    @SerializedName("owner_id")
    val ownerID: Long?,
    @SerializedName("from_id")
    val fromID: Long?
)