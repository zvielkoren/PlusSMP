package com.zvicraft.entity;

import com.zvicraft.PlusSMP;
import com.zvicraft.entity.boss.PlusBossEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModEntities {

    // יצירת RegistryKey תקין
    public static final RegistryKey<EntityType<?>> MY_ENTITY_KEY = RegistryKey.of(Registries.ENTITY_TYPE.getKey(), Identifier.of(PlusSMP.MOD_ID, "boss"));

    // יצירת ה-EntityType והרשמתו
    public static final EntityType<PlusBossEntity> BOSS = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(PlusSMP.MOD_ID, "boss"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, PlusBossEntity::new)
                    .entityFactory(PlusBossEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 2.0f))
                    .trackRangeBlocks(80)
                    .build(MY_ENTITY_KEY)
    );

    public static void init() {
        // אם צריך לקרוא הפעלה ראשונית – פה זה המקום
    }
}
