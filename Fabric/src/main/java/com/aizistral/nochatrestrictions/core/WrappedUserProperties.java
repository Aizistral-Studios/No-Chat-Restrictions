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

	flags.add(UserFlag.CHAT_ALLOWED);
	flags.add(UserFlag.SERVERS_ALLOWED);
	flags.add(UserFlag.REALMS_ALLOWED);

	if (config.allowTelemetry()) {
	    addOptionalFlag(UserFlag.TELEMETRY_ENABLED, flags, properties);
	    addOptionalFlag(UserFlag.OPTIONAL_TELEMETRY_AVAILABLE, flags, properties);
	}

	if (config.allowProfanityFilter()) {
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
