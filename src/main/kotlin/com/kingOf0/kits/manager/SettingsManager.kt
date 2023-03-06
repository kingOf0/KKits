package com.kingOf0.kits.manager

import com.kingOf0.kits.manager.FileManager.config
import com.kingOf0.kits.shade.smartinventory.event.abs.ClickEvent
import com.kingOf0.kits.util.KUtils.disable
import org.bukkit.event.inventory.ClickType

object SettingsManager : IManager("SettingsManager") {

    var now: String = "Şimdi"
    var days = "gün"
    var hours = "saat"
    var minutes = "dakika"
    var seconds = "saniye"

    var targetOffline: String = "TargetOffline"
    var noConsole: String = "Console cant use this command!"
    var notNow: String = "§cNot now"
    var reloaded: String = "§cReloaded"
    var noPermission: String = "§cYou don't have permission to execute this command!"
    var internalError: String = "§cInternal error occured!"
    var kitNotFound: String = "§cKit not found!"
    var onCooldown: String = "§cYou are on cooldown for this kit!"
    var givenKit: String = "§cYou have been given the kit §e%kit%§c!"
    var help = arrayOf(
        "",
        "§c§lKKits Help",
        "§c/kkits §7- §eOpens the kit menu",
        "§c/kkits preview <kit> [player] §7- §eOpens the kit preview menu",
        "§c/kkits give <kit> <player> §7- §eGives the kit to the player",
        "§c/kkits reload §7- §eReloads the plugin",
        ""
    )

    var previewTitle: String = "§cPreview"
    var listTitle: String = "§cKits"
    var listRows: Int = 3

    var giveClick: ClickType = ClickType.LEFT

    var aliases: MutableList<String> = ArrayList()

    var day = 24 * 60 * 60 * 1000

    override fun load(): Boolean {
        config.getConfigurationSection("messages")?.apply {
            if (isString("notNow")) notNow = getString("notNow")!!
            if (isString("internalError")) internalError = getString("internalError")!!
            if (isString("reloaded")) reloaded = getString("reloaded")!!
            if (isString("noPermission")) noPermission = getString("noPermission")!!
            if (isString("noConsole")) noConsole = getString("noConsole")
            if (isString("targetOffline")) targetOffline = getString("targetOffline")
            if (isString("kitNotFound")) kitNotFound = getString("kitNotFound")
            if (isString("onCooldown")) onCooldown = getString("onCooldown")
            if (isString("givenKit")) givenKit = getString("givenKit")
            if (isList("help")) help = getStringList("help").toTypedArray()

        }
        config.getConfigurationSection("settings")?.apply {
            if (isInt("day")) day = getInt("day")
            if (isList("aliases")) aliases = getStringList("aliases")
            getConfigurationSection("listener")?.apply {
//                if (isBoolean("registerSpawnListener")) registerSpawnListener = getBoolean("registerSpawnListener")
            }
            if (isString("now")) now = getString("now")!!
            if (isString("days")) days = getString("days")!!
            if (isString("hours")) hours = getString("hours")!!
            if (isString("minutes")) minutes = getString("minutes")!!
            if (isString("seconds")) seconds = getString("seconds")!!
            if (isString("previewTitle")) previewTitle = getString("previewTitle")!!
            if (isString("listTitle")) listTitle = getString("listTitle")!!
            if (isInt("listRows")) listRows = getInt("listRows")

            runCatching {
                giveClick = ClickType.valueOf(getString("giveClick")!!)
            }
        }

        return true
    }

    fun reload() {
        load()
    }

}