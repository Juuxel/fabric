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

package net.fabricmc.fabric.api.loot;

import net.fabricmc.fabric.impl.loot.LootSupplierBuilderHooks;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.function.LootFunction;

import java.util.Collection;

public class FabricLootSupplierBuilder extends LootSupplier.Builder {
	private final LootSupplierBuilderHooks extended = (LootSupplierBuilderHooks) this;

	private FabricLootSupplierBuilder() {}

	private FabricLootSupplierBuilder(LootSupplier supplier) {
		FabricLootSupplier extendedSupplier = (FabricLootSupplier) supplier;
		extended.fabric_getPools().addAll(extendedSupplier.getPools());
		extended.fabric_getFunctions().addAll(extendedSupplier.getFunctions());
		withType(extendedSupplier.getType());
	}

	@Override
	public FabricLootSupplierBuilder withPool(LootPool.Builder pool) {
		super.withPool(pool);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder withType(LootContextType type) {
		super.withType(type);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder withFunction(LootFunction.Builder function) {
		super.withFunction(function);
		return this;
	}

	public FabricLootSupplierBuilder withPool(LootPool pool) {
		extended.fabric_getPools().add(pool);
		return this;
	}

	public FabricLootSupplierBuilder withFunction(LootFunction function) {
		extended.fabric_getFunctions().add(function);
		return this;
	}

	public FabricLootSupplierBuilder withPools(Collection<LootPool> pools) {
		pools.forEach(this::withPool);
		return this;
	}

	public FabricLootSupplierBuilder withFunctions(Collection<LootFunction> functions) {
		functions.forEach(this::withFunction);
		return this;
	}

	public static FabricLootSupplierBuilder builder() {
		return new FabricLootSupplierBuilder();
	}

	public static FabricLootSupplierBuilder of(LootSupplier supplier) {
		return new FabricLootSupplierBuilder(supplier);
	}
}
