package com.wingedsheep.mtg.sets.definitions.eve

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Eventide (2009)
 *
 * Set Code: EVE
 * Release Date: July 17, 2009
 */
object EventideSet : MtgSet {

    override val code = "EVE"
    override val displayName = "Eventide"
    override val releaseDate = "2009-07-17"
    override val block = "Shadowmoor"
    override val incomplete = true
    override val sealedSupported = false

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.eve.cards"
}
