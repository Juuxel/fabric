package net.fabricmc.fabric.test.object.builder;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatItem;
import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatType;

public final class BoatTest implements ModInitializer {
	private static final String NAMESPACE = "fabric-object-builder-api-v1-testmod";
	public static final RegistryKey<FabricBoatType> TEST_BOAT_KEY =
			RegistryKey.of(FabricBoatType.REGISTRY_KEY, new Identifier(NAMESPACE, "test"));
	public static final Item TEST_BOAT_ITEM = new FabricBoatItem(false, TEST_BOAT_KEY, new Item.Settings());
	public static final Item TEST_CHEST_BOAT_ITEM = new FabricBoatItem(true, TEST_BOAT_KEY, new Item.Settings());

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier(NAMESPACE, "test_boat"), TEST_BOAT_ITEM);
		Registry.register(Registries.ITEM, new Identifier(NAMESPACE, "test_chest_boat"), TEST_CHEST_BOAT_ITEM);
	}
}
