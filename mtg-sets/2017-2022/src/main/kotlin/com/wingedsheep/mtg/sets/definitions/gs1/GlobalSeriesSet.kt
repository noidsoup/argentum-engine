package com.wingedsheep.mtg.sets.definitions.gs1

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Global Series: Jiang Yanggu & Mu Yanling (2018)
 *
 * All 40 cards in the product: 30 unique definitions, 6 reprint rows, and 4 basic lands.
 * Hosts the earliest real printing of cards later reprinted elsewhere (e.g. Ancestor Dragon).
 *
 * Set Code: GS1
 * Release Date: 2018-06-22
 */
object GlobalSeriesSet : MtgSet {

    override val code = "GS1"
    override val displayName = "Global Series: Jiang Yanggu & Mu Yanling"
    override val releaseDate = "2018-06-22"
    override val sealedSupported = false

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.gs1.cards"
}
