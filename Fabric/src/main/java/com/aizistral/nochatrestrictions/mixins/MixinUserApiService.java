package com.aizistral.nochatrestrictions.mixins;


import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

//Because of Mojang's more restrictive apis on Authlib, instead of wrapping, We have to inject the flags using Mixins
@Mixin(value = YggdrasilUserApiService.class, remap = false)
public class MixinUserApiService {

    @Unique
    private static final UserApiService.UserProperties FORCE_PROPERTIES =
            new UserApiService.UserProperties(
                    Set.of(
                            UserApiService.UserFlag.CHAT_ALLOWED,
                            UserApiService.UserFlag.SERVERS_ALLOWED,
                            UserApiService.UserFlag.REALMS_ALLOWED), Map.of()
            );

    @Inject(method = "properties", at = @At("HEAD"), cancellable = true)
    private void forceProperties(CallbackInfoReturnable<UserApiService.UserProperties> cir) {
        cir.setReturnValue(FORCE_PROPERTIES);
    }




}
