package com.wingedsheep.gym.trainer.selfplay

import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gym.GameEnvironment
import com.wingedsheep.gym.trainer.defaults.DynamicSlotActionFeaturizer
import com.wingedsheep.gym.trainer.defaults.HeuristicEvaluator
import com.wingedsheep.gym.trainer.defaults.JsonlSelfPlaySink
import com.wingedsheep.gym.trainer.defaults.StructuralFeatures
import com.wingedsheep.gym.trainer.defaults.StructuralStateFeaturizer
import com.wingedsheep.mtg.sets.MtgSetCatalog
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlin.io.path.createTempDirectory

/**
 * GS1 verify-set self-play smoke: a full game with Global Series cards in both decks.
 * Surfaces cards that stall the step loop or never produce legal actions.
 */
class Gs1SelfPlaySmokeTest : FunSpec({

    fun registry(): CardRegistry = CardRegistry().apply {
        register(PredefinedTokens.allTokens)
        for (set in MtgSetCatalog.all) {
            register(set.cards.stampSetCode(set.code))
            register(set.basicLands)
            set.basicLandsFallback?.let { register(it.basicLands) }
        }
    }

    fun gs1Deck() = Deck.of(
        "Forest" to 10,
        "Island" to 8,
        "Mountain" to 6,
        "Jiang Yanggu" to 2,
        "Mu Yanling" to 2,
        "Journey for the Elixir" to 2,
        "Stormcloud Spirit" to 4,
        "Purple-Crystal Crab" to 4,
        "Moon-Eating Dog" to 2,
    )

    fun config() = GameConfig(
        players = listOf(
            PlayerConfig("Alice", gs1Deck()),
            PlayerConfig("Bob", gs1Deck()),
        ),
        skipMulligans = true,
        startingPlayerIndex = 0,
    )

    test("GS1-heavy decks complete a self-play game without stalling") {
        val tmpDir = createTempDirectory("gs1-selfplay-")
        val sink = JsonlSelfPlaySink(
            path = tmpDir.resolve("gs1.jsonl"),
            featureSerializer = StructuralFeatures.serializer(),
            append = false,
        )

        val loop = SelfPlayLoop(
            envFactory = { GameEnvironment.create(registry()) },
            featurizer = StructuralStateFeaturizer(),
            actionFeaturizer = DynamicSlotActionFeaturizer(headSize = 256),
            evaluator = HeuristicEvaluator(),
            sink = sink,
            simulationsPerMove = 8,
            dirichletAlpha = null,
            temperature = 0.0,
            maxSteps = 500,
        )

        val outcome = loop.playGame(config(), gameId = "gs1-smoke")
        sink.close()

        outcome.stepCount shouldBeGreaterThan 0
        (outcome.winner != null || outcome.truncated).shouldBeTrue()
    }
})

private fun List<CardDefinition>.stampSetCode(setCode: String): List<CardDefinition> =
    map { if (it.setCode == null) it.copy(setCode = setCode) else it }
