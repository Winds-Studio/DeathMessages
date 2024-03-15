package dev.mrshawn.deathmessages.config.modules;

import dev.mrshawn.deathmessages.utils.EntityUtil;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfHeader({
        "DeathMessages by Dreeam__ (Original by MrShawn/Joshb_/CosmoConsole[DeathMessagesPrime Author])",
        ""
})
public interface Gangs {

    @ConfKey("Gang.Enabled")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(1)
    boolean enableGangs();

    static Map<String, Level> defaultGangs() {
        Map<String, Level> gangs = new LinkedHashMap<>();

        EntityUtil.getEntities().forEach((e) -> gangs.put(e, Level.of(5, 4)));

        return gangs;
    }

    @ConfKey("Gang.Mobs")
    @ConfDefault.DefaultObject("defaultGangs")
    @AnnotationBasedSorter.Order(2)
    Map<String, @SubSection Level> mobs();

    interface Level {

        int Radius();

        int Amount();

        static Level of(int radius, int amount) {
            return new Level() {

                @Override
                public int Radius() {
                    return radius;
                }

                @Override
                public int Amount() {
                    return amount;
                }
            };
        }
    }
}

