package dev.mrshawn.deathmessages.nms.paper.v1_21_4;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class NMSAdaptorImpl extends dev.mrshawn.deathmessages.nms.paper.v1_21_3.NMSAdaptorImpl {

    @Override
    public Component itemDisplayName(ItemStack i) {
        return i.effectiveName();
    }
}
