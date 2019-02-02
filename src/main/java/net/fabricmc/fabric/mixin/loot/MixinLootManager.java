/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.loot;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import net.fabricmc.fabric.api.events.LootTableLoadingEvent;
import net.fabricmc.fabric.api.loot.FabricLootSupplier;
import net.fabricmc.fabric.impl.loot.LootPoolAdder;
import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.item.Items;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootManager;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.world.loot.entry.ItemEntry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@Mixin(LootManager.class)
public class MixinLootManager {
	@Shadow @Final public static int jsonLength;
	@Shadow @Final private static Logger LOGGER;
	@Shadow @Final private static Gson gson;
	@Shadow @Final private Map<Identifier, LootSupplier> suppliers;
	private static final String PATH = "loot_pools";
	private static final int PATH_LENGTH = PATH.length() + 1; // Includes the slash as well

	/**
	 * A map of loot pools adders with the keys being the targets.
	 */
	private final Multimap<Identifier, LootPoolAdder> lootPoolAdders = MultimapBuilder.hashKeys().hashSetValues().build();

	// TODO: Move this to a test mod
	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(CallbackInfo ci) {
		LootTableLoadingEvent.REGISTRY.register((id, supplier) -> {
			if ("minecraft:blocks/dirt".equals(id.toString())) {
				LootPool[] pools = Arrays.copyOf(supplier.getPools(), supplier.getPools().length + 1);
				pools[pools.length - 1] = LootPool.create()
						.withEntry(ItemEntry.builder(Items.FEATHER))
						.withRolls(ConstantLootTableRange.create(1))
						.withCondition(SurvivesExplosionLootCondition.method_871())
						.build();

				supplier.setPools(pools);
			}
		});
	}

	@Inject(method = "onResourceReload", at = @At("HEAD"))
	private void loadAdders(ResourceManager manager, CallbackInfo info) {
		manager.findResources(PATH, (name) -> name.endsWith(".json")).forEach(fullId -> {
			String originalPath = fullId.getPath();
			Identifier id = new Identifier(fullId.getNamespace(), originalPath.substring(PATH_LENGTH, originalPath.length() - jsonLength));
			try (Resource resource = manager.getResource(fullId)) {
				LootPoolAdder adder = JsonHelper.deserialize(gson, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), LootPoolAdder.class);
				if (adder != null) {
					if (adder.targets.size() == 0) {
						LOGGER.warn("[Fabric] Loot pool adder {} has no targets", id);
					}

					for (String target : adder.targets) {
						lootPoolAdders.put(new Identifier(target), adder);
					}
				}
			} catch (Throwable t) {
				LOGGER.error("[Fabric] Couldn't read loot pool adder {} from {}", id, fullId, t);
			}
		});
	}

	@Inject(method = "onResourceReload", at = @At("RETURN"))
	private void injectAdders(ResourceManager manager, CallbackInfo info) {
		suppliers.forEach((id, supplier) -> {
			ArrayList<LootPool> pools = new ArrayList<>();
			for (LootPoolAdder adder : lootPoolAdders.get(id)) {
				pools.addAll(adder.pools);
			}

			fabric_addPools(supplier, pools.toArray(new LootPool[0]));
			LootTableLoadingEvent[] handlers = ((HandlerArray<LootTableLoadingEvent>) LootTableLoadingEvent.REGISTRY)
					.getBackingArray();

			for (LootTableLoadingEvent handler : handlers) {
				handler.accept(id, (FabricLootSupplier) supplier);
			}
		});
	}

	private static void fabric_addPools(LootSupplier supplier, LootPool[] pools) {
		FabricLootSupplier extendedSupplier = (FabricLootSupplier) supplier;
		LootPool[] oldPools = extendedSupplier.getPools();
		LootPool[] newPools = new LootPool[oldPools.length + pools.length];
		System.arraycopy(oldPools, 0, newPools, 0, oldPools.length);
		System.arraycopy(pools, 0, newPools, oldPools.length, pools.length);
		extendedSupplier.setPools(newPools);
	}
}
