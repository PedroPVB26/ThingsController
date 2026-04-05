package com.pedro.thingscontroller.data.datasource.impl

import android.util.Log
import com.pedro.thingscontroller.data.datasource.impl.retrofit.ThingsApi
import com.pedro.thingscontroller.domain.model.command.ThingCommand
import com.pedro.thingscontroller.domain.model.thing.Thing
import com.pedro.thingscontroller.domain.repository.ThingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ThingRepositoryImpl @Inject constructor(
    private val thingsApi: ThingsApi
): ThingRepository {
    private val TAG = "ThingRepositoryImpl"
    private val repositoryScope = CoroutineScope(Dispatchers.IO)


    private val _allThings = MutableStateFlow<Map<String, Thing>>(emptyMap())
    override val allThings = _allThings.asStateFlow()

    override fun observeThing(thingId: String): Flow<Thing?> {
        TODO("Not yet implemented")
    }

    override suspend fun initialize() {
        try{
            val response = thingsApi.getAllThings()
            if(response.isSuccessful && response.body() != null){
                val things = response.body()!!.things
                _allThings.value = things.associateBy { it.thingId }
                Log.i(TAG, allThings.value.toString())
            }
        }catch (e: Exception){
            Log.e(TAG, "initialize: $e")
        }
    }

    override suspend fun sendCommand(
        thingId: String,
        thingCommand: ThingCommand
    ) {
        TODO("Not yet implemented")
    }


}