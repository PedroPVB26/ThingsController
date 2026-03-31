package com.pedro.thingscontroller.data.datasource.impl.retrofit

import com.pedro.thingscontroller.data.model.dto.PostCommandResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ThingsApi {
    @POST("{thingName}/command")
    suspend fun postCommand(@Path("thingName") thingName: String, @Body esp32Command: Any): Response<PostCommandResponse> // AJUSTAR O TIPO DE RETORNO
}