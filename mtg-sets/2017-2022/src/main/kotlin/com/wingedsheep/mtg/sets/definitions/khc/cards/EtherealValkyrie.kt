package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ethereal Valkyrie — Kaldheim Commander (KHC) #16
 * {4}{W}{U} · Creature — Spirit Angel 4/4
 *
 * Flying
 * Whenever this creature enters or attacks, draw a card, then exile a card from your hand face
 * down. It becomes foretold. Its foretell cost is its mana cost reduced by {2}.
 *
 * "Enters or attacks" is two sibling triggers (Stadium Tidalmage). The draw-then-exile-foretell
 * pipeline mirrors Jacob Hauken, Inspector's hand exile, then [Effects.MakeForetold] stamps
 * foretold state and a mana-cost-minus-{2} foretell cast option (CR 702.143d) without paying the
 * keyword's {2} setup cost.
 */
val EtherealValkyrie = card("Ethereal Valkyrie") {
    manaCost = "{4}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Creature — Spirit Angel"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "Whenever this creature enters or attacks, draw a card, then exile a card from your hand " +
        "face down. It becomes foretold. Its foretell cost is its mana cost reduced by {2}. " +
        "(On a later turn, you may cast it for its foretell cost, even if this creature has left " +
        "the battlefield.)"

    keywords(Keyword.FLYING)

    val foretellFromHand = Effects.Composite(
        Effects.DrawCards(1),
        GatherCardsEffect(
            source = CardSource.FromZone(Zone.HAND, Player.You),
            storeAs = "valkyrieHand",
        ),
        SelectFromCollectionEffect(
            from = "valkyrieHand",
            selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
            storeSelected = "valkyrieForetold",
            prompt = "Choose a card to exile face down",
        ),
        MoveCollectionEffect(
            from = "valkyrieForetold",
            destination = CardDestination.ToZone(Zone.EXILE),
            faceDown = FaceDownMode.HIDDEN,
        ),
        Effects.MakeForetold(from = "valkyrieForetold"),
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = foretellFromHand
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = foretellFromHand
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "16"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/5/5/552912bf-4085-49ad-902f-5c41540df97a.jpg?1783928335"
    }
}
