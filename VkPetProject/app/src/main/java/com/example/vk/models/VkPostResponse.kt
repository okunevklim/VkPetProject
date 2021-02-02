package com.example.vk.models

import java.io.Serializable

data class VkPostResponse(
    val count: Int?,
    val items: ArrayList<VkPost>?
) : Serializable