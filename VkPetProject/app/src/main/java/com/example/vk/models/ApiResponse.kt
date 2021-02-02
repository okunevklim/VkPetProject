package com.example.vk.models

import java.io.Serializable

data class ApiResponse(
    val response: VkPostResponse?
) : Serializable