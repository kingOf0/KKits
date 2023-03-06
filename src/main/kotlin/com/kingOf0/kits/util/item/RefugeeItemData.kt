package com.kingOf0.kits.util.item

import org.bukkit.configuration.ConfigurationSection

class RefugeeItemData(section: ConfigurationSection) : ItemData(section) {

    val provider: String
    init {
        provider = section.getString("provider", "")!!
    }

    fun set(contents: com.kingOf0.kits.shade.smartinventory.InventoryContents) {
        contents.set(row, column, com.kingOf0.kits.shade.smartinventory.Icon.cancel(itemStack.clone()))
    }

}