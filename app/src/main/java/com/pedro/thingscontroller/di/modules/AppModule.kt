package com.pedro.thingscontroller.di.modules

import android.content.Context
import com.google.gson.Gson
import com.pedro.thingscontroller.data.auth.AmplifyTokenProvider
import com.pedro.thingscontroller.data.auth.AuthRepositoryImpl
import com.pedro.thingscontroller.data.datasource.MqttDataSource
import com.pedro.thingscontroller.data.datasource.impl.AwsMqttDataSource
import com.pedro.thingscontroller.data.datasource.impl.NetworkMonitorImpl
import com.pedro.thingscontroller.data.datasource.impl.ThingRepositoryImpl
import com.pedro.thingscontroller.data.datasource.impl.retrofit.AuthInterceptor
import com.pedro.thingscontroller.data.datasource.impl.retrofit.ThingsApi
import com.pedro.thingscontroller.domain.repository.AuthRepository
import com.pedro.thingscontroller.domain.repository.NetworkMonitor
import com.pedro.thingscontroller.domain.repository.ThingRepository
import com.pedro.thingscontroller.domain.repository.TokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTokenProvider(
        @ApplicationContext context: Context
    ): TokenProvider {
        return AmplifyTokenProvider(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository{
        return AuthRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenProvider: TokenProvider
    ): AuthInterceptor {
        return AuthInterceptor(tokenProvider)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor {
        return NetworkMonitorImpl(context)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://jakzr6neag.execute-api.us-east-2.amazonaws.com/dev/thing/") //
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideThingsApi(
        retrofit: Retrofit
    ): ThingsApi {
        return retrofit.create(ThingsApi::class.java)
    }

    @Provides
    @Singleton
    fun providesMqttDataSource(
        @ApplicationContext context: Context
    ): MqttDataSource{
        return AwsMqttDataSource(context)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @ApplicationScope
    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun providesThingRepository(
        thingsApi: ThingsApi,
        mqttDataSource: MqttDataSource,
        gson: Gson,
        @ApplicationScope coroutineScope: CoroutineScope
    ): ThingRepository{
        return ThingRepositoryImpl(
            thingsApi = thingsApi,
            mqttDataSource = mqttDataSource,
            gson = gson,
            appScope = coroutineScope
        )
    }

}