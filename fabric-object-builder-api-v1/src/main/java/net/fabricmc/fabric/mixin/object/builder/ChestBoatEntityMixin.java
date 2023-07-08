package net.fabricmc.fabric.mixin.object.builder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

@Mixin(ChestBoatEntity.class)
abstract class ChestBoatEntityMixin extends BoatEntityMixin {
	@Inject(method = "asItem", at = @At("HEAD"), cancellable = true)
	private void replaceChestBoatItem(CallbackInfoReturnable<Item> info) {
		if (fabricBoatType != null) {
			ItemConvertible chestBoat = fabricBoatType.value().chestBoat();

			if (chestBoat != null) {
				info.setReturnValue(chestBoat.asItem());
			}
		}
	}
}
