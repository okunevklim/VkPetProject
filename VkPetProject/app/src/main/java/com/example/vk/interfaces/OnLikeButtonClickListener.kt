package com.example.vk.interfaces

import com.example.vk.models.VkPost

interface OnLikeButtonClickListener {
    fun onPostLikeClick(vkPost: VkPost)
}