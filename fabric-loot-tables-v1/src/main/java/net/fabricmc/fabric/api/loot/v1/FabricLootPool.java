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
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;

/**
 * Accessor methods for loot pool properties.
 * This interface is automatically implemented on all {@link LootPool} instances.
 */
public interface FabricLootPool {
	/**
	 * Gets this loot pool as its vanilla type.
	 *
	 * @return the vanilla loot pool
	 */
	default LootPool asVanilla() {
		return (LootPool) this;
	}

	/**
	 * Gets an unmodifiable list of this pool's entries.
	 *
	 * @return the entries
	 */
	List<LootPoolEntry> getEntries();

	/**
	 * Gets an unmodifiable list of this pool's conditions.
	 *
	 * @return the conditions
	 */
	List<LootCondition> getConditions();

	/**
	 * Gets an unmodifiable list of this pool's functions.
	 *
	 * @return the functions
	 */
	List<LootFunction> getFunctions();

	LootTableRange getRolls();
}
