package com.example.vk.models

import java.io.Serializable

data class VkCityResponse(
    val response: ArrayList<VkCityName>?
): Serializable