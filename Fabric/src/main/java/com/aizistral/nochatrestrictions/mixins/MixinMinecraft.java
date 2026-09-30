package com.aizistral.nochatrestrictions.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.nochatrestrictions.core.NCRCore;
import com.aizistral.nochatrestrictions.core.WrappedUserApiService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.UserApiService.UserProperties;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = { "m_193585_", "createUserApiService" }, at = @At("RETURN"), cancellable = true)
    public void onCreateUserApi(YggdrasilAuthenticationService authService, GameConfig gameConfig,
	    CallbackInfoReturnable<UserApiService> info) {
	UserApiService returnedService = info.getReturnValue();
	assert returnedService != null;
	info.setReturnValue(new WrappedUserApiService(returnedService));

	NCRCore.LOGGER.info("Successfully supplanted UserApiService with a wrapped version.");
    }

    // Account switchers fetch the properties themselves and put the result straight into
    // userPropertiesFuture, so wrapping the service on its own doesn't always stick.
    // Everything that gates chat/multiplayer/realms reads them back through here.
    @Inject(method = "userProperties", at = @At("RETURN"), cancellable = true)
    public void onGetUserProperties(CallbackInfoReturnable<UserProperties> info) {
	info.setReturnValue(NCRCore.FORCED_USER_PROPERTIES);
    }

    @Inject(method = "isNameBanned", at = @At("HEAD"), cancellable = true)
    public void onCheckNameBan(CallbackInfoReturnable<Boolean> info) {
	info.setReturnValue(Boolean.FALSE);
    }

}
