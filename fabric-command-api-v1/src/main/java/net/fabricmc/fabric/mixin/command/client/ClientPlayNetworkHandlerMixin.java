package net.fabricmc.fabric.mixin.command.client;

import com.mojang.brigadier.CommandDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.server.command.CommandSource;

import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;

@Mixin(ClientPlayNetworkHandler.class)
abstract class ClientPlayNetworkHandlerMixin {
	@Shadow
	private CommandDispatcher<CommandSource> commandDispatcher;

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Inject(method = "onCommandTree", at = @At("RETURN"))
	private void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo info) {
		// Add the `/` commands to the vanilla dispatcher for completion.
		// It's done separately because both the server and the client commands have
		// to be in the same dispatcher and completion results.
		ClientCommandInternals.addCommands('/', (CommandDispatcher) commandDispatcher);
	}
}
