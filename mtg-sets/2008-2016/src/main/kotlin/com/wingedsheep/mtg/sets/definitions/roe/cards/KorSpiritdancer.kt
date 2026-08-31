package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.AttachmentKind
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Kor Spiritdancer
 * {1}{W}
 * Creature — Kor Wizard
 * 0/2
 *
 * This creature gets +2/+2 for each Aura attached to it.
 * Whenever you cast an Aura spell, you may draw a card.
 */
val KorSpiritdancer = card("Kor Spiritdancer") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Wizard"
    oracleText = "This creature gets +2/+2 for each Aura attached to it.\n" +
        "Whenever you cast an Aura spell, you may draw a card."
    power = 0
    toughness = 2

    val auraBonus = DynamicAmount.Multiply(
        DynamicAmount.EntityProperty(
            EntityReference.Source,
            EntityNumericProperty.AttachmentCount(AttachmentKind.AURA),
        ),
        2,
    )

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = auraBonus,
            toughnessBonus = auraBonus,
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastSubtype(Subtype.AURA)
        optional = true
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "31"
        artist = "Scott Chou"
        flavorText = "She reaches beyond the physical realm, touching the ideals from which all creatures draw their power."
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03066fa1-ea16-4a87-9218-120efa976909.jpg?1783942006"
        ruling(
            "2021-03-19",
            "An ability that triggers when a player casts a spell resolves before the spell that caused it to trigger. It resolves even if that spell is countered.",
        )
    }
}
