package de.rasmusantons.playertracker.server.mixin;

import de.rasmusantons.playertracker.Utils;
import de.rasmusantons.playertracker.server.extension.ServerPlayerExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraft.core.component.DataComponents.LORE;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "use", at = @At("HEAD"))
    private void onUse(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (player instanceof ServerPlayer serverPlayer && Utils.isPlayerTracker(itemStack)) {
            ServerPlayer currentlyTracking = ((ServerPlayerExtension) serverPlayer).playertracker$getTrackedPlayer();
            List<ServerPlayer> players = new ArrayList<>(Objects.requireNonNull(level.getServer()).getPlayerList().getPlayers());
            players.remove(player);
            Component message, lore;
            if (!players.isEmpty()) {
                int currentlyTrackingIdx = players.indexOf(currentlyTracking);
                int nowTrackingIdx = (currentlyTrackingIdx + 1) % players.size();
                ServerPlayer nowTracking = players.get(nowTrackingIdx);
                ((ServerPlayerExtension) serverPlayer).playertracker$setTrackedPlayer(nowTracking);
                String nowTrackingName = Objects.requireNonNull(nowTracking.getDisplayName()).getString();
                message = Component.translatable("playertracker.action.now_tracking", nowTrackingName).withStyle(ChatFormatting.GOLD);
                lore = Component.translatable("playertracker.lore.tracking", nowTrackingName).withStyle(ChatFormatting.GOLD);
            } else {
                message = Component.translatable("playertracker.action.no_player").withStyle(ChatFormatting.GOLD);
                lore = Component.translatable("playertracker.lore.not_tracking").withStyle(ChatFormatting.GOLD);
            }
            itemStack.set(LORE, new ItemLore(List.of(lore)));
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(message));
        }
    }
}
