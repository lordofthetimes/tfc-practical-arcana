package net.lordofthetimes.tfcpracticalarcana.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.lordofthetimes.tfcpracticalarcana.TfcPracticalArcana;
import net.lordofthetimes.tfcpracticalarcana.Utils;
import net.lordofthetimes.tfcpracticalarcana.registry.ModFoodTraits;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class HeatSpell extends AbstractSpell {

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(TfcPracticalArcana.MODID, "heat");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(10)
            .build();

    public static final TagKey<Item> COMMON_FOODS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("c", "foods")
    );

    public static final TagKey<Item> WOODEN_RODS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("c", "rods/wooden")
    );



    public HeatSpell(){
        this.baseManaCost = 70;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
        return Utils.preCastHeatCapabilityHelper(level, caster) && super.checkPreCastConditions(level, spellLevel, caster, playerMagicData);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);

        if(!(caster instanceof Player player)) return;

        ItemStack stack = caster.getMainHandItem();
        final @Nullable IHeat heat = HeatCapability.get(stack);

        if(heat == null) return;

        if(stack.is(COMMON_FOODS) || stack.is(WOODEN_RODS)){
            heat.setTemperature(heat.getTemperature() + 200f);
        }
        else{
            HeatingRecipe recipe = HeatingRecipe.getRecipe(stack);

            if(recipe == null) return;

            float heatBy = recipe.getTemperature() / 5;

            heat.setTemperature(heat.getTemperature() + heatBy);
        }

        HeatingRecipe recipe = HeatingRecipe.getRecipe(stack);

        if(recipe == null || !recipe.isValidTemperature(heat.getTemperature())) return;

        ItemStack result = recipe.assembleItem(stack);

        result.setCount(stack.getCount());

        if(stack.is(COMMON_FOODS)){
            AddTraitModifier addTraitModifier = new AddTraitModifier(ModFoodTraits.MAGICALLY_RUINED);
            addTraitModifier.apply(result, null, null);
        }

        player.getInventory().removeItem(stack);
        player.getInventory().add(result);

    }
}
