package dev.mrshawn.deathmessages.nms.paper.v1_21_3;

import org.bukkit.block.Biome;

public class NMSAdaptorImpl extends dev.mrshawn.deathmessages.nms.paper.v1_16_5.NMSAdaptorImpl {

    @Override
    public String biomeKeyName(Biome biome) {
        return biome.key().value();
    }
}
