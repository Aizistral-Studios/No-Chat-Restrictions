package com.aizistral.nochatrestrictions.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    // Removal of multiplayer/chat/telemetry restrictions is handled in
    // MixinYggdrasilUserApiService, so that it keeps working after in-game
    // account switchers (e.g. IAS) replace the UserApiService instance.

    @Inject(method = "isNameBanned", at = @At("HEAD"), cancellable = true)
    private void onCheckNameBan(CallbackInfoReturnable<Boolean> info) {
	info.setReturnValue(Boolean.FALSE);
    }

}
