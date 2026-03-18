package dev.mrshawn.deathmessages.nms.spigot.v1_21_3;

import org.bukkit.block.Biome;

public class NMSAdaptorImpl extends dev.mrshawn.deathmessages.nms.spigot.v1_16_5.NMSAdaptorImpl {

    @Override
    public String biomeKeyName(Biome biome) {
        return biome.getKey().getKey();
    }
}
