package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Angel of Serenity — Return to Ravnica #1
 * {4}{W}{W}{W} · Creature — Angel · Mythic
 * 5/6
 *
 * Flying
 * When this creature enters, you may exile up to three other target creatures from the battlefield
 * and/or creature cards from graveyards.
 * When this creature leaves the battlefield, return the exiled cards to their owners' hands.
 *
 * The exile shape matches [com.wingedsheep.mtg.sets.definitions.mkm.cards.AureliasVindicator]:
 * one cross-zone union target ([TargetFilter.OtherCreature] or [TargetFilter.CreatureInGraveyard]),
 * `optional = true` for "you may" / "up to three", and [GatherCardsEffect] +
 * [MoveCollectionEffect] with `linkToSource = true` instead of [Effects.ExileUntilLeaves] so
 * multiple targets exile in one resolution and the linked pile returns to hand on LTB.
 */
val AngelOfSerenity = card("Angel of Serenity") {
    manaCost = "{4}{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 5
    toughness = 6
    oracleText = "Flying\n" +
        "When this creature enters, you may exile up to three other target creatures from the " +
        "battlefield and/or creature cards from graveyards.\n" +
        "When this creature leaves the battlefield, return the exiled cards to their owners' hands."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "up to three other target creatures from the battlefield and/or creature cards from graveyards",
            TargetObject(
                optional = true,
                filter = TargetFilter.OtherCreature.or(TargetFilter.CreatureInGraveyard),
                dynamicMaxCount = DynamicAmount.Fixed(3),
            ),
        )
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(source = CardSource.ChosenTargets, storeAs = "serenity_exiled"),
                MoveCollectionEffect(
                    from = "serenity_exiled",
                    destination = CardDestination.ToZone(Zone.EXILE),
                    linkToSource = true,
                ),
            )
        )
        description = "When this creature enters, you may exile up to three other target creatures " +
            "from the battlefield and/or creature cards from graveyards."
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileToHand()
        description = "When this creature leaves the battlefield, return the exiled cards to " +
            "their owners' hands."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "1"
        artist = "Aleksi Briclot"
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f10d82f7-7759-457e-a9bb-f9a5bd968f82.jpg?1783940378"
    }
}
