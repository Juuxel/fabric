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

package net.fabricmc.fabric.api.loot.v2;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;

import net.fabricmc.fabric.mixin.loot.LootPoolAccessor;

/**
 * Utility methods related to loot pools.
 */
public final class FabricLootPools {
	private FabricLootPools() {
	}

	public static LootTableRange getRolls(LootPool pool) {
		Objects.requireNonNull(pool, "pool cannot be null");
		return ((LootPoolAccessor) pool).getRolls();
	}

	public static List<LootPoolEntry> getEntries(LootPool pool) {
		Objects.requireNonNull(pool, "pool cannot be null");
		return ImmutableList.copyOf(((LootPoolAccessor) pool).getEntries());
	}

	public static List<LootCondition> getConditions(LootPool pool) {
		Objects.requireNonNull(pool, "pool cannot be null");
		return ImmutableList.copyOf(((LootPoolAccessor) pool).getConditions());
	}

	public static List<LootFunction> getFunctions(LootPool pool) {
		Objects.requireNonNull(pool, "pool cannot be null");
		return ImmutableList.copyOf(((LootPoolAccessor) pool).getFunctions());
	}
}
