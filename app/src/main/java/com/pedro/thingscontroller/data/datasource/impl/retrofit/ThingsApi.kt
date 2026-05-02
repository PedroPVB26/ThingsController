package com.pedro.thingscontroller.data.datasource.impl.retrofit

import com.pedro.thingscontroller.data.model.dto.GetAllThingsResponse
import com.pedro.thingscontroller.data.model.dto.GetThingComponentsResponse
import com.pedro.thingscontroller.data.model.dto.PostCommandResponse
import com.pedro.thingscontroller.domain.model.command.ThingCommand
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ThingsApi {
    @GET("all")
    suspend fun getAllThings(): Response<GetAllThingsResponse>

    @GET("{thingName}/components")
    suspend fun getThingComponents(@Path("thingName") thingName: String): Response<GetThingComponentsResponse>

    @POST("{thingName}/command")
    suspend fun postCommand(@Path("thingName") thingName: String, @Body thingCommand: ThingCommand): Response<PostCommandResponse>
}