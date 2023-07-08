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

import org.jetbrains.annotations.Nullable;

import net.minecraft.registry.entry.RegistryEntry;

/**
 * Extensions to {@link net.minecraft.entity.vehicle.BoatEntity}.
 *
 * <p>Note: This interface is automatically implemented on all boats via Mixin and interface injection.
 */
public interface FabricBoatEntity {
	/**
	 * Returns the {@code FabricBoatType} of this boat.
	 *
	 * <p>The returned registry is always from the {@link FabricBoatType#REGISTRY_KEY fabric:boat_type} registry.
	 *
	 * @return a registry entry referring to the boat type, or {@code null} if not set
	 */
	default @Nullable RegistryEntry<FabricBoatType> getFabricBoatType() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the new {@code FabricBoatType} of this boat.
	 *
	 * <p>The new boat type, if not {@code null}, must be an entry from the
	 * {@link FabricBoatType#REGISTRY_KEY fabric:boat_type} registry.
	 *
	 * @param boatType a registry entry referring to the boat type, or {@code null} to unset the {@code FabricBoatType}
	 */
	default void setFabricBoatType(@Nullable RegistryEntry<FabricBoatType> boatType) {
		throw new UnsupportedOperationException();
	}
}
