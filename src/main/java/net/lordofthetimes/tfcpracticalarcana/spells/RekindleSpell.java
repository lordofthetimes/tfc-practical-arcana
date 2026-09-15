package net.lordofthetimes.tfcpracticalarcana.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.*;
import net.lordofthetimes.tfcpracticalarcana.TfcPracticalArcana;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class RekindleSpell extends AbstractSpell {

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(TfcPracticalArcana.MODID, "rekindle");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(2 * 60)
            .build();

    public RekindleSpell(){
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 3;
        this.spellPowerPerLevel = 1;
        this.castTime = 20;
        this.baseManaCost = 100;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.radius", getRadius(spellLevel, caster))
        );
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
    public void onServerPreCast(Level level, int spellLevel, LivingEntity caster, @Nullable MagicData playerMagicData) {
        super.onServerPreCast(level, spellLevel, caster, playerMagicData);
        if (playerMagicData == null)
            return;
        TargetedAreaEntity targetedAreaEntity = TargetedAreaEntity.createTargetAreaEntity(level, caster.position(), (float) getRadius(spellLevel,caster), 16007990, caster);
        playerMagicData.setAdditionalCastData(new TargetAreaCastData(caster.position(), targetedAreaEntity));
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        double radius = getRadius(spellLevel, caster);

        BlockPos center = BlockPos.containing(caster.position());

        int scanRadius = Mth.ceil(radius);
        double radiusSquared = radius * radius;

        BlockPos.betweenClosedStream(
                        center.offset(-scanRadius, -scanRadius, -scanRadius),
                        center.offset(scanRadius, scanRadius, scanRadius)
                )
                .filter(pos -> pos.distSqr(center) <= radiusSquared)
                .forEach(pos -> {
                    Block block = level.getBlockState(pos).getBlock();
                    BlockState state = level.getBlockState(pos);

                    if(block instanceof TorchBlock){
                        level.setBlockAndUpdate(pos, TFCBlocks.TORCH.get().withPropertiesOf(state));
                        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
                    }

                    if(block instanceof WallTorchBlock){
                        level.setBlockAndUpdate(pos, TFCBlocks.WALL_TORCH.get().withPropertiesOf(state));
                        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
                    }

                    if (block instanceof CandleBlock){
                        level.setBlockAndUpdate(pos, state.setValue(CandleBlock.LIT, true));
                        TFCBlocks.CANDLE.get().setPlacedBy(level,pos,state,caster,ItemStack.EMPTY);
                        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
                    }

                    if (block instanceof CandleCakeBlock){
                        level.setBlockAndUpdate(pos, state.setValue(TFCCandleCakeBlock.LIT, true));
                        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
                    }


                });


        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    public double getRadius(int spellLevel, LivingEntity caster){
        return (double) Math.floor(getSpellPower(spellLevel, caster) * 4);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_RAISED_HAND;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.TOUCH_GROUND_ANIMATION;
    }

    @Override
    public boolean stopSoundOnCancel() {
        return true;
    }
}
