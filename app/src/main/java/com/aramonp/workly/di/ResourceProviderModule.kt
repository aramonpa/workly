package com.aramonp.workly.di

import android.content.Context
import com.aramonp.workly.data.repository.ResourceProviderImpl
import com.aramonp.workly.domain.repository.ResourceProvider
import com.aramonp.workly.domain.use_case.ValidateEmail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResourceProviderModule {
    @Provides
    @Singleton
    fun provideResourceProvider(
        @ApplicationContext context: Context
    ): ResourceProviderImpl {
        return ResourceProviderImpl(context)
    }
}