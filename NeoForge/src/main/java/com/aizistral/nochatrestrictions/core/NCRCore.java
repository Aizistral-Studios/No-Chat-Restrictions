package com.aizistral.nochatrestrictions.core;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.minecraft.UserApiService.UserFlag;
import com.mojang.authlib.minecraft.UserApiService.UserProperties;

public class NCRCore {
    public static final Logger LOGGER = LogManager.getLogger("NoChatRestrictions");
    public static final UserProperties FORCED_USER_PROPERTIES;

    static {
	ImmutableSet.Builder<UserFlag> flags = ImmutableSet.builder();

	flags.add(UserFlag.CHAT_ALLOWED); // always let the player access chat
	flags.add(UserFlag.SERVERS_ALLOWED); // always let the player open multiplayer menu
	flags.add(UserFlag.REALMS_ALLOWED); // always let the player open Realms menu
	// flags.add(UserFlag.TELEMETRY_ENABLED); // not adding this for obvious reasons
	// flags.add(UserFlag.OPTIONAL_TELEMETRY_AVAILABLE); // thanks but no thanks
	// flags.add(UserFlag.PROFANITY_FILTER_ENABLED) // not adding this one either

	FORCED_USER_PROPERTIES = new UserProperties(flags.build(), Map.of());
    }

}
