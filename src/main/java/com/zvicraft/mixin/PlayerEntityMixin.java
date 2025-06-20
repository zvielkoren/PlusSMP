package com.zvicraft.mixin;

import com.zvicraft.PlusSMP;
import com.zvicraft.item.ModItems;
import com.zvicraft.power.PowerSystem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.ItemEntity;
import net.minecraft.text.Text;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onPlayerDeath(DamageSource source, CallbackInfo ci) {
        if (!((Object)this instanceof ServerPlayerEntity player)) return;
        
        // Get the player's power data
        PowerSystem.PowerData powerData = PowerSystem.getPlayerPower(player);
        if (powerData == null || powerData.powerType == null) return;
        
        // Create power drop item
        ItemStack powerDrop = new ItemStack(ModItems.getPowerItem(powerData.powerType));
        if (!powerDrop.isEmpty()) {
            // Drop the item at player's position with a small offset
            Vec3d pos = player.getPos();
            player.getServerWorld().spawnEntity(
                new ItemEntity(
                    player.getWorld(),
                    pos.x,
                    pos.y + 0.5,
                    pos.z,
                    powerDrop
                )
            );
            
            // Clear the player's power
            PowerSystem.clearPlayerPower(player);
            player.sendMessage(Text.literal("You've lost your " + powerData.powerType.getName() + "!"));
        }
    }
}
