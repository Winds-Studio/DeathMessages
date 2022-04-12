package net.joshb.deathmessages.assets;

import org.bukkit.block.Biome;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {

    public static List<String> biomes = Stream.of(Biome.values()).map(Biome::name).collect(Collectors.toList());
}
