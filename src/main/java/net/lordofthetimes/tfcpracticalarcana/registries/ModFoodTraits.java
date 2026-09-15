package net.lordofthetimes.tfcpracticalarcana.registries;

import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.lordofthetimes.tfcpracticalarcana.TfcPracticalArcana;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModFoodTraits {

    public static final DeferredRegister<FoodTrait> TRAITS = DeferredRegister.create(FoodTraits.KEY, TfcPracticalArcana.MODID);

    public static final DeferredHolder<FoodTrait, FoodTrait> MAGICALLY_PRESERVED = register("magically_preserved", () -> 0.7d);

    private static DeferredHolder<FoodTrait, FoodTrait> register(String name, Supplier<Double> decayModifier)
    {
        return TRAITS.register(name, () -> new FoodTrait(decayModifier, "tooltip.tfc_practical_arcana.food_trait." + name));
    }
}
