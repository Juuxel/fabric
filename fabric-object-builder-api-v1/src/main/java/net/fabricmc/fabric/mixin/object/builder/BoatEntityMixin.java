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

package net.fabricmc.fabric.mixin.object.builder;

import java.util.OptionalInt;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatType;

@Mixin(BoatEntity.class)
abstract class BoatEntityMixin extends Entity implements FabricBoatEntity {
	@Unique
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-object-builder-api-v1");

	// Hardcode the max tracked data id to avoid conflicts with other mods.
	@Unique
	private static final TrackedData<OptionalInt> FABRIC_BOAT_TYPE = TrackedDataHandlerRegistry.OPTIONAL_INT.create(254);

	BoatEntityMixin() {
		super(null, null);
	}

	@Override
	public @Nullable RegistryEntry<FabricBoatType> getFabricBoatType() {
		OptionalInt rawId = dataTracker.get(FABRIC_BOAT_TYPE);
		if (rawId.isEmpty()) return null;

		return getWorld()
				.getRegistryManager()
				.get(FabricBoatType.REGISTRY_KEY)
				.getEntry(rawId.getAsInt())
				.orElse(null);
	}

	@Override
	public void setFabricBoatType(@Nullable RegistryEntry<FabricBoatType> boatType) {
		OptionalInt rawId = OptionalInt.empty();

		if (boatType != null) {
			Registry<FabricBoatType> registry = getWorld()
					.getRegistryManager()
					.get(FabricBoatType.REGISTRY_KEY);

			if (boatType.ownerEquals(registry.getEntryOwner())) {
				// Fetch the raw ID
				int id = registry.getRawId(boatType.value());
				rawId = OptionalInt.of(id);
			} else {
				LOGGER.error("Fabric boat type {} is not from the boat type registry", boatType);
			}
		}

		dataTracker.set(FABRIC_BOAT_TYPE, rawId);
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	private void writeFabricTypeToNbt(NbtCompound nbt, CallbackInfo info) {
		RegistryEntry<FabricBoatType> boatType = getFabricBoatType();

		if (boatType != null) {
			nbt.putString("FabricType", boatType.getKey().orElseThrow().getValue().toString());
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	private void readFabricTypeFromNbt(NbtCompound nbt, CallbackInfo info) {
		if (nbt.contains("FabricType", NbtElement.STRING_TYPE)) {
			try {
				var id = new Identifier(nbt.getString("FabricType"));
				RegistryKey<FabricBoatType> key = RegistryKey.of(FabricBoatType.REGISTRY_KEY, id);
				Registry<FabricBoatType> registry = getWorld().getRegistryManager().get(FabricBoatType.REGISTRY_KEY);
				FabricBoatType type = registry.get(key);

				if (type != null) {
					dataTracker.set(FABRIC_BOAT_TYPE, OptionalInt.of(registry.getRawId(type)));
				} else {
					LOGGER.warn("Unknown Fabric boat type {}", id);
				}
			} catch (InvalidIdentifierException e) {
				LOGGER.error("Cannot load Fabric boat type from NBT: invalid id {}", nbt.getString("FabricType"), e);
			}
		}
	}

	@Inject(method = "initDataTracker", at = @At("RETURN"))
	private void onInitDataTracker(CallbackInfo info) {
		dataTracker.startTracking(FABRIC_BOAT_TYPE, OptionalInt.empty());
	}

	@ModifyArg(
			method = "fall",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/vehicle/BoatEntity;dropItem(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;",
					ordinal = 0
			),
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity$Type;getBaseBlock()Lnet/minecraft/block/Block;")),
			allow = 1
	)
	private ItemConvertible modifyPlanks(ItemConvertible original) {
		RegistryEntry<FabricBoatType> boatType = getFabricBoatType();

		if (boatType != null) {
			ItemConvertible planks = boatType.value().planks();

			if (planks != null) {
				return planks;
			}
		}

		return original;
	}

	@Inject(method = "asItem", at = @At("HEAD"), cancellable = true)
	private void replaceBoatItem(CallbackInfoReturnable<Item> info) {
		RegistryEntry<FabricBoatType> boatType = getFabricBoatType();

		if (boatType != null) {
			ItemConvertible boat = boatType.value().boat();

			if (boat != null) {
				info.setReturnValue(boat.asItem());
			}
		}
	}
}
