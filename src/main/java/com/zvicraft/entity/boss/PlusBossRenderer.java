package com.zvicraft.entity.boss;

import com.zvicraft.PlusSMP;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class PlusBossRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<PlusBossEntity, R> {
    public PlusBossRenderer(EntityRendererFactory.Context context, GeoModel renderState) {
        super(context, renderState);
    }

}

