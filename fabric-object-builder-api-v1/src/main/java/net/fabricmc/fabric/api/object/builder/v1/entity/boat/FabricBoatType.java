package net.fabricmc.fabric.api.object.builder.v1.entity.boat;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public record FabricBoatType(
		@Nullable ItemConvertible planks,
		@Nullable ItemConvertible boat,
		@Nullable ItemConvertible chestBoat
) {
	public static final RegistryKey<Registry<FabricBoatType>> REGISTRY_KEY =
			RegistryKey.ofRegistry(new Identifier("fabric", "boat_type"));

	public static final Codec<FabricBoatType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registries.ITEM.getCodec().optionalFieldOf("planks").forGetter(type -> asOptional(type.planks)),
			Registries.ITEM.getCodec().optionalFieldOf("boat").forGetter(type -> asOptional(type.boat)),
			Registries.ITEM.getCodec().optionalFieldOf("chest_boat").forGetter(type -> asOptional(type.chestBoat))
	).apply(instance, (planks, boat, chestBoat) -> new FabricBoatType(planks.orElse(null), boat.orElse(null), chestBoat.orElse(null))));
	public static final Codec<FabricBoatType> NETWORK_CODEC = Codec.unit(() -> new FabricBoatType(null, null, null));

	private static Optional<Item> asOptional(@Nullable ItemConvertible item) {
		return Optional.ofNullable(item).map(ItemConvertible::asItem);
	}
}
