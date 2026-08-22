package com.aizistral.nochatrestrictions.core;

import java.util.Map;

import com.aizistral.nochatrestrictions.config.NCRConfig;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.minecraft.UserApiService.UserFlag;
import com.mojang.authlib.minecraft.UserApiService.UserProperties;

public final class WrappedUserProperties {

    private WrappedUserProperties() {
	throw new UnsupportedOperationException("Can't touch this");
    }

    public static UserProperties of(UserProperties properties) {
	NCRConfig config = NCRConfig.getInstance();
	ImmutableSet.Builder<UserFlag> flags = ImmutableSet.builder();

	flags.add(UserFlag.CHAT_ALLOWED); // always let the player access chat
	flags.add(UserFlag.SERVERS_ALLOWED); // always let the player open multiplayer menu
	flags.add(UserFlag.REALMS_ALLOWED); // always let the player open Realms menu
	flags.add(UserFlag.FRIENDS_ENABLED); // not sure if we need this, but let it be
	// flags.add(UserFlag.CHAT_FRIENDS_ONLY); // not adding this for obvious reasons

	addOptionalFlag(UserFlag.ACCEPT_FRIEND_INVITES, flags, properties); // I assume this is user-controller

	if (config.allowTelemetry()) { // weird flex but ok
	    addOptionalFlag(UserFlag.TELEMETRY_ENABLED, flags, properties);
	    addOptionalFlag(UserFlag.OPTIONAL_TELEMETRY_AVAILABLE, flags, properties);
	}

	if (config.allowProfanityFilter()) { // never seen anyone actually want this, but sure
	    addOptionalFlag(UserFlag.PROFANITY_FILTER_ENABLED, flags, properties);
	}

	return new UserProperties(flags.build(), Map.of());
    }

    private static void addOptionalFlag(UserFlag flag, ImmutableSet.Builder<UserFlag> builder, UserProperties properties) {
	if (properties.flag(flag)) {
	    builder.add(flag);
	}
    }

}
