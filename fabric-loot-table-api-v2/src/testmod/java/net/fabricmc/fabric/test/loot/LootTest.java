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

package net.fabricmc.fabric.test.loot;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.entry.ItemEntry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableLoadingCallback;

public class LootTest implements ModInitializer {
	@Override
	public void onInitialize() {
		// Test loot table load event
		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, tableBuilder, setter) -> {
			if (Blocks.WHITE_WOOL.getLootTableId().equals(id)) {
				LootPool pool = FabricLootPoolBuilder.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.GOLD_INGOT).build())
						.conditionally(SurvivesExplosionLootCondition.builder().build())
						.build();

				tableBuilder.pool(pool);
			}
		});
	}
}
