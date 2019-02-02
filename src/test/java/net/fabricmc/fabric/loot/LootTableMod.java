package net.fabricmc.fabric.loot;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.events.LootTableLoadingCallback;
import net.minecraft.item.Items;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.world.loot.entry.ItemEntry;

import java.util.Arrays;

public class LootTableMod implements ModInitializer {
	@Override
	public void onInitialize() {
		LootTableLoadingCallback.REGISTRY.register((id, supplier) -> {
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
}
