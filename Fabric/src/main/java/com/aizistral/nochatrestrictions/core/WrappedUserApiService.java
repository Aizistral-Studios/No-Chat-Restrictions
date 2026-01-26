package com.aizistral.nochatrestrictions.core;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;

import java.util.UUID;
import java.util.concurrent.Executor;

public class WrappedUserApiService implements UserApiService  {

    private final UserApiService service;

    public WrappedUserApiService(UserApiService service) {
        if (service == null) {
            throw new NullPointerException("'service' argument cannot be null!");
        }
        this.service = service;
    }


    @Override
    public UserProperties properties() {
        return null;
    }

    @Override
    public boolean isBlockedPlayer(UUID uuid) {
        return false;
    }

    @Override
    public void refreshBlockList() {

    }

    @Override
    public TelemetrySession newTelemetrySession(Executor executor) {
        return null;
    }

    @Override
    public KeyPairResponse getKeyPair() {
        return null;
    }

    @Override
    public void reportAbuse(AbuseReportRequest abuseReportRequest) {

    }

    @Override
    public boolean canSendReports() {
        return false;
    }

    @Override
    public AbuseReportLimits getAbuseReportLimits() {
        return null;
    }

    //Boolean expressions replaced with flags. should be force set to diable any chat restrictions
    public enum UserFlag {
        SERVERS_ALLOWED,
        REALMS_ALLOWED,
        CHAT_ALLOWED,
        TELEMETRY_ENABLED,
        PROFANITY_FILTER_ENABLED,
        OPTIONAL_TELEMETRY_AVAILABLE
    }

}
