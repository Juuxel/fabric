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

package net.fabricmc.fabric.impl.object.builder.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.registry.RegistryKey;

import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatType;

public final class BoatRenderingRegistryImpl {
	public static final Map<Key, Settings> ALL_SETTINGS = new HashMap<>();

	public static void registerBoat(RegistryKey<FabricBoatType> type, boolean chest) {
		register(type, false, chest);
	}

	public static void registerRaft(RegistryKey<FabricBoatType> type, boolean chest) {
		register(type, true, chest);
	}

	private static void register(RegistryKey<FabricBoatType> type, boolean raft, boolean chest) {
		Objects.requireNonNull(type, "Boat type cannot be null");
		Key key = new Key(type, chest);

		if (ALL_SETTINGS.putIfAbsent(key, new Settings(type, raft, chest)) != null) {
			throw new IllegalArgumentException("Trying to register a boat " + type + " for rendering multiple times!");
		}
	}

	private record Key(RegistryKey<FabricBoatType> type, boolean chest) {
	}

	public record Settings(RegistryKey<FabricBoatType> type, boolean raft, boolean chest) {
	}
}
