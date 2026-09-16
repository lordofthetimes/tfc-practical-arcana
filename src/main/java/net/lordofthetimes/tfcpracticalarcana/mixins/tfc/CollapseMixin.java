package net.lordofthetimes.tfcpracticalarcana.mixins.tfc;

import net.dries007.tfc.ForgeEventHandler;
import net.lordofthetimes.tfcpracticalarcana.registry.ModEffects;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeEventHandler.class)
public class CollapseMixin {

    @Inject(method = "onBlockBroken", at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/common/recipes/CollapseRecipe;tryTriggerCollapse(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"), cancellable = true)
    private static void checkEffect(BlockEvent.BreakEvent event, CallbackInfo ci) {
        if(event.getPlayer().hasEffect(ModEffects.ARCANE_SUPPORT_EFFECT)) ci.cancel();
    }

}
