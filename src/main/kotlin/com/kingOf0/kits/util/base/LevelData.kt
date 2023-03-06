package com.kingOf0.kits.util.base

import com.kingOf0.kits.LOGGER
import com.kingOf0.kits.util.base.requirement.EconomyRequirement
import com.kingOf0.kits.util.base.requirement.IRequirement
import com.kingOf0.kits.util.base.requirement.PermissionRequirement
import com.kingOf0.kits.util.result.UpgradeResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*

class LevelData(
    section: ConfigurationSection
) {

    val requirements: ArrayList<IRequirement> = ArrayList()

    init {
        section.getConfigurationSection("requirements")?.apply {
            for (s in getKeys(false)) {
                val x = getConfigurationSection(s)!!
                when(s.lowercase(Locale.ENGLISH)) {
                    "economy" -> requirements += EconomyRequirement(x)
                    "permission" -> requirements += PermissionRequirement(x)
                    else -> LOGGER.warning("- Invalid requirement '$s' for level ${section.name}")
                }
            }
        }

    }

    fun canLevelUp(player: Player): Pair<UpgradeResult, Replacer> {
        for (requirement in requirements) {
            requirement.check(player).apply {
                if (first != UpgradeResult.OK)
                    return this
            }
        }
        return Pair(UpgradeResult.OK, Replacer())
    }

    fun levelUp(player: Player) {
        requirements.forEach {
            it.run(player)
        }
    }
}