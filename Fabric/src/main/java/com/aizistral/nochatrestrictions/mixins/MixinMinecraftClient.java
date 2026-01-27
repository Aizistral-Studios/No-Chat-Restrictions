package com.aizistral.nochatrestrictions.mixins;



import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "createUserApiService", at = @At("RETURN"), cancellable = true)
    public void onCreateUserAPI(YggdrasilAuthenticationService authService, RunArgs runArgs, CallbackInfoReturnable<UserApiService> info) {
        UserApiService returnedService = info.getReturnValue();
        assert returnedService != null;
        //Missing statement as the wrapper has been replaced with a mixin, is this still needed?
        //NCRCore.LOGGER.info("Successfully supplanted UserApiService with a wrapped version.");
    }

}
