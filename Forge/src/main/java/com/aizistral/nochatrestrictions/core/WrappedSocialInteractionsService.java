package com.aizistral.nochatrestrictions.core;

import java.util.UUID;
import com.mojang.authlib.minecraft.SocialInteractionsService;

public class WrappedSocialInteractionsService implements SocialInteractionsService {
    private final SocialInteractionsService service;

    public WrappedSocialInteractionsService(SocialInteractionsService service) {
	if (service == null)
	    throw new NullPointerException("'service' argument cannot be null!");

	this.service = service;
    }

    @Override
    public boolean serversAllowed() {
	return true;
    }

    @Override
    public boolean realmsAllowed() {
	return true;
    }

    @Override
    public boolean chatAllowed() {
	return true;
    }

    @Override
    public boolean isBlockedPlayer(UUID playerID) {
	return this.service.isBlockedPlayer(playerID);
    }

}
