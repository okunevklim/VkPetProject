package com.example.vk.interfaces

import com.example.vk.models.VkPost

interface OnDeletePostListener {
    fun onDeletePost(vkPost: VkPost)
}