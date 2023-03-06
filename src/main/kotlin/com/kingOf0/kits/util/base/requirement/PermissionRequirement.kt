package com.kingOf0.kits.util.base.requirement

import com.kingOf0.kits.util.base.Replacer
import com.kingOf0.kits.util.result.UpgradeResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class PermissionRequirement(section: ConfigurationSection) : BaseRequirement() {

    private val permission: String

    init {
        permission = section.getString("permission")!!
    }

    override fun check(player: Player): Pair<UpgradeResult, Replacer> {
        return if (player.hasPermission(permission)) Pair(UpgradeResult.OK, Replacer())
        else Pair(UpgradeResult.NOT_ENOUGH_PERMISSION, Replacer().add("permission", permission))
    }

}
