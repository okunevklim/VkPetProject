package com.example.vk.interfaces

import com.example.vk.models.VkComment
import com.example.vk.models.VkPost

interface OnCommentLikeClickListener {
    fun onCommentLikeClick(vkComment: VkComment, vkPost: VkPost)
}