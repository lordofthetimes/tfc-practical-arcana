package net.lordofthetimes.tfcpracticalarcana.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.lordofthetimes.tfcpracticalarcana.TfcPracticalArcana;
import net.lordofthetimes.tfcpracticalarcana.Utils;
import net.lordofthetimes.tfcpracticalarcana.data.BlockEntityCastData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.Optional;

import static net.dries007.tfc.common.fluids.TFCFluids.SALT_WATER;

public class BrineWellSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(TfcPracticalArcana.MODID, "brinewell");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(60)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.tfc_practical_arcana.fluid_amount", getFillAmount(spellLevel, caster))
        );
    }

    public BrineWellSpell(){
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 100;
        this.baseManaCost = 150;
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
        return CastType.LONG;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.EVOKER_PREPARE_ATTACK);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastBarrelHelper(level, entity, playerMagicData, new FluidStack(SALT_WATER.getSource(),1), 8)
                && super.checkPreCastConditions(level, spellLevel, entity, playerMagicData);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {


        if(playerMagicData.getAdditionalCastData() instanceof  BlockEntityCastData blockEntityCastData){
            BlockEntity blockEntity = blockEntityCastData.getTarget((ServerLevel) level);

            if(blockEntity instanceof BarrelBlockEntity barrel){
                barrel.getInventory().fill(
                        new FluidStack(SALT_WATER.getSource(), getFillAmount(spellLevel, caster)),
                        IFluidHandler.FluidAction.EXECUTE
                );
            }
        }

        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    public int getFillAmount(int spellLevel, LivingEntity caster){
        return (int) (getSpellPower(spellLevel, caster) * 1000);
    }
}
