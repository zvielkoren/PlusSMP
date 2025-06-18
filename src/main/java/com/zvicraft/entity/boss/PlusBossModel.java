package com.zvicraft.entity.boss;

import com.zvicraft.PlusSMP;
import com.zvicraft.animation.AnimationSystem;
import net.minecraft.util.Identifier;

public class PlusBossModel extends AnimationSystem {
    @Override
    public Identifier getModelResource(PlusBossEntity entity) {
        return Identifier.of(PlusSMP.MOD_ID, "geo/plusboss.geo.json");
    }

    @Override
    public Identifier getTextureResource(PlusBossEntity entity) {
        return Identifier.of(PlusSMP.MOD_ID, "textures/entity/plusboss.png");
    }

    @Override
    public Identifier getAnimationResource(PlusBossEntity entity) {
        return Identifier.of(PlusSMP.MOD_ID, "animations/plusboss.animation.json");
    }
}
