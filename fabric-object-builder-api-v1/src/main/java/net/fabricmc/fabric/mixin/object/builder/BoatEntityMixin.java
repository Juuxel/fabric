package net.fabricmc.fabric.mixin.object.builder;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatType;

@Mixin(BoatEntity.class)
abstract class BoatEntityMixin extends Entity implements FabricBoatEntity {
	@Unique
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-object-builder-api-v1");

	@Unique
	protected @Nullable RegistryEntry<FabricBoatType> fabricBoatType;

	BoatEntityMixin() {
		super(null, null);
	}

	@Override
	public @Nullable RegistryEntry<FabricBoatType> getFabricBoatType() {
		return fabricBoatType;
	}

	@Override
	public void setFabricBoatType(@Nullable RegistryEntry<FabricBoatType> boatType) {
		fabricBoatType = boatType;
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	private void writeFabricTypeToNbt(NbtCompound nbt) {
		if (fabricBoatType != null) {
			nbt.putString("FabricType", fabricBoatType.getKey().orElseThrow().getValue().toString());
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	private void readFabricTypeFromNbt(NbtCompound nbt) {
		if (nbt.contains("FabricType", NbtElement.STRING_TYPE)) {
			try {
				var id = new Identifier(nbt.getString("FabricType"));
				RegistryKey<FabricBoatType> key = RegistryKey.of(FabricBoatType.REGISTRY_KEY, id);
				Registry<FabricBoatType> registry = getWorld().getRegistryManager().get(FabricBoatType.REGISTRY_KEY);
				RegistryEntry<FabricBoatType> type = registry.getEntry(key).orElse(null);

				if (type != null) {
					this.fabricBoatType = type;
				} else {
					LOGGER.warn("Unknown Fabric boat type");
				}
			} catch (InvalidIdentifierException e) {
				LOGGER.error("Cannot load Fabric boat type from NBT: invalid id {}", nbt.getString("FabricType"), e);
			}
		}
	}

	@ModifyArg(
			method = "fall",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/vehicle/BoatEntity;dropItem(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;",
					ordinal = 0
			),
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity$Type;getBaseBlock()Lnet/minecraft/block/Block;")),
			allow = 1
	)
	private ItemConvertible modifyPlanks(ItemConvertible original) {
		if (fabricBoatType != null) {
			ItemConvertible planks = fabricBoatType.value().planks();

			if (planks != null) {
				return planks;
			}
		}

		return original;
	}

	@Inject(method = "asItem", at = @At("HEAD"), cancellable = true)
	private void replaceBoatItem(CallbackInfoReturnable<Item> info) {
		if (fabricBoatType != null) {
			ItemConvertible boat = fabricBoatType.value().boat();

			if (boat != null) {
				info.setReturnValue(boat.asItem());
			}
		}
	}
}
