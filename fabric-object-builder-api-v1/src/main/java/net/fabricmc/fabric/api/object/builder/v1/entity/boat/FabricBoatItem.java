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

import java.util.NoSuchElementException;
import java.util.Objects;

import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

/**
 * A boat item that uses a {@link FabricBoatType}.
 */
public class FabricBoatItem extends BoatItem {
	private final RegistryKey<FabricBoatType> boatType;

	/**
	 * Constructs a {@code FabricBoatItem}.
	 *
	 * @param chest    {@code true} if this is an item for a boat with a chest, {@code false} otherwise
	 * @param boatType the registry key of the boat type
	 * @param settings the item settings
	 */
	public FabricBoatItem(boolean chest, RegistryKey<FabricBoatType> boatType, Settings settings) {
		super(chest, BoatEntity.Type.OAK, settings);
		Objects.requireNonNull(boatType, "Boat type cannot be null");
		this.boatType = boatType;
	}

	/**
	 * {@return the registry key of the boat type of this item}.
	 */
	public RegistryKey<FabricBoatType> getBoatType() {
		return boatType;
	}

	@Override
	protected BoatEntity createEntity(World world, HitResult hitResult) {
		BoatEntity boat = super.createEntity(world, hitResult);
		RegistryEntry<FabricBoatType> typeEntry = world.getRegistryManager()
				.get(FabricBoatType.REGISTRY_KEY)
				.getEntry(boatType)
				.orElseThrow(() -> new NoSuchElementException("Boat type not found: " + boatType));
		boat.setFabricBoatType(typeEntry);
		return boat;
	}
}
