package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

private val timotharBatReturnTrigger = TriggeredAbility.create(
    trigger = Triggers.DealsCombatDamageToPlayer.event,
    binding = Triggers.DealsCombatDamageToPlayer.binding,
    effect = Effects.Composite(
        SacrificeSelfEffect,
        Effects.ReturnLinkedExileTappedUnderOwnersControl(),
    ),
    descriptionOverride = "When this token deals combat damage to a player, sacrifice it and " +
        "return the exiled card to the battlefield tapped.",
)

/**
 * Timothar, Baron of Bats — Innistrad: Crimson Vow Commander #4
 * {4}{B}{B} · Legendary Creature — Vampire Noble · 4/4
 *
 * Ward—Discard a card.
 * Whenever another nontoken Vampire you control dies, you may pay {1} and exile it. If you do,
 * create a 1/1 black Bat creature token with flying. It gains "When this token deals combat
 * damage to a player, sacrifice it and return the exiled card to the battlefield tapped."
 *
 * The dies trigger is [TriggerBinding.OTHER] over nontoken Vampires you control. The optional
 * {1} rider is [MayPayManaEffect] chaining token creation (with the combat-damage return trigger
 * baked in) and a graveyard-gated exile linked to [CREATED_TOKENS] via [Effects.Move.linkToTarget].
 */
val TimotharBaronOfBats = card("Timothar, Baron of Bats") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Vampire Noble"
    power = 4
    toughness = 4
    oracleText = "Ward—Discard a card.\n" +
        "Whenever another nontoken Vampire you control dies, you may pay {1} and exile it. If you do, " +
        "create a 1/1 black Bat creature token with flying. It gains \"When this token deals combat " +
        "damage to a player, sacrifice it and return the exiled card to the battlefield tapped.\""

    keywordAbility(KeywordAbility.wardDiscard())

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE).youControl().nontoken(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER,
        )
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{1}"),
            effect = Effects.Composite(
                CreateTokenEffect(
                    power = 1,
                    toughness = 1,
                    colors = setOf(Color.BLACK),
                    creatureTypes = setOf("Bat"),
                    keywords = setOf(Keyword.FLYING),
                    triggeredAbilities = listOf(timotharBatReturnTrigger),
                ),
                Effects.Move(
                    target = EffectTarget.TriggeringEntity,
                    destination = Zone.EXILE,
                    fromZone = Zone.GRAVEYARD,
                    linkToTarget = EffectTarget.PipelineTarget(CREATED_TOKENS, 0),
                ),
            ),
        )
        description = "Whenever another nontoken Vampire you control dies, you may pay {1} and " +
            "exile it. If you do, create a 1/1 black Bat creature token with flying. It gains " +
            "\"When this token deals combat damage to a player, sacrifice it and return the exiled " +
            "card to the battlefield tapped.\""
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "4"
        artist = "Jason A. Engle"
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a148447f-e3bd-4a49-a802-4d489f3c50d9.jpg?1783925008"
    }
}
