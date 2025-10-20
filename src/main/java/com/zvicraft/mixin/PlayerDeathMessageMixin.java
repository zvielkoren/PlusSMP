package com.zvicraft.mixin;

import com.zvicraft.effect.PowerEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerDeathMessageMixin {
    private void onGetDeathMessage(CallbackInfoReturnable<Text> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        DamageSource damageSource = (DamageSource) player.getDamageTracker().getDeathMessage();
        
        if (damageSource != null && damageSource.getAttacker() instanceof ServerPlayerEntity attacker) {
            // Check if the attacker has the Invis power active
            if (PowerEffects.isInvisActive(attacker)) {
                // Obfuscate the death message
                Text obfuscatedMessage = Text.translatable("death.attack.player.invis", 
                    player.getDisplayName(), 
                    Text.literal(""));
                cir.setReturnValue(obfuscatedMessage);
            }
        }
    }
}
