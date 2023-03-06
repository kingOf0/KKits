package com.kingOf0.kits.manager

import com.kingOf0.kits.PLUGIN_INSTANCE
import com.kingOf0.kits.util.config.ConfigFile
import com.kingOf0.kits.util.item.ItemData
import com.kingOf0.kits.util.item.KItem
import com.kingOf0.kits.util.item.RefugeeItemData

object YamlItemManager : IManager("YamlItemManager") {

    var items = HashMap<KItem, ItemData>()
    var refugeeItems = HashMap<String, ArrayList<RefugeeItemData>>()

    override fun load(): Boolean {
        val file = ConfigFile("items", PLUGIN_INSTANCE)
        for (id in file.getKeys(false)) {
            runCatching {
                items[KItem.valueOf(id)] = ItemData(file.getConfigurationSection(id)!!)
            }.onFailure {
                val nonInventoryItem = RefugeeItemData(file.getConfigurationSection(id)!!)
                refugeeItems.getOrPut(nonInventoryItem.provider) { ArrayList() }.add(RefugeeItemData(file.getConfigurationSection(id)!!))
            }
        }
        return true
    }

    fun reload() {
        items.clear()
        load()
    }

}