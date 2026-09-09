package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

private val artifactYouDontControl = GameObjectFilter.Artifact.copy(
    controllerPredicate = ControllerPredicate.Not(ControllerPredicate.ControlledByYou),
)

/**
 * Vandalblast
 * {R}
 * Sorcery
 *
 * Destroy target artifact you don't control.
 * Overload {4}{R} (You may cast this spell for its overload cost. If you do, change "target" in its
 * text to "each.")
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Overload swaps the single-target destroy for a sweep of every artifact the caster doesn't control
 * (CR 702.95). The overload cast is untargeted even though the printed mode targets.
 */
val Vandalblast = card("Vandalblast") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy target artifact you don't control.\n" +
        "Overload {4}{R} (You may cast this spell for its overload cost. If you do, change \"target\" in its text to \"each.\")"

    keywordAbility(KeywordAbility.overload("{4}{R}"))

    spell {
        val t = target(
            "target artifact you don't control",
            TargetPermanent(filter = TargetFilter(artifactYouDontControl)),
        )
        effect = Effects.Destroy(t)
        overloadEffect = Effects.DestroyAll(artifactYouDontControl)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "111"
        artist = "Seb McKinnon"
        flavorText = "Beauty is in the eye of the exploder."
        imageUri = "https://cards.scryfall.io/normal/front/5/9/5925c559-3e3c-481b-ba95-20a405cbffce.jpg?1783940352"
    }
}
