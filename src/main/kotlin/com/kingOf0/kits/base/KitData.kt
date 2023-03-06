package com.kingOf0.kits.base

import com.cryptomorin.xseries.XItemStack
import com.kingOf0.kits.LOGGER
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.math.min

class KitData(val id: String, section: ConfigurationSection) {

    val items: MutableList<ItemStack> = ArrayList()

    val permission: String
    val commands: List<String>
    val cooldown: Long
    val icon: ItemStack?
    val name: String

    val rows: Int

    init {
        section.getConfigurationSection("items").apply {
            for (key in getKeys(false)) {
                runCatching {
                    val itemSection = getConfigurationSection(key)
                    val itemStack = XItemStack.deserialize(itemSection)

                    items.add(itemStack)
                }.onFailure {
                    LOGGER.warning("Failed to load kit ${section.name}! Error: ${it.message}")
                }
            }
        }

        cooldown = section.getLong("cooldown") * 1000L
        permission = section.getString("permission", "kits.$id")!!
        commands = section.getStringList("commands")
        rows = min(6, ((commands.size / 9) + (items.size / 9)) + 1)
        name = section.getString("name", id)!!
        icon = if (section.isConfigurationSection("icon")) {
            XItemStack.deserialize(section.getConfigurationSection("icon"))
        } else null
    }

    fun giveItems(player: Player) {
        player.inventory.addItem(*items.toTypedArray()).values.forEach {
            player.world.dropItem(player.location, it).velocity = Vector()
        }
    }

    fun executeCommands(player: Player) {
        commands.forEach {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.replace("%player%", player.name))
        }
    }

}