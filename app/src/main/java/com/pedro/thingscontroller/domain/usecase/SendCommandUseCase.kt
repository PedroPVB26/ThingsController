package com.pedro.thingscontroller.domain.usecase

import com.pedro.thingscontroller.domain.model.ThingException
import com.pedro.thingscontroller.domain.model.UseCaseResult
import com.pedro.thingscontroller.domain.model.command.ThingCommand
import com.pedro.thingscontroller.domain.repository.ThingRepository
import javax.inject.Inject

/**
 * Use case responsible for sending a command to a specific Thing (IoT device).
 *
 * Before dispatching the command, connectivity is verified via [EnsureNetworkUseCase].
 * If the device is offline, the operation fails immediately with a [ThingException.ThingNotConnectedException]
 * without reaching the repository.
 *
 * This reflects the business rule that commands cannot be sent without
 * an active internet connection, keeping that constraint at the domain level
 * rather than delegating it to the presentation layer.
 *
 * @param thingRepository Repository used to dispatch the command to the target Thing.
 * @param ensureNetworkUseCase Use case used to verify connectivity before proceeding.
 *
 * @see ThingCommand
 */
class SendCommandUseCase @Inject constructor(
    private val thingRepository: ThingRepository,
    private val ensureNetworkUseCase: EnsureNetworkUseCase
) {
    suspend operator fun invoke(thingId: String, command: ThingCommand): UseCaseResult<Unit> {
        ensureNetworkUseCase().let {
            if(it is UseCaseResult.Failure.NoNetwork) return it
        }

        return try {
            thingRepository.sendCommand(thingId, command)
            UseCaseResult.Success(Unit)
        }catch (e: ThingException){
            UseCaseResult.Failure.ThingError(e)
        }catch (e: Exception) {
            UseCaseResult.Failure.Unknown(e)
        }
    }
}