package com.aizistral.nochatrestrictions.client.mixins;

import com.aizistral.nochatrestrictions.client.WrappedUserApiService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(
            method = "createUserApiService(Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;Lnet/minecraft/client/main/GameConfig;)Lcom/mojang/authlib/minecraft/UserApiService;",
            at = @At("RETURN"),
            cancellable = true
    )
    public void onCreateUserApiService(YggdrasilAuthenticationService authService,
                                       GameConfig gameConfig,
                                       CallbackInfoReturnable<UserApiService> info) {
        UserApiService returnedService = info.getReturnValue();
        if (returnedService != null) {
            info.setReturnValue(new WrappedUserApiService(returnedService));
        }
    }
}