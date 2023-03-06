package com.kingOf0.kits.provider

import com.kingOf0.kits.base.KitData
import com.kingOf0.kits.shade.smartinventory.Icon
import com.kingOf0.kits.shade.smartinventory.InventoryContents
import com.kingOf0.kits.util.base.Replacer
import com.kingOf0.kits.util.item.KInventoryProvider
import com.kingOf0.kits.util.item.KItem

class PreviewProvider(private val kit: KitData) : KInventoryProvider("preview") {


    override fun init(contents: InventoryContents) {
        setup(contents)

        for (item in kit.items) {
            contents.add(Icon.cancel(item))
        }
        for (command in kit.commands) {
            KItem.COMMAND_PREVIEW.replace(Replacer().add("command", command))
                ?.let { contents.add(Icon.cancel(it)) }
        }

    }

}
