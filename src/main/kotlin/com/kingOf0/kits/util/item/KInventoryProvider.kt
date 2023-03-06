package com.kingOf0.kits.util.item

import com.kingOf0.kits.manager.YamlItemManager.refugeeItems

open class KInventoryProvider(val id: String) :
    com.kingOf0.kits.shade.smartinventory.InventoryProvider {

    fun setup(contents: com.kingOf0.kits.shade.smartinventory.InventoryContents) {
        KItem.FILL.fill(contents)
        KItem.FILL_EMPTIES.fillEmpties(contents)
        refugeeItems[id]?.forEach {
            it.set(contents)
        }
    }

}