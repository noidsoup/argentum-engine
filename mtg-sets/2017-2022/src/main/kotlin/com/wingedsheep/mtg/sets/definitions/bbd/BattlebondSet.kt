package com.wingedsheep.mtg.sets.definitions.bbd

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Battlebond (2018)
 *
 * Set Code: BBD
 * Release Date: June 8, 2018
 *
 * Scaffold for canonical definitions whose earliest real printing is in this set.
 */
object BattlebondSet : MtgSet {

    override val code = "BBD"
    override val displayName = "Battlebond"
    override val releaseDate = "2018-06-08"
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.bbd.cards"
}
