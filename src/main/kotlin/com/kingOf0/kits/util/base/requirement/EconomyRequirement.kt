package com.kingOf0.kits.util.base.requirement

import com.kingOf0.kits.ECONOMY
import com.kingOf0.kits.util.base.Replacer
import com.kingOf0.kits.util.result.UpgradeResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class EconomyRequirement(section: ConfigurationSection) : BaseRequirement() {

    private val value: Double

    init {
        value = section.getDouble("value")
    }

    override fun check(player: Player): Pair<UpgradeResult, Replacer> {
        return if (ECONOMY.has(player, value)) {
            Pair(UpgradeResult.OK, Replacer())
        } else {
            Pair(UpgradeResult.NOT_ENOUGH_MONEY, Replacer().add("value", value))
        }
    }

    override fun run(player: Player) {
        ECONOMY.withdrawPlayer(player, value)
    }

}
