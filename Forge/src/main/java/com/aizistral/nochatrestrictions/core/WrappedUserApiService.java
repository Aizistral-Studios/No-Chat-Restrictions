package com.aizistral.nochatrestrictions.core;

import java.util.UUID;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;

import com.aizistral.nochatrestrictions.config.NCRConfig;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;

public class WrappedUserApiService implements UserApiService {
    private final UserApiService service;
    private @Nullable UserProperties properties = null;

    public WrappedUserApiService(UserApiService service) {
	this.service = service;
    }

    @Override
    public UserProperties properties() {
	if (this.properties != null)
	    return this.properties;
	else
	    return this.properties = WrappedUserProperties.of(this.service.properties());
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

}
