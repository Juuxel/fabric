package net.fabricmc.fabric.test.object.builder.client;

import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.ChestBoatEntityModel;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.BoatRenderingRegistry;
import net.fabricmc.fabric.test.object.builder.BoatTest;

public final class BoatClientTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BoatRenderingRegistry.registerBoat(BoatTest.TEST_BOAT_KEY);
		EntityModelLayerRegistry.registerModelLayer(BoatRenderingRegistry.getModelLayer(BoatTest.TEST_BOAT_KEY, false),
				BoatEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(BoatRenderingRegistry.getModelLayer(BoatTest.TEST_BOAT_KEY, true),
				ChestBoatEntityModel::getTexturedModelData);
	}
}
