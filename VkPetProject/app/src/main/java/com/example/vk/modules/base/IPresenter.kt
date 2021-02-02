package com.example.vk.modules.base

interface IPresenter<V> {
    fun attachView(view: V)
    fun detachView()
}