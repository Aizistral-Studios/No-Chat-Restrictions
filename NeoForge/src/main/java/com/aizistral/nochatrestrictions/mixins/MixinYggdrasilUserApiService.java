package com.aizistral.nochatrestrictions.mixins;

import java.util.Map;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.nochatrestrictions.config.NCRConfig;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService.UserFlag;
import com.mojang.authlib.minecraft.UserApiService.UserProperties;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;

/**
 * Applies the chat/multiplayer restriction removal directly on the concrete
 * service class instead of wrapping it once at {@code Minecraft.createUserApiService}.
 *
 * In-game account switchers (e.g. IAS) build a brand new {@link YggdrasilUserApiService}
 * and swap it onto the Minecraft instance without going through the original wrapping
 * path, which is why restrictions used to come back after switching accounts. By hooking
 * the service class itself, every instance the game ever uses returns permissive
 * properties, regardless of who created it or when.
 */
@Mixin(value = YggdrasilUserApiService.class, remap = false)
public class MixinYggdrasilUserApiService {

    @Inject(method = "fetchProperties", at = @At("RETURN"), cancellable = true)
    private void onFetchProperties(CallbackInfoReturnable<UserProperties> info) {
	UserProperties original = info.getReturnValue();

	if (original == null)
	    return;

	NCRConfig config = NCRConfig.getInstance();
	ImmutableSet.Builder<UserFlag> flags = ImmutableSet.builder();

	flags.add(UserFlag.CHAT_ALLOWED); // always let the player access chat
	flags.add(UserFlag.SERVERS_ALLOWED); // always let the player open multiplayer menu
	flags.add(UserFlag.REALMS_ALLOWED); // always let the player open Realms menu
	flags.add(UserFlag.FRIENDS_ENABLED); // not sure if we need this, but let it be
	// flags.add(UserFlag.CHAT_FRIENDS_ONLY); // not adding this for obvious reasons

	this.addOptionalFlag(UserFlag.ACCEPT_FRIEND_INVITES, flags, original); // I assume this is user-controlled

	if (config.allowTelemetry()) { // weird flex but ok
	    this.addOptionalFlag(UserFlag.TELEMETRY_ENABLED, flags, original);
	    this.addOptionalFlag(UserFlag.OPTIONAL_TELEMETRY_AVAILABLE, flags, original);
	}

	if (config.allowProfanityFilter()) { // never seen anyone actually want this, but sure
	    this.addOptionalFlag(UserFlag.PROFANITY_FILTER_ENABLED, flags, original);
	}

	info.setReturnValue(new UserProperties(flags.build(), Map.of()));
    }

    @Inject(method = "newTelemetrySession", at = @At("HEAD"), cancellable = true)
    private void onNewTelemetrySession(Executor executor, CallbackInfoReturnable<TelemetrySession> info) {
	if (!NCRConfig.getInstance().allowTelemetry()) {
	    info.setReturnValue(TelemetrySession.DISABLED);
	}
    }

    @Unique
    private void addOptionalFlag(UserFlag flag, ImmutableSet.Builder<UserFlag> builder, UserProperties properties) {
	if (properties.flag(flag)) {
	    builder.add(flag);
	}
    }

}
