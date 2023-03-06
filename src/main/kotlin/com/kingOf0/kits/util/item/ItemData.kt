package com.kingOf0.kits.util.item

import com.cryptomorin.xseries.XItemStack
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

open class ItemData(section: ConfigurationSection) {

    val itemStack: ItemStack

    var row: Int
    var column: Int

    init {
        itemStack = XItemStack.deserialize(section.getConfigurationSection("item")!!)!!
        row = section.getInt("row")
        column = section.getInt("column")
    }

}