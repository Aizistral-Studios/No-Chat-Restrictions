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
	else
	    return this.properties = WrappedUserProperties.of(this.service.fetchProperties());
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
