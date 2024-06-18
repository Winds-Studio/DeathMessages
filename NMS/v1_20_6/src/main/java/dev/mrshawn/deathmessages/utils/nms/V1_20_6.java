/*
 * This file is part of InteractiveChat.
 * @see https://github.com/LOOHP/InteractiveChat/blob/b0c0f1c12d97026953526536942d1aba62da2080/V1_20_6/src/main/java/com/loohp/interactivechat/nms/V1_20_6.java
 *
 * Copyright (C) 2024. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2024. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package dev.mrshawn.deathmessages.utils.nms;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.serializer.gson.GsonDataComponentValue;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class V1_20_6 implements Wrapper {

    public final Gson GSON = new Gson();

    @Override
    public Map<Key, DataComponentValue> getItemStackComponentsMap(ItemStack i) {
        if (i.getType().isAir()) {
            return Collections.emptyMap();
        }
        IRegistryCustom registryAccess = ((CraftWorld) Bukkit.getWorlds().getFirst()).getHandle().H_();
        net.minecraft.world.item.ItemStack nmsItemStack = toNMSCopy(i);
        DataComponentPatch dataComponentPatch = nmsItemStack.d();
        Map<Key, DataComponentValue> convertedComponents = new HashMap<>();
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : dataComponentPatch.b()) {
            DataComponentType<?> type = entry.getKey();
            Optional<?> optValue = entry.getValue();
            MinecraftKey minecraftKey = BuiltInRegistries.as.b(type);
            Key key = Key.key(minecraftKey.b(), minecraftKey.a());
            if (optValue.isPresent()) {
                Codec codec = type.b();
                if (codec != null) {
                    Object nativeJsonElement = codec.encodeStart(registryAccess.a(JsonOps.INSTANCE), optValue.get()).getOrThrow();
                    JsonElement jsonElement = fromNative(nativeJsonElement);
                    DataComponentValue value = GsonDataComponentValue.gsonDataComponentValue(jsonElement);
                    convertedComponents.put(key, value);
                }
            } else {
                convertedComponents.put(key, DataComponentValue.removed());
            }
        }

        return convertedComponents;
    }

    public net.minecraft.world.item.ItemStack toNMSCopy(ItemStack itemstack) {
        return CraftItemStack.asNMSCopy(itemstack);
    }

    public JsonElement fromNative(Object nativeJsonElement) {
        String json = GSON.toJson(nativeJsonElement);
        return GSON.fromJson(json, JsonElement.class);
    }
}
