package com.example.vk.di.component

import com.example.vk.di.module.Api
import com.example.vk.modules.details.DetailsPresenter
import com.example.vk.modules.input.InputPresenter
import com.example.vk.modules.liked.LikedPresenter
import com.example.vk.modules.news.NewsPresenter
import com.example.vk.modules.profile.ProfilePresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [Api::class])
interface ApiComponent {
    fun inject(newsPresenter: NewsPresenter)
    fun inject(inputPresenter: InputPresenter)
    fun inject(detailsPresenter: DetailsPresenter)
    fun inject(profilePresenter: ProfilePresenter)
    fun inject(likedPresenter: LikedPresenter)
}