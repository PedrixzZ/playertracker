package de.rasmusantons.playertracker.common.mixin;

import de.rasmusantons.playertracker.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemCost.class)
public class ItemCostMixin {
    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    private void test(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (Utils.isPlayerTracker(stack))
            cir.setReturnValue(false);
    }
}
