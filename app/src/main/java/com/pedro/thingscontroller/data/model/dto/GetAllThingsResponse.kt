package com.pedro.thingscontroller.data.model.dto

import com.pedro.thingscontroller.domain.model.thing.Thing

/**
 * Data Transfer Object representing the response of a "get all Things" operation.
 *
 * This response encapsulates both the retrieved list of domain entities and
 * metadata provided by DynamoDB regarding the query execution.
 *
 * @property count Number of items returned in the response after applying any filters.
 * @property scannedCount Total number of items evaluated by DynamoDB during the operation (before filtering).
 * @property things List of retrieved [Thing] domain models.
 */
data class GetAllThingsResponse(
    val count: Int,
    val scannedCount: Int,
    val things: List<Thing>
)