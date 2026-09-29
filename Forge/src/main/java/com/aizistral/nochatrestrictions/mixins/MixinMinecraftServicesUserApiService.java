package com.aizistral.nochatrestrictions.mixins;

import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.nochatrestrictions.config.NCRConfig;
import com.aizistral.nochatrestrictions.core.NCRCore;
import com.aizistral.nochatrestrictions.core.WrappedUserProperties;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService.UserProperties;
import com.mojang.authlib.services.MinecraftServicesUserApiService;

/**
 * Applies the chat/multiplayer restriction removal directly on the concrete
 * service class instead of just wrapping it once at {@code Minecraft#createUserApiService}.
 *
 * In-game account switchers (e.g. IAS) build a brand new {@link MinecraftServicesUserApiService}
 * and swap it onto the Minecraft instance without going through the original wrapping
 * path, which is why restrictions used to come back after switching accounts. By hooking
 * the service class itself, every instance the game ever uses returns permissive
 * properties, regardless of who created it or when.
 */
@Mixin(value = MinecraftServicesUserApiService.class, remap = false)
public class MixinMinecraftServicesUserApiService {

    static {
	NCRCore.LOGGER.info("MixinMinecraftServicesUserApiService initialized successfully.");
    }

    private @Unique @Nullable UserProperties wrappedProperties;

    @Inject(method = "fetchProperties", at = @At("RETURN"), cancellable = true)
    private void onFetchProperties(CallbackInfoReturnable<UserProperties> info) {
	UserProperties original = info.getReturnValue();

	if (original == null)
	    return;
	else if (this.wrappedProperties != null) {
	    info.setReturnValue(this.wrappedProperties);
	    return;
	} else {
	    info.setReturnValue(this.wrappedProperties = WrappedUserProperties.of(original));
	    return;
	}
    }

    @Inject(method = "newTelemetrySession", at = @At("HEAD"), cancellable = true)
    private void onNewTelemetrySession(Executor executor, CallbackInfoReturnable<TelemetrySession> info) {
	if (!NCRConfig.getInstance().allowTelemetry()) {
	    info.setReturnValue(TelemetrySession.DISABLED);
	}
    }

}
