package com.genegebra.healthtracker.di

import com.genegebra.healthtracker.data.repository.AuthRepositoryImpl
import com.genegebra.healthtracker.data.repository.HealthEntryRepositoryImpl
import com.genegebra.healthtracker.domain.repository.AuthRepository
import com.genegebra.healthtracker.domain.repository.HealthEntryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindHealthEntryRepository(impl: HealthEntryRepositoryImpl): HealthEntryRepository
}
