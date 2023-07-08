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

import java.util.Objects;

import net.minecraft.block.dispenser.BoatDispenserBehavior;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.registry.RegistryKey;

/**
 * A dispenser behavior for placing boats with a {@link FabricBoatType}.
 */
public class FabricBoatDispenserBehavior extends BoatDispenserBehavior {
	private final RegistryKey<FabricBoatType> boatType;

	/**
	 * Constructs a {@code FabricBoatDispenserBehavior} for a boat without a chest.
	 *
	 * @param boatType the registry key of the boat type
	 */
	public FabricBoatDispenserBehavior(RegistryKey<FabricBoatType> boatType) {
		this(boatType, false);
	}

	/**
	 * Constructs a {@code FabricBoatDispenserBehavior}.
	 *
	 * @param boatType the registry key of the boat type
	 * @param chest    {@code true} if a chest boat should be placed, {@code false} otherwise
	 */
	public FabricBoatDispenserBehavior(RegistryKey<FabricBoatType> boatType, boolean chest) {
		super(BoatEntity.Type.OAK, chest);
		Objects.requireNonNull(boatType, "Boat type cannot be null");
		this.boatType = boatType;
	}

	/**
	 * {@return the registry key of the placed boat type}.
	 */
	public RegistryKey<FabricBoatType> getBoatType() {
		return boatType;
	}
}
