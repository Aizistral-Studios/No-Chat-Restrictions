package com.aizistral.nochatrestrictions.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;

import com.aizistral.nochatrestrictions.config.NCRConfig;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;

public class WrappedUserApiService implements UserApiService {
    private final UserApiService service;
    private @Nullable UserProperties properties = null;

    public WrappedUserApiService(UserApiService service) {
	this.service = service;
    }

    @Override
    public UserProperties fetchProperties() throws AuthenticationException {
	if (this.properties != null)
	    return this.properties;

	NCRConfig config = NCRConfig.getInstance();
	UserProperties properties = this.service.fetchProperties();
	ImmutableSet.Builder<UserFlag> flags = ImmutableSet.builder();

	flags.add(UserFlag.CHAT_ALLOWED); // always let the player access chat
	flags.add(UserFlag.SERVERS_ALLOWED); // always let the player open multiplayer menu
	flags.add(UserFlag.REALMS_ALLOWED); // always let the player open Realms menu
	flags.add(UserFlag.FRIENDS_ENABLED); // not sure if we need this, but let it be
	// flags.add(UserFlag.CHAT_FRIENDS_ONLY); // not adding this for obvious reasons

	this.addOptionalFlag(UserFlag.ACCEPT_FRIEND_INVITES, flags, properties); // I assume this is user-controller

	if (config.allowTelemetry()) { // weird flex but ok
	    this.addOptionalFlag(UserFlag.TELEMETRY_ENABLED, flags, properties);
	    this.addOptionalFlag(UserFlag.OPTIONAL_TELEMETRY_AVAILABLE, flags, properties);
	}

	if (config.allowProfanityFilter()) { // never seen anyone actually want this, but sure
	    this.addOptionalFlag(UserFlag.PROFANITY_FILTER_ENABLED, flags, properties);
	}

	return this.properties = new UserProperties(flags.build(), Map.of());
    }

    private void addOptionalFlag(UserFlag flag, ImmutableSet.Builder<UserFlag> builder, UserProperties properties) {
	if (properties.flag(flag)) {
	    builder.add(flag);
	}
    }

    @Override
    public boolean isBlockedPlayer(UUID playerID) {
	return this.service.isBlockedPlayer(playerID);
    }

    @Override
    public void refreshBlockList() {
	this.service.refreshBlockList();
    }

    @Override
    public TelemetrySession newTelemetrySession(Executor executor) {
	if (NCRConfig.getInstance().allowTelemetry())
	    return this.service.newTelemetrySession(executor);
	else
	    return TelemetrySession.DISABLED;
    }

    // Methods below primarily concern chat reporting. Not doing anything with them
    // here as that's out of scope for this mod, it's more of a No Chat Reports thing

    @Override
    public KeyPairResponse getKeyPair() {
	return this.service.getKeyPair();
    }

    @Override
    public void reportAbuse(AbuseReportRequest request) {
	this.service.reportAbuse(request);
    }

    @Override
    public boolean canSendReports() {
	return this.service.canSendReports();
    }

    @Override
    public AbuseReportLimits getAbuseReportLimits() {
	return this.service.getAbuseReportLimits();
    }

}
