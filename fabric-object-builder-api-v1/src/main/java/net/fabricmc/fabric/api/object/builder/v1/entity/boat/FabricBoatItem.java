package net.fabricmc.fabric.api.object.builder.v1.entity.boat;

import java.util.NoSuchElementException;

import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class FabricBoatItem extends BoatItem {
	private final RegistryKey<FabricBoatType> boatType;

	public FabricBoatItem(boolean chest, RegistryKey<FabricBoatType> boatType, Settings settings) {
		super(chest, BoatEntity.Type.OAK, settings);
		this.boatType = boatType;
	}

	public RegistryKey<FabricBoatType> getBoatType() {
		return boatType;
	}

	@Override
	protected BoatEntity createEntity(World world, HitResult hitResult) {
		BoatEntity boat = super.createEntity(world, hitResult);
		RegistryEntry<FabricBoatType> typeEntry = world.getRegistryManager()
				.get(FabricBoatType.REGISTRY_KEY)
				.getEntry(boatType)
				.orElseThrow(() -> new NoSuchElementException("Boat type not found: " + boatType));
		((FabricBoatEntity) boat).setFabricBoatType(typeEntry);
		return boat;
	}
}
