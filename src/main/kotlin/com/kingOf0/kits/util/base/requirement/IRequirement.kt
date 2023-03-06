package com.kingOf0.kits.util.base.requirement

import com.kingOf0.kits.util.base.Replacer
import com.kingOf0.kits.util.result.UpgradeResult
import org.bukkit.entity.Player

interface IRequirement {

    fun check(player: Player): Pair<UpgradeResult, Replacer>

    fun run(player: Player)

}

