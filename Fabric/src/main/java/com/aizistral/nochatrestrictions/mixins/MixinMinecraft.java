package com.aizistral.nochatrestrictions.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.nochatrestrictions.core.NCRCore;
import com.aizistral.nochatrestrictions.core.WrappedUserApiService;
import com.mojang.authlib.minecraft.UserApiService;
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

    @Inject(method = "isNameBanned", at = @At("HEAD"), cancellable = true)
    public void onCheckNameBan(CallbackInfoReturnable<Boolean> info) {
	info.setReturnValue(Boolean.FALSE);
    }

}
