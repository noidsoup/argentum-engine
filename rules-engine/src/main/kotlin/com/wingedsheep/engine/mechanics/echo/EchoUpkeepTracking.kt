package com.wingedsheep.engine.mechanics.echo

import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.PresentAtControllersLastUpkeepComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.sdk.model.EntityId

/**
 * CR 702.30a upkeep tracking — records which permanents were under a player's control when
 * their upkeep step ends, so echo's intervening-`if` can tell whether a permanent *came under*
 * that controller since the beginning of their last upkeep.
 *
 * Stamped in [com.wingedsheep.engine.core.TurnManager] when leaving [com.wingedsheep.sdk.core.Step.UPKEEP];
 * cleared when a permanent enters the battlefield or changes controller.
 */
object EchoUpkeepTracking {

    /** Stamp every permanent [controllerId] controls at the end of their upkeep step. */
    fun stampPresentAtUpkeep(state: GameState, controllerId: EntityId): GameState {
        val projected = state.projectedState
        var newState = state
        for (entityId in state.allBattlefieldEntities()) {
            if (projected.getController(entityId) != controllerId) continue
            newState = newState.updateEntity(entityId) { container ->
                container.with(PresentAtControllersLastUpkeepComponent(controllerId))
            }
        }
        return newState
    }

    /** Clear echo upkeep memory — call on battlefield entry and control change. */
    fun clearPresence(state: GameState, entityId: EntityId): GameState =
        state.updateEntity(entityId) { container ->
            if (container.has<PresentAtControllersLastUpkeepComponent>()) {
                container.without<PresentAtControllersLastUpkeepComponent>()
            } else {
                container
            }
        }
}
