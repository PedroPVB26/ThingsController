package com.pedro.thingscontroller.domain.model.command

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pedro.thingscontroller.domain.model.component.ComponentAction
import com.pedro.thingscontroller.domain.model.component.ComponentType
import java.lang.reflect.Type

/**
 * Represents a command to be sent to a Thing (IoT).
 *
 * A command targets a specific component of the thing and defines
 * which action should be executed.
 *
 * @property requestId Identifier used to track the command request.
 * Used for correlation between client and thing responses.
 * Generated in the repository implementation
 *
 * @property componentId Unique identifier of the target component inside the Thing
 * (e.g., "redLed", "greenLed", "environment1").
 *
 * @property componentType Type of the component that will receive the command
 * (e.g., LED, BUZZER).
 *
 * @property action Action to be performed on the component. The action must be
 * compatible with the specified component type.
 */
@JsonAdapter(ThingCommand.Serializer::class)
sealed class ThingCommand {
    abstract var requestId: String?
    abstract val componentId: String
    abstract val componentType: ComponentType
    abstract val action: ComponentAction

    /**
     * Custom Gson serializer for [ThingCommand] to handle polymorphic serialization.
     *
     * By default, when Retrofit/Gson encounters a sealed class or an interface in an API 
     * signature, it may attempt to serialize it based on the declared type rather than 
     * the actual runtime implementation. Since [ThingCommand] contains abstract properties 
     * without backing fields, default serialization results in an empty JSON object {}.
     *
     * This serializer forces Gson to use the runtime class (e.g., LedCommand), ensuring 
     * all implementation-specific fields and @SerializedName annotations are correctly 
     * processed and sent to the cloud.
     */
    class Serializer : JsonSerializer<ThingCommand> {
        override fun serialize(
            src: ThingCommand,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src, src.javaClass)
        }
    }
}
