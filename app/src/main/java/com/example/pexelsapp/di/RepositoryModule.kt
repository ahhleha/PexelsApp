package com.example.pexelsapp.di

import com.example.pexelsapp.data.RepositoryImpl
import com.example.pexelsapp.domain.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun getRepository(impl: RepositoryImpl): Repository
}