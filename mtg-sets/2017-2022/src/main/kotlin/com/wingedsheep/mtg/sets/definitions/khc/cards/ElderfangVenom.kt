package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Elderfang Venom — Kaldheim Commander (KHC) #15
 * {2}{B}{G} · Enchantment
 *
 * Attacking Elves you control have deathtouch.
 * Whenever an Elf you control dies, each opponent loses 1 life and you gain 1 life.
 *
 * The deathtouch grant is the Crossway Troublemakers shape narrowed to attacking Elves. The death
 * trigger is Lathril's fixed life swing composed onto an Elf-only leaves-the-battlefield trigger.
 */
val ElderfangVenom = card("Elderfang Venom") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Enchantment"
    oracleText = "Attacking Elves you control have deathtouch.\n" +
        "Whenever an Elf you control dies, each opponent loses 1 life and you gain 1 life."

    val attackingElvesYouControl =
        GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.ELF).attacking().youControl())

    staticAbility {
        ability = GrantKeyword(Keyword.DEATHTOUCH, attackingElvesYouControl)
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.ELF).youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            GainLifeEffect(1),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "15"
        artist = "Alessandra Pisano"
        flavorText = "\"The Cosmos Serpent favored us with his venom. As thanks, we sink fangs " +
            "into those who dishonor the old ways.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d5bbb02-a04e-4add-ae9a-67819cffdb3a.jpg?1783928335"
    }
}
