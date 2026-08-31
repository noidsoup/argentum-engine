package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Scryfall artwork for the 0/0 black Phyrexian Germ token Living weapon creates.
 */
private const val LIVING_WEAPON_GERM_TOKEN_IMAGE =
    "https://cards.scryfall.io/normal/front/6/1/61f94e32-3b22-4c47-b866-1f36a7f3c734.jpg?1783934659"

/**
 * Add Living weapon — keyword + enters-the-battlefield triggered ability.
 *
 * "Living weapon (When this Equipment enters, create a 0/0 black Phyrexian Germ creature token,
 * then attach this to it.)"
 *
 * Mirrors [jobSelect]: create token → publish to [CREATED_TOKENS] → attach source Equipment.
 */
fun CardBuilder.livingWeapon() {
    keywordSet.add(Keyword.LIVING_WEAPON)
    triggeredAbilities.add(
        TriggeredAbility.create(
            trigger = Triggers.EntersBattlefield.event,
            binding = Triggers.EntersBattlefield.binding,
            effect = Effects.Composite(
                Effects.CreateToken(
                    power = 0,
                    toughness = 0,
                    colors = setOf(Color.BLACK),
                    creatureTypes = setOf("Phyrexian", "Germ"),
                    imageUri = LIVING_WEAPON_GERM_TOKEN_IMAGE,
                ),
                Effects.AttachEquipment(EffectTarget.PipelineTarget(CREATED_TOKENS, 0)),
            ),
            descriptionOverride = "Living weapon (When this Equipment enters, create a 0/0 " +
                "black Phyrexian Germ creature token, then attach this to it.)",
        ),
    )
}
