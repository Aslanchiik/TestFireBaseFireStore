package com.example.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Singleton
  @Provides
    fun fetchFireStore () : FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    @Singleton
    @Provides
     // чтобы использовать в другом потоке
      fun pull () : ExecutorService {
          return Executors.newFixedThreadPool(4)
      }
}