package com.aramonp.workly.di

import com.aramonp.workly.domain.use_case.ValidateDates
import com.aramonp.workly.domain.use_case.ValidateEmail
import com.aramonp.workly.domain.use_case.ValidateField
import com.aramonp.workly.domain.use_case.ValidatePassword
import com.aramonp.workly.domain.use_case.ValidateRepeatedPassword
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideValidateEmail(): ValidateEmail {
        return ValidateEmail()
    }

    @Provides
    @Singleton
    fun provideValidatePassword(): ValidatePassword {
        return ValidatePassword()
    }

    @Provides
    @Singleton
    fun provideValidateRepeatedPassword(): ValidateRepeatedPassword {
        return ValidateRepeatedPassword()
    }

    @Provides
    @Singleton
    fun provideValidateField(): ValidateField {
        return ValidateField()
    }

    @Provides
    @Singleton
    fun provideValidateDates(): ValidateDates {
        return ValidateDates()
    }
}