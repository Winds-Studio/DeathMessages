package dev.mrshawn.deathmessages.nms.spigot.v1_13;

import org.bukkit.GameRule;
import org.bukkit.World;

public class NMSAdaptorImpl extends dev.mrshawn.deathmessages.nms.spigot.v1_12_2.NMSAdaptorImpl {

    @Override
    public boolean showDeathMessages(World world) {
        return Boolean.TRUE.equals(world.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES));
    }

    @Override
    public void showDeathMessages(World world, boolean show) {
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, show);
    }
}
