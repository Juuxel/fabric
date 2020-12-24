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

/**
 * Accessor methods for loot table properties.
 * This interface is automatically implemented on all {@link LootTable} instances.
 */
public interface FabricLootTable {
	/**
	 * Gets this loot table as its vanilla type.
	 *
	 * @return the vanilla loot table
	 */
	default LootTable asVanilla() {
		return (LootTable) this;
	}

	/**
	 * Gets an unmodifiable list of this table's loot pools.
	 *
	 * @return the loot pools
	 */
	List<LootPool> getPools();

	/**
	 * Gets an unmodifiable list of this table's functions.
	 *
	 * @return the functions
	 */
	List<LootFunction> getFunctions();

	LootContextType getType();
}
