/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.object.builder.v1.entity.boat;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

/**
 * A custom boat type representing boats made from a specific material.
 * This record contains data needed for boat logic.
 *
 * <p>New boat types can be registered by placing them in {@code data/<mod id>/fabric/boat_type/<boat type id>.json}
 * with the following format (all keys are optional):
 * <pre>
 * {
 *   // The planks item
 *   "planks": "my_mod:fabricium_planks",
 *
 *   // The regular boat item
 *   "boat": "my_mod:fabricium_boat",
 *
 *   // The chest boat item
 *   "chest_boat": "my_mod:fabricium_chest_boat"
 * }
 * </pre>
 *
 * @param planks    the planks item, can be null
 * @param boat      the boat item, can be null
 * @param chestBoat the chest boat item, can be null
 */
public record FabricBoatType(
		@Nullable ItemConvertible planks,
		@Nullable ItemConvertible boat,
		@Nullable ItemConvertible chestBoat
) {
	/**
	 * The key of the boat type registry, {@code fabric:boat_type}.
	 */
	public static final RegistryKey<Registry<FabricBoatType>> REGISTRY_KEY =
			RegistryKey.ofRegistry(new Identifier("fabric", "boat_type"));

	/**
	 * The codec used for loading boat types from data packs.
	 */
	public static final Codec<FabricBoatType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registries.ITEM.getCodec().optionalFieldOf("planks").forGetter(type -> asOptional(type.planks)),
			Registries.ITEM.getCodec().optionalFieldOf("boat").forGetter(type -> asOptional(type.boat)),
			Registries.ITEM.getCodec().optionalFieldOf("chest_boat").forGetter(type -> asOptional(type.chestBoat))
	).apply(instance, (planks, boat, chestBoat) -> new FabricBoatType(planks.orElse(null), boat.orElse(null), chestBoat.orElse(null))));

	/**
	 * The codec used for syncing boat types.
	 */
	public static final Codec<FabricBoatType> NETWORK_CODEC = Codec.unit(() -> new FabricBoatType(null, null, null));

	private static Optional<Item> asOptional(@Nullable ItemConvertible item) {
		return Optional.ofNullable(item).map(ItemConvertible::asItem);
	}
}
