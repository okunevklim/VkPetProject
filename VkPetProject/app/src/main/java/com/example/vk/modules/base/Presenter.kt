package com.example.vk.modules.base

abstract class Presenter<V : IView<*>?> : IPresenter<V> {
    private val lock = Any()
    var view: V? = null
    override fun attachView(view: V) {
        synchronized(lock) { this.view = view }
        onViewAttached()
    }

    override fun detachView() {
        synchronized(lock) { view = null }
        onViewDetached()
    }

    protected abstract fun onViewAttached()
    protected abstract fun onViewDetached()
}