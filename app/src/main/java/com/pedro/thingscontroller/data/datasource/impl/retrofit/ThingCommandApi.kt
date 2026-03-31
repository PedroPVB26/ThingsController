package com.pedro.thingscontroller.data.datasource.impl.retrofit

import com.pedro.thingscontroller.data.model.dto.GetAllThingsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ThingCommandApi {
    @GET("all")
    suspend fun getAllThings(): Response<GetAllThingsResponse>

    @GET("{thingName}/components")
    suspend fun getThingComponents(@Path("thingName") thingName: String): Response<ThingComponentsResponse>
}