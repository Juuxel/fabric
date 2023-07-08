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

package net.fabricmc.fabric.api.object.builder.v1.client;

import java.util.Objects;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatType;
import net.fabricmc.fabric.impl.object.builder.client.BoatRenderingRegistryImpl;

/**
 * A registry for managing boat rendering data.
 *
 * <h2>Usage</h2>
 *
 * A custom boat or raft needs to have two rendering registrations:
 * <ul>
 *     <li>Model and texture data with {@code BoatRenderingRegistry}:<ul>
 *         <li>Boat and chest boat: {@link #registerBoat(RegistryKey)}</li>
 *         <li>Boat or chest boat: {@link #registerBoat(RegistryKey, boolean)}</li>
 *         <li>Raft and chest raft: {@link #registerRaft(RegistryKey)}</li>
 *         <li>Raft or chest raft: {@link #registerRaft(RegistryKey, boolean)}</li>
 *     </ul></li>
 *     <li>Model layer and its textured model data. It can be registered with Fabric Rendering API (v1)'s
 *     {@link net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry EntityModelLayerRegistry}.
 *
 *     <p>The model layer is available with {@link #getModelLayer}, and the textured model data can be retrieved
 *     from vanilla classes:<ul>
 *         <li>{@link net.minecraft.client.render.entity.model.BoatEntityModel}</li>
 *         <li>{@link net.minecraft.client.render.entity.model.ChestBoatEntityModel}</li>
 *         <li>{@link net.minecraft.client.render.entity.model.RaftEntityModel}</li>
 *         <li>{@link net.minecraft.client.render.entity.model.ChestRaftEntityModel}</li>
 *     </ul></li>
 * </ul>
 *
 * <h2>Example</h2>
 *
 * <pre>
 * BoatRenderingRegistry.{@link #registerBoat(RegistryKey) registerBoat}(MY_BOAT_TYPE);
 * // Boat without chest
 * {@link net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry EntityModelLayerRegistry}.registerModelLayer(
 * 	BoatRenderingRegistry.{@link #getModelLayer getModelLayer}(MY_BOAT_TYPE, false),
 * 	BoatEntityModel::{@link net.minecraft.client.render.entity.model.BoatEntityModel#getTexturedModelData getTexturedModelData}
 * );
 * // Boat with chest
 * EntityModelLayerRegistry.registerModelLayer(
 * 	BoatRenderingRegistry.getModelLayer(MY_BOAT_TYPE, true),
 * 	ChestBoatEntityModel::{@link net.minecraft.client.render.entity.model.ChestBoatEntityModel#getTexturedModelData getTexturedModelData}
 * );
 * </pre>
 */
public final class BoatRenderingRegistry {
	/**
	 * Gets a model layer for a {@link FabricBoatType}.
	 *
	 * @param type  the registry key of the boat type
	 * @param chest {@code true} if the model layer is for a chest boat, {@code false} otherwise
	 * @return the model layer
	 */
	public static EntityModelLayer getModelLayer(RegistryKey<FabricBoatType> type, boolean chest) {
		Objects.requireNonNull(type, "Boat type cannot be null");
		Identifier id = type.getValue();
		String boatKind = chest ? "chest_boat/" : "boat/";
		return new EntityModelLayer(new Identifier(id.getNamespace(), boatKind + id.getPath()), "main");
	}

	/**
	 * Registers boat rendering for a boat type.
	 * This includes boats with and without a chest.
	 *
	 * @param type the registry key of the boat type
	 */
	public static void registerBoat(RegistryKey<FabricBoatType> type) {
		registerBoat(type, false);
		registerBoat(type, true);
	}

	/**
	 * Registers boat rendering for a boat type.
	 *
	 * @param type the registry key of the boat type
	 * @param chest {@code true} if rendering is registered for a chest boat, {@code false} otherwise
	 */
	public static void registerBoat(RegistryKey<FabricBoatType> type, boolean chest) {
		BoatRenderingRegistryImpl.registerBoat(type, chest);
	}

	/**
	 * Registers raft rendering for a boat type.
	 * This includes rafts with and without a chest.
	 *
	 * @param type the registry key of the boat type
	 */
	public static void registerRaft(RegistryKey<FabricBoatType> type) {
		registerRaft(type, false);
		registerRaft(type, true);
	}

	/**
	 * Registers raft rendering for a boat type.
	 *
	 * @param type the registry key of the boat type
	 * @param chest {@code true} if rendering is registered for a chest raft, {@code false} otherwise
	 */
	public static void registerRaft(RegistryKey<FabricBoatType> type, boolean chest) {
		BoatRenderingRegistryImpl.registerRaft(type, chest);
	}
}
