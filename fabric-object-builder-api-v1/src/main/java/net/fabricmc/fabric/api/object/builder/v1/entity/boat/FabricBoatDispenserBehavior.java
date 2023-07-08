package net.fabricmc.fabric.api.object.builder.v1.entity.boat;

import net.minecraft.block.dispenser.BoatDispenserBehavior;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.registry.RegistryKey;

public class FabricBoatDispenserBehavior extends BoatDispenserBehavior {
	private final RegistryKey<FabricBoatType> boatType;

	public FabricBoatDispenserBehavior(RegistryKey<FabricBoatType> boatType) {
		this(boatType, false);
	}

	public FabricBoatDispenserBehavior(RegistryKey<FabricBoatType> boatType, boolean chest) {
		super(BoatEntity.Type.OAK, chest);
		this.boatType = boatType;
	}

	public RegistryKey<FabricBoatType> getBoatType() {
		return boatType;
	}
}
