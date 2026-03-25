package com.aizistral.nochatrestrictions.client;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

public class WrappedUserApiService implements UserApiService {
    private static final UserApiService.UserProperties FORCED_PROPERTIES;

    private final UserApiService service;

    static {
        ImmutableSet.Builder<UserApiService.UserFlag> flags = ImmutableSet.builder();
        flags.add(UserApiService.UserFlag.CHAT_ALLOWED);
        flags.add(UserApiService.UserFlag.SERVERS_ALLOWED);
        flags.add(UserApiService.UserFlag.REALMS_ALLOWED);
        FORCED_PROPERTIES = new UserApiService.UserProperties((Set)flags.build(), Map.of());
    }

    public WrappedUserApiService(UserApiService service) {
        this.service = service;
    }

    public UserApiService.UserProperties fetchProperties() {
        return FORCED_PROPERTIES;
    }

    public boolean isBlockedPlayer(UUID playerID) {
        return this.service.isBlockedPlayer(playerID);
    }

    public void refreshBlockList() {
        this.service.refreshBlockList();
    }

    public TelemetrySession newTelemetrySession(Executor executor) {
        return TelemetrySession.DISABLED;
    }

    public KeyPairResponse getKeyPair() {
        return this.service.getKeyPair();
    }

    public void reportAbuse(AbuseReportRequest request) {
        this.service.reportAbuse(request);
    }

    public boolean canSendReports() {
        return this.service.canSendReports();
    }

    public AbuseReportLimits getAbuseReportLimits() {
        return this.service.getAbuseReportLimits();
    }
}
