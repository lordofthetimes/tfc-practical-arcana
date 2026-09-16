package net.lordofthetimes.tfcpracticalarcana.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.*;
import net.lordofthetimes.tfcpracticalarcana.TfcPracticalArcana;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RevitalizeSpell extends AbstractSpell {

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(TfcPracticalArcana.MODID, "revitalize");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(20)
            .build();

    public RevitalizeSpell(){
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 20;
        this.baseManaCost = 50;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.tfc_practical_arcana.chance_crop", Math.floor(spellLevel * 0.2 * 100)),
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
        TargetedAreaEntity targetedAreaEntity = TargetedAreaEntity.createTargetAreaEntity(level, caster.position(), (float) getRadius(spellLevel,caster), 0X00ff00, caster);
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

                    if(block instanceof WildCropBlock cropBlock){
                        int roll = level.random.nextInt(5) + 1;

                        if (roll > spellLevel || state.getValue(WildCropBlock.MATURE)) return;

                        level.setBlockAndUpdate(pos, state.cycle(WildCropBlock.MATURE));
                    }

                    if((block instanceof DeadCropBlock deadCrop)) {
                        int roll = level.random.nextInt(5) + 1;

                        if (roll > spellLevel) return;

                        String description = deadCrop.getDescriptionId();

                        String cropString = Arrays.stream(description.split("\\.")).toList().getLast().toUpperCase();

                        Crop crop = Crop.valueOf(cropString);

                        CropBlock cropBlock = (CropBlock) TFCBlocks.CROPS.get(crop).get();
                        BlockState newState = cropBlock.defaultBlockState();

                        if(state.getValue(DeadCropBlock.MATURE)){
                            if (cropBlock instanceof DoubleCropBlock doubleCropBlock) {

                                BlockState bottomState = doubleCropBlock.defaultBlockState()
                                        .setValue(DoubleCropBlock.PART, DoubleCropBlock.Part.BOTTOM)
                                        .setValue(doubleCropBlock.getAgeProperty(),
                                                doubleCropBlock.getMaxAge());

                                BlockState topState = doubleCropBlock.defaultBlockState()
                                        .setValue(DoubleCropBlock.PART, DoubleCropBlock.Part.TOP)
                                        .setValue(doubleCropBlock.getAgeProperty(),
                                                doubleCropBlock.getMaxAge());

                                int flags = Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS;

                                // Put both states in place without running the normal shape update.
                                level.setBlock(pos.above(), topState, flags);
                                level.setBlock(pos, bottomState, flags);

                                // Now that both halves exist, notify neighbors.
                                level.updateNeighborsAt(pos, doubleCropBlock);
                                level.updateNeighborsAt(pos.above(), doubleCropBlock);
                            }

                            //TODO: FIX THE FLOODEDCROPBLOCK REVITALIZATION FOR MATURE CROP SO IT DOESN'T INSTANTLY BREAK
                            if (cropBlock instanceof FloodedCropBlock floodedCropBlock){
                                newState = floodedCropBlock.defaultBlockState()
                                        .setValue(floodedCropBlock.getAgeProperty(),
                                                floodedCropBlock.getMaxAge())
                                        .setValue(FloodedCropBlock.FLUID, state.getValue(FloodedCropBlock.FLUID));

                                int flags = Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS;

                                level.setBlock(pos, newState, flags);
                            }

                            if(cropBlock instanceof ClimbingCropBlock climbingCropBlock) {

                                BlockState bottomState = climbingCropBlock.defaultBlockState()
                                        .setValue(ClimbingCropBlock.PART, ClimbingCropBlock.Part.BOTTOM)
                                        .setValue(climbingCropBlock.getAgeProperty(),
                                                climbingCropBlock.getMaxAge());

                                BlockState topState = climbingCropBlock.defaultBlockState()
                                        .setValue(ClimbingCropBlock.PART, ClimbingCropBlock.Part.TOP)
                                        .setValue(climbingCropBlock.getAgeProperty(),
                                                climbingCropBlock.getMaxAge());

                                int flags = Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS;

                                // Put both states in place without running the normal shape update.
                                level.setBlock(pos.above(), topState, flags);
                                level.setBlock(pos, bottomState, flags);

                                // Now that both halves exist, notify neighbors.
                                level.updateNeighborsAt(pos, climbingCropBlock);
                                level.updateNeighborsAt(pos.above(), climbingCropBlock);

                                level.setBlockAndUpdate(pos, climbingCropBlock.defaultBlockState()
                                        .setValue(climbingCropBlock.getAgeProperty(), climbingCropBlock.getMaxAge())
                                        .setValue(ClimbingCropBlock.STICK, true)
                                );
                            }

                            if(cropBlock instanceof DefaultCropBlock defaultCropBlock){
                                level.setBlockAndUpdate(pos, defaultCropBlock.defaultBlockState().setValue(defaultCropBlock.getAgeProperty(), defaultCropBlock.getMaxAge()));
                            }

                            if(level.getBlockEntity(pos) instanceof CropBlockEntity cropBlockEntity){
                                cropBlockEntity.setYield(0.2f);
                                cropBlockEntity.setGrowth(1f);
                                System.out.println("i");
                                int i = 1;
                            }

                        }
                        else{

                            if (cropBlock instanceof FloodedCropBlock floodedCropBlock){
                                newState = floodedCropBlock.defaultBlockState()
                                        .setValue(FloodedCropBlock.FLUID, state.getValue(FloodedCropBlock.FLUID));

                                level.setBlockAndUpdate(pos,newState);
                            }

                            level.setBlockAndUpdate(pos, newState);
                        }
                    }
                });


        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    public double getRadius(int spellLevel, LivingEntity caster){
        return 2 + Math.floor(getSpellPower(spellLevel,caster));
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
