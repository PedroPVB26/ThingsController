package com.pedro.thingscontroller.domain.model

import com.pedro.thingscontroller.domain.usecase.EnsureNetworkUseCase

/**
 * Represents the outcome of a use case operation.
 *
 * This sealed class provides a type-safe way to express the result of any
 * domain operation, replacing Kotlin's built-in [Result] with a richer
 * structure that includes domain-specific failure cases.
 *
 * Every use case in this application returns a [UseCaseResult], allowing
 * the presentation layer to handle all possible outcomes exhaustively
 * without casting or guessing.
 *
 * @param T The type of data returned on success.
 *
 * @see Success
 * @see Failure
 */
sealed class UseCaseResult<out T> {
    /**
     * Indicates that the use case completed successfully.
     *
     * @param T The type of the resulting data.
     * @property data The result produced by the use case.
     */
    data class Success<T>(val data: T): UseCaseResult<T>()

    /**
     * Represents a failure that occurred during a use case operation.
     *
     * Subclasses cover all known failure scenarios, enabling the presentation
     * layer to react to each case with a specific and meaningful user message
     * without relying on exception casting.
     *
     * @see NoNetwork
     * @see Timeout
     * @see ThingError
     * @see Unknown
     */
    sealed class Failure: UseCaseResult<Nothing>(){
        /**
         * The operation was aborted because the device has no active
         * internet connection.
         *
         * Produced by [EnsureNetworkUseCase] and propagated by any
         * use case that requires network access before executing.
         */
        data object NoNetwork: Failure()

        /**
         * The operation failed because the remote data source did not
         * respond within the expected time.
         */
        data object Timeout: Failure()

        /**
         * The operation failed due to a known domain error related to
         * a Thing or one of its components.
         *
         * @property exception The [ThingException] that caused the failure.
         * Inspect its subtype for specific details such as [thingId] or [componentId].
         *
         * @see ThingException
         */
        data class ThingError(val exception: ThingException) : Failure()

        /**
         * The operation failed due to an unexpected error not covered
         * by any other [Failure] subclass.
         *
         * @property cause The underlying exception that caused the failure.
         */
        data class Unknown(val cause: Throwable) : Failure()
    }
}