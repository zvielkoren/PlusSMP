package com.zvicraft.entity.boss;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PlusBossEntity extends AnimalEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.plusboss.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.plusboss.walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("animation.plusboss.attack");
    private boolean handSwinging;

    public PlusBossEntity(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createBossAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 500.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ATTACK_DAMAGE, 20.0)
                .add(EntityAttributes.FOLLOW_RANGE, 50.0)
                .add(EntityAttributes.ARMOR, 10.0);
    }



    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    @Override
    public double getBoneResetTime() {
        return getBoneResetTime();
    }

    @Override
    public boolean shouldPlayAnimsWhileGamePaused() {
        return shouldPlayAnimsWhileGamePaused();
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }

    @Override
    public AnimatableInstanceCache animatableCacheOverride() {
        return this.cache;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
}
