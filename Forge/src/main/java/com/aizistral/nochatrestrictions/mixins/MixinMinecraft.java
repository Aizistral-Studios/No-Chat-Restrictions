package com.aizistral.nochatrestrictions.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.nochatrestrictions.core.NCRCore;
import com.aizistral.nochatrestrictions.core.WrappedSocialInteractionsService;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilSocialInteractionsService;

import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = { "func_244735_a", "createSocialInteractions" }, at = @At("RETURN"), cancellable = true)
    public void onCreateSocialInteractions(YggdrasilAuthenticationService authService, GameConfiguration gameConfig,
	    CallbackInfoReturnable<SocialInteractionsService> info) {
	SocialInteractionsService returnedService = info.getReturnValue();
	assert returnedService != null;
	info.setReturnValue(new WrappedSocialInteractionsService(returnedService));

	NCRCore.LOGGER.info("Successfully supplanted SocialInteractionsService with a wrapped version.");
    }

}
