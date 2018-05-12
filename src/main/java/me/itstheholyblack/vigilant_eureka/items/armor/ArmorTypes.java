package me.itstheholyblack.vigilant_eureka.items.armor;

import me.itstheholyblack.vigilant_eureka.Reference;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

class ArmorTypes {
    static final ItemArmor.ArmorMaterial BUGGER_ME_MAT = EnumHelper.addArmorMaterial("BUGGER_ME_MAT",
            Reference.MOD_ID + ":bugger_me", 20, new int[]{2, 6, 5, 2}, 40, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            0.0F);
}
