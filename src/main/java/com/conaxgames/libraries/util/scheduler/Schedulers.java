package com.conaxgames.libraries.util.scheduler;

import org.bukkit.Server;

public final class Schedulers {

    private static final Class<?> REGIONIZED_SERVER = loadRegionizedServer();

    private Schedulers() {}

    public static Scheduler forServer(Server server) {
        if (REGIONIZED_SERVER != null && REGIONIZED_SERVER.isInstance(server)) {
            return new FoliaScheduler();
        }
        return new PaperScheduler();
    }

    private static Class<?> loadRegionizedServer() {
        try {
            return Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
