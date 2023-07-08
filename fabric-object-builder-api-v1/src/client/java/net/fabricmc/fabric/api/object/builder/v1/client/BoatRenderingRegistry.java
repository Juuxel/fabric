package net.fabricmc.fabric.api.object.builder.v1.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatType;
import net.fabricmc.fabric.impl.object.builder.v1.client.BoatRenderingRegistryImpl;

public final class BoatRenderingRegistry {
	public static EntityModelLayer getModelLayer(RegistryKey<FabricBoatType> type, boolean chest) {
		Identifier id = type.getValue();
		String boatKind = chest ? "chest_boat/" : "boat/";
		return new EntityModelLayer(new Identifier(id.getNamespace(), boatKind + id.getPath()), "main");
	}

	public static void registerBoat(RegistryKey<FabricBoatType> type) {
		registerBoat(type, false);
		registerBoat(type, true);
	}

	public static void registerBoat(RegistryKey<FabricBoatType> type, boolean chest) {
		BoatRenderingRegistryImpl.registerBoat(type, chest);
	}

	public static void registerRaft(RegistryKey<FabricBoatType> type) {
		registerRaft(type, false);
		registerRaft(type, true);
	}

	public static void registerRaft(RegistryKey<FabricBoatType> type, boolean chest) {
		BoatRenderingRegistryImpl.registerRaft(type, chest);
	}
}
