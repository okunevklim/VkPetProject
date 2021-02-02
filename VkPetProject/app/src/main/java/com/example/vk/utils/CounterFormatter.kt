package com.example.vk.utils

import com.example.vk.R
import com.example.vk.base.VkApplication.Companion.context

object CounterFormatter {

    fun formatCounterValue(value: Long): String {
        return when (value) {
            in 0..1000 -> {
                value.toString()
            }
            in 1000..1000000 -> {
                (value / 1000).toString() + context.getString(R.string.thousand)
            }
            else ->
                (value / 1000000).toString() + context.getString(R.string.million)
        }
    }
}