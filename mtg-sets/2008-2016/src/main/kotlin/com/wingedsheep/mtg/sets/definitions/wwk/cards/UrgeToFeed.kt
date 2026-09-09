package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion
import com.wingedsheep.sdk.scripting.effects.TapUntapCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Urge to Feed
 * {B}{B}
 * Instant
 *
 * Target creature gets -3/-3 until end of turn. You may tap any number of untapped Vampire
 * creatures you control. If you do, put a +1/+1 counter on each of those Vampires.
 *
 * The debuff is the standard [Effects.ModifyStats] until-end-of-turn instant shape (Ulcerate). The
 * optional Vampire tap uses the Orphans-of-the-Wheat gather → choose-any-number → tap pipeline;
 * [Effects.IfYouDo] with [SuccessCriterion.CollectionNonEmpty] gates
 * [Effects.AddCountersToCollection] on at least one Vampire tapped.
 */
val UrgeToFeed = card("Urge to Feed") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -3/-3 until end of turn. You may tap any number of untapped " +
        "Vampire creatures you control. If you do, put a +1/+1 counter on each of those Vampires."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(-3, -3, creature),
            Effects.IfYouDo(
                action = Effects.Composite(
                    listOf(
                        GatherCardsEffect(
                            source = CardSource.ControlledPermanents(
                                player = Player.You,
                                filter = GameObjectFilter.Creature.youControl()
                                    .withSubtype("Vampire")
                                    .untapped(),
                            ),
                            storeAs = "vampires",
                        ),
                        SelectFromCollectionEffect(
                            from = "vampires",
                            selection = SelectionMode.ChooseAnyNumber,
                            storeSelected = "tapped",
                            prompt = "Tap any number of untapped Vampire creatures you control",
                            useTargetingUI = true,
                        ),
                        TapUntapCollectionEffect("tapped", tap = true),
                    ),
                ),
                ifYouDo = Effects.AddCountersToCollection("tapped", Counters.PLUS_ONE_PLUS_ONE),
                successCriterion = SuccessCriterion.CollectionNonEmpty("tapped", min = 1),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "70"
        artist = "Johann Bodin"
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b388d4d9-62b6-4174-897e-f933a4badcf9.jpg?1783942054"
    }
}
