package com.ncf.seguros.indico.di

import com.ncf.seguros.indico.repository.AuthRepository
import com.ncf.seguros.indico.repository.CondominiumRepository
import com.ncf.seguros.indico.repository.InsuranceRepository
import com.ncf.seguros.indico.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }
    
    @Provides
    @Singleton
    fun provideCondominiumRepository(): CondominiumRepository {
        return CondominiumRepository()
    }
    
    @Provides
    @Singleton
    fun provideInsuranceRepository(): InsuranceRepository {
        return InsuranceRepository()
    }
} 