package com.example.vk.models

import java.io.Serializable

data class VkPostSizes(
    val width: Int?,
    val height: Int?,
    val url: String?
) : Serializable