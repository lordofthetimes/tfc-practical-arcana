package net.lordofthetimes.tfcpracticalarcana.data;

import io.redspace.ironsspellbooks.api.spells.ICastData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class BlockEntityCastData implements ICastData {

    private final BlockPos pos;

    public BlockEntityCastData(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void reset() {

    }

    @Nullable
    public BlockEntity getTarget(ServerLevel level){
        return level.getBlockEntity(pos);
    }

    public BlockPos getTargetPosition(){
        return pos;
    }
}
