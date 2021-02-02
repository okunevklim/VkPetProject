package com.example.vk.models

import java.io.Serializable

data class VkPostPhoto(
    val sizes: ArrayList<VkPostSizes>?
) : Serializable