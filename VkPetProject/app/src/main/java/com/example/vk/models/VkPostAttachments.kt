package com.example.vk.models

import java.io.Serializable

data class VkPostAttachments(
    val type: String?,
    val photo: VkPostPhoto?
) : Serializable