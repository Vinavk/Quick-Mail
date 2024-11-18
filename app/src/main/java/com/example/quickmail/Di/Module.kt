package com.example.quickmail.Di

import com.example.quickmail.repository.MailRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMailApi(): SendMailApiInterface {
        return Retrofit.Builder()
            .baseUrl("https://api.sendgrid.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SendMailApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideMailRepo(mailApi: SendMailApiInterface): MailRepo {
        return MailRepo(mailApi)
    }
}
