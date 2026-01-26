package com.aizistral.nochatrestrictions;

import com.aizistral.nochatrestrictions.core.NCRCore;

import net.fabricmc.api.ModInitializer;

public class NoChatRestrictions implements ModInitializer {

    @Override
    public void onInitialize() {
	NCRCore.LOGGER.info("NoChatRestrictions Fabric mod initialized!");
    }

}