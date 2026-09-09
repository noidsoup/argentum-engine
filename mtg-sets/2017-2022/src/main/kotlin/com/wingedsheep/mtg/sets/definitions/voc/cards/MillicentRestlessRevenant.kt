package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Millicent, Restless Revenant
 * {5}{W}{U}
 * Legendary Creature — Spirit Soldier
 * 4/4
 *
 * Affinity for Spirits
 * Flying
 * Whenever Millicent or another nontoken Spirit you control dies or deals combat damage to a
 * player, create a 1/1 white Spirit creature token with flying.
 *
 * "Dies or deals combat damage" is two triggered abilities sharing one effect — the Daemogoth
 * Titan / Syr Vondam idiom. "Millicent or another nontoken Spirit you control" is one ANY-bound
 * filter over nontoken Spirits you control (the Fili the Pathfinder shape); Spirit tokens don't
 * re-trigger because of `nontoken()`. Combat damage uses [GrantTriggeredAbility] (the Seshiro the
 * Anointed shape) so each nontoken Spirit carries its own SELF-bound trigger — the subtype combat-
 * damage fast path would otherwise ignore `nontoken()` on a bare [Triggers.dealsDamage] filter.
 */
val MillicentRestlessRevenant = card("Millicent, Restless Revenant") {
    manaCost = "{5}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Spirit Soldier"
    oracleText = "Affinity for Spirits (This spell costs {1} less to cast for each Spirit you control.)\n" +
        "Flying\n" +
        "Whenever Millicent or another nontoken Spirit you control dies or deals combat damage to " +
        "a player, create a 1/1 white Spirit creature token with flying."
    power = 4
    toughness = 4

    keywordAbility(KeywordAbility.AffinityForSubtype(Subtype.SPIRIT))
    keywords(Keyword.FLYING)

    val nontokenSpiritYouControl =
        GameObjectFilter.Creature.withSubtype(Subtype.SPIRIT).nontoken().youControl()

    val createSpiritToken = Effects.CreateToken(
        power = 1,
        toughness = 1,
        colors = setOf(Color.WHITE),
        creatureTypes = setOf("Spirit"),
        keywords = setOf(Keyword.FLYING),
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83497714-97ae-4846-8096-f7f1524f0e09.jpg?1783924702",
    )

    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(
                filter = nontokenSpiritYouControl,
                from = Zone.BATTLEFIELD,
                to = Zone.GRAVEYARD,
            ),
            binding = TriggerBinding.ANY,
        )
        effect = createSpiritToken
        description = "Whenever Millicent or another nontoken Spirit you control dies, create a 1/1 " +
            "white Spirit creature token with flying."
    }

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.DealsCombatDamageToPlayer.event,
                binding = Triggers.DealsCombatDamageToPlayer.binding,
                effect = createSpiritToken,
                descriptionOverride = "Whenever this creature deals combat damage to a player, " +
                    "create a 1/1 white Spirit creature token with flying.",
            ),
            filter = GroupFilter(nontokenSpiritYouControl),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "1"
        artist = "Denman Rooke"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b86b538-0766-440d-a2cd-f5d5bfcfb010.jpg?1783925010"
    }
}
