package net.lordofthetimes.tfcpracticalarcana.registry;

import net.lordofthetimes.tfcpracticalarcana.TfcPracticalArcana;
import net.lordofthetimes.tfcpracticalarcana.effect.ArcaneSupportEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, TfcPracticalArcana.MODID);

    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }

    public static final Holder<MobEffect> ARCANE_SUPPORT_EFFECT = MOB_EFFECTS.register("arcane_support",
            () -> new ArcaneSupportEffect(MobEffectCategory.BENEFICIAL, 0x2196F3));

}
