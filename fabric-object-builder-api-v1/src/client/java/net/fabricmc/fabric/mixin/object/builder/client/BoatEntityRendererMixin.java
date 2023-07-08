package net.fabricmc.fabric.mixin.object.builder.client;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.ChestBoatEntityModel;
import net.minecraft.client.render.entity.model.ChestRaftEntityModel;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.RaftEntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.object.builder.v1.client.BoatRenderingRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.boat.FabricBoatType;
import net.fabricmc.fabric.impl.object.builder.v1.client.BoatRenderingRegistryImpl;

@Mixin(BoatEntityRenderer.class)
abstract class BoatEntityRendererMixin {
	@Unique
	private Map<RegistryKey<FabricBoatType>, Pair<Identifier, CompositeEntityModel<BoatEntity>>> fabricTexturesAndModels;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(EntityRendererFactory.Context ctx, boolean chest, CallbackInfo info) {
		ImmutableMap.Builder<RegistryKey<FabricBoatType>, Pair<Identifier, CompositeEntityModel<BoatEntity>>> builder =
				ImmutableMap.builder();

		for (BoatRenderingRegistryImpl.Settings settings : BoatRenderingRegistryImpl.ALL_SETTINGS.values()) {
			if (settings.chest() != chest) continue;

			Identifier id = settings.type().getValue();
			String textureDir = chest ? "chest_boat/" : "boat/";
			var texture = new Identifier(id.getNamespace(), "textures/entity/" + textureDir + id.getPath() + ".png");

			EntityModelLayer modelLayer = BoatRenderingRegistry.getModelLayer(settings.type(), chest);
			ModelPart modelPart = ctx.getPart(modelLayer);
			CompositeEntityModel<BoatEntity> model;

			if (settings.raft()) {
				model = chest ? new ChestRaftEntityModel(modelPart) : new RaftEntityModel(modelPart);
			} else {
				model = chest ? new ChestBoatEntityModel(modelPart) : new BoatEntityModel(modelPart);
			}

			builder.put(settings.type(), Pair.of(texture, model));
		}

		fabricTexturesAndModels = builder.buildOrThrow();
	}

	@ModifyVariable(
			method = "render(Lnet/minecraft/entity/vehicle/BoatEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At("STORE"),
			allow = 1
	)
	private Pair<?, ?> replaceTextureAndModel(Pair<?, ?> original, BoatEntity boat) {
		RegistryEntry<FabricBoatType> boatType = boat.getFabricBoatType();

		if (boatType != null) {
			RegistryKey<FabricBoatType> key = boatType.getKey().orElseThrow();
			return fabricTexturesAndModels.get(key);
		}

		return original;
	}

	@Inject(method = "getTexture(Lnet/minecraft/entity/vehicle/BoatEntity;)Lnet/minecraft/util/Identifier;", at = @At("HEAD"), cancellable = true)
	private void onGetTexture(BoatEntity boat, CallbackInfoReturnable<Identifier> info) {
		RegistryEntry<FabricBoatType> boatType = boat.getFabricBoatType();

		if (boatType != null) {
			RegistryKey<FabricBoatType> key = boatType.getKey().orElseThrow();
			info.setReturnValue(fabricTexturesAndModels.get(key).getFirst());
		}
	}
}
