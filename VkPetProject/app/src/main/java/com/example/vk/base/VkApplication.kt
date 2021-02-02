package com.example.vk.base

import android.app.Application
import android.content.Context
import com.example.vk.di.component.ApiComponent
import com.example.vk.di.component.DaggerApiComponent

class VkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        component = buildComponent()
    }

    private fun buildComponent(): ApiComponent {
        return DaggerApiComponent.builder().build()
    }

    companion object {
        lateinit var context: Context
        lateinit var component: ApiComponent
    }
}