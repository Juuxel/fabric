package net.fabricmc.fabric.impl.object.builder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatType;

public final class ObjectBuilderInit implements ModInitializer {
	@Override
	public void onInitialize() {
		DynamicRegistries.registerSynced(FabricBoatType.REGISTRY_KEY, FabricBoatType.CODEC, FabricBoatType.NETWORK_CODEC);
	}
}
