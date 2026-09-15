package net.lordofthetimes.tfcpracticalarcana;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.RaycastBuilder;
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.lordofthetimes.tfcpracticalarcana.data.BlockEntityCastData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;

public class Utils {

    public static boolean preCastBarrelHelper(Level level, LivingEntity caster, MagicData playerMagicData, FluidStack fillFluid, int range){
        var target = RaycastBuilder.begin(level, caster)
                .range(range)
                .checkForBlocks(true)
                .build();


        if( caster instanceof ServerPlayer serverPlayer){
            if(!(target instanceof BlockHitResult blockHitResult)){
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("ui.tfc_practical_arcana.cast_error_target_block_entity_barrel")
                                .withStyle(ChatFormatting.RED)
                ));
                return false;
            }
            BlockEntity blockEntity = level.getBlockEntity(blockHitResult.getBlockPos());

            if(!(blockEntity instanceof BarrelBlockEntity barrelBlockEntity)){
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("ui.tfc_practical_arcana.cast_error_target_block_entity_barrel")
                                .withStyle(ChatFormatting.RED)
                ));
                return false;
            }

            if(barrelBlockEntity.getBlockState().getValue(BarrelBlock.SEALED)){
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("ui.tfc_practical_arcana.cast_error_target_barrel_sealed")
                                .withStyle(ChatFormatting.RED)
                ));
                return false;
            }
            FluidStack fluid = barrelBlockEntity.getInventory().getFluidInTank(0);

            if(!(fluid.is(fillFluid.getFluid()) || fluid.isEmpty())){
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("ui.tfc_practical_arcana.cast_error_target_barrel_another_fluid",
                                        fillFluid.getHoverName().getString()
                                )
                                .withStyle(ChatFormatting.RED)
                ));
                return false;
            }

            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("ui.tfc_practical_arcana.spell_target_success_fill_barrel",
                            fillFluid.getHoverName().getString()
                    ).withStyle(ChatFormatting.GREEN)

            ));

            playerMagicData.setAdditionalCastData(new BlockEntityCastData(blockEntity.getBlockPos()));
            return true;
        }

        return false;
    }


}
