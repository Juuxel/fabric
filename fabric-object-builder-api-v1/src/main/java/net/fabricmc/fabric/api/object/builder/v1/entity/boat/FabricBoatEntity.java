package net.fabricmc.fabric.api.object.builder.v1.entity.boat;

import org.jetbrains.annotations.Nullable;

import net.minecraft.registry.entry.RegistryEntry;

// TODO: Inject
public interface FabricBoatEntity {
	@Nullable RegistryEntry<FabricBoatType> getFabricBoatType();
	void setFabricBoatType(@Nullable RegistryEntry<FabricBoatType> boatType);
}
