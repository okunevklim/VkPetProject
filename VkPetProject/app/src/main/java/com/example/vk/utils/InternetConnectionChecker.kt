package com.example.vk.utils

import androidx.lifecycle.LifecycleOwner
import com.example.vk.base.VkApplication
import com.example.vk.network.NetworkConnection

object InternetConnectionChecker {
    fun checkInternetConnection(
            viewLifecycleOwner: LifecycleOwner,
            listener: (isConnected: Boolean) -> Unit
    ) {
        val networkConnection = NetworkConnection(VkApplication.context)
        networkConnection.observe(viewLifecycleOwner, { isConnected ->
            listener.invoke(isConnected)
        }
        )
    }
}