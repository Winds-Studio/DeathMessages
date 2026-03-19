package dev.mrshawn.deathmessages.hooks;

import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.ErrorTracker;
import dev.mrshawn.deathmessages.DeathMessages;

public class FastStatsHook {

    // Context-aware error tracker
    private static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();

    private final BukkitMetrics metrics = BukkitMetrics.factory()
            .errorTracker(ERROR_TRACKER)
            .token("0daf780ef0aac23f13009a6769a9992e")
            .create(DeathMessages.getInstance());

    public BukkitMetrics get() {
        return metrics;
    }
}
