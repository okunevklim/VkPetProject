package com.example.vk.utils

import android.content.Context.MODE_PRIVATE
import com.example.vk.R
import com.example.vk.base.VkApplication.Companion.context
import com.example.vk.utils.PreferencesHelper.Keys.KEY_REFRESH_TOKEN


object PreferencesHelper {
    private val sharedPreferences =
            context.getSharedPreferences(context.getString(R.string.package_name), MODE_PRIVATE)

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    fun putRefreshToken(refreshToken: String) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    fun getRefreshToken(): String {
        return sharedPreferences.getString(
                KEY_REFRESH_TOKEN,
                ""
        ) ?: ""
    }

    object Keys {
        const val KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN"
    }
}