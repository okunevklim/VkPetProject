package com.example.vk.models

import java.io.Serializable

data class CommentResponse(
    val count: Int?,
    val items: ArrayList<VkComment>?
) : Serializable