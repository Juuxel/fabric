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

import java.util.NoSuchElementException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.dispenser.BoatDispenserBehavior;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatDispenserBehavior;
import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatType;

@Mixin(BoatDispenserBehavior.class)
abstract class BoatDispenserBehaviorMixin {
	@Inject(
			method = "dispenseSilently",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;setVariant(Lnet/minecraft/entity/vehicle/BoatEntity$Type;)V"),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void setFabricType(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> info, Direction direction, World world, BoatEntity boat) {
		if ((Object) this instanceof FabricBoatDispenserBehavior fabric) {
			RegistryKey<FabricBoatType> typeKey = fabric.getBoatType();
			RegistryEntry<FabricBoatType> entry = world.getRegistryManager()
					.get(FabricBoatType.REGISTRY_KEY)
					.getEntry(fabric.getBoatType())
					.orElseThrow(() -> new NoSuchElementException("Boat type not found: " + typeKey));
			boat.setFabricBoatType(entry);
		}
	}
}
