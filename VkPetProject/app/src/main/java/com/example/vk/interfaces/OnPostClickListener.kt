package com.example.vk.interfaces

import com.example.vk.models.VkPost

interface OnPostClickListener {
    fun onPostClick(vkPost: VkPost, name: String, photoURL: String, canPost: Int, numberOfComments: Long)
}