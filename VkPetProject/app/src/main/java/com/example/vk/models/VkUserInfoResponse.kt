package com.example.vk.models

import java.io.Serializable

data class VkUserInfoResponse(
    val response: ArrayList<VkUserInfo>?
) : Serializable