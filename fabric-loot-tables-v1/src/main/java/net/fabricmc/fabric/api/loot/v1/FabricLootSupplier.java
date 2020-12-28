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

package net.fabricmc.fabric.api.loot.v1;

import java.util.List;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;

import net.fabricmc.fabric.api.loot.v2.FabricLootTables;

/**
 * An interface implemented by all {@link LootTable} instances when
 * Fabric API is present. Contains accessors for various fields.
 *
 * @deprecated Replaced with {@link FabricLootTables}.
 */
@Deprecated
public interface FabricLootSupplier {
	default LootTable asVanilla() {
		return (LootTable) this;
	}

	default List<LootPool> getPools() {
		return FabricLootTables.getPools(asVanilla());
	}
	default List<LootFunction> getFunctions() {
		return FabricLootTables.getFunctions(asVanilla());
	}
	default LootContextType getType() {
		return asVanilla().getType(); // Vanilla has this now
	}
}
