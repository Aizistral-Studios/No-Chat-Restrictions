package com.aizistral.nochatrestrictions.mixins;

import java.util.Map;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.nochatrestrictions.core.NCRCore;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService.UserFlag;
import com.mojang.authlib.minecraft.UserApiService.UserProperties;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;

/**
 * Applies the chat/multiplayer restriction removal directly on the concrete
 * service class instead of just wrapping it once at {@code Minecraft#createUserApiService}.
 *
 * In-game account switchers (e.g. IAS) build a brand new {@link YggdrasilUserApiService}
 * and swap it onto the Minecraft instance without going through the original wrapping
 * path, which is why restrictions used to come back after switching accounts. By hooking
 * the service class itself, every instance the game ever uses returns permissive
 * properties, regardless of who created it or when.
 */
@Mixin(value = YggdrasilUserApiService.class, remap = false)
public class MixinYggdrasilUserApiService {

    static {
	NCRCore.LOGGER.info("MixinYggdrasilUserApiService initialized succesfully.");
    }

    @Inject(method = "fetchProperties", at = @At("RETURN"), cancellable = true)
    private void onFetchProperties(CallbackInfoReturnable<UserProperties> info) {
	info.setReturnValue(NCRCore.FORCED_USER_PROPERTIES);
    }

    @Inject(method = "newTelemetrySession", at = @At("HEAD"), cancellable = true)
    private void onNewTelemetrySession(Executor executor, CallbackInfoReturnable<TelemetrySession> info) {
	info.setReturnValue(TelemetrySession.DISABLED);
    }

}
