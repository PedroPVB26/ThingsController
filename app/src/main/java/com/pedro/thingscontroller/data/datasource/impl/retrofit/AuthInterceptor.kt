package com.pedro.thingscontroller.data.datasource.impl.retrofit

import com.pedro.thingscontroller.domain.repository.TokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor (
    private val tokenProvider: TokenProvider
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Interceptor não suporta suspend
        val tokens = runBlocking {
            tokenProvider.getTokens()
        }

        tokens?.idToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())

    }

}