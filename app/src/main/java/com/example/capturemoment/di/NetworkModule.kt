package com.example.capturemoment.di

import com.example.capturemoment.data.source.remote.network.ApiConfig
import com.example.capturemoment.data.source.remote.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideApiService() : ApiService = ApiConfig.getApiService()
}