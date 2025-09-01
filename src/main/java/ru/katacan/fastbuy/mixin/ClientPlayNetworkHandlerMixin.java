package ru.katacan.fastbuy.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.katacan.fastbuy.FastBuy;
import ru.katacan.fastbuy.command.Command;

import static ru.katacan.fastbuy.FastBuy.boostCount;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Unique
    private static void sendPacketToServer(Packet<?> packet) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.networkHandler.sendPacket(packet);
        }
    }

    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    public void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (FastBuy.volume > 10) {
            FastBuy.volume = 10;
        }
        if (FastBuy.windowOneInt != -1) {
            ci.cancel();
            FastBuy.windowDonate = packet.getName();
            sendPacketToServer(new ClickSlotC2SPacket(packet.getSyncId(), 0, 8, 0, SlotActionType.PICKUP, ItemStack.EMPTY,
                    Int2ObjectMaps.emptyMap()));
            FastBuy.windowOneInt = -1;
            FastBuy.boostType = Command.boostType;
        } else if (!(packet.getName().equals(FastBuy.windowDonate)) && FastBuy.boostType != -1) {
            FastBuy.windowDonate = null;
            ci.cancel();
            FastBuy.windowTwo = packet.getName();
            FastBuy.windowAllBoosters = packet.getName();
            sendPacketToServer(new ClickSlotC2SPacket(packet.getSyncId(), 0, FastBuy.boostType - 1, 0, SlotActionType.PICKUP, ItemStack.EMPTY,
                    Int2ObjectMaps.emptyMap()));
            FastBuy.boostType = -1;

        } else if (!(packet.getName().equals(FastBuy.windowAllBoosters)) && FastBuy.volume != -1) {
            if (boostCount != FastBuy.volume) {
                sendPacketToServer(new ClickSlotC2SPacket(packet.getSyncId(), 0, 4, 0, SlotActionType.PICKUP, ItemStack.EMPTY,
                        Int2ObjectMaps.emptyMap()));
            } else {
                FastBuy.windowAllBoosters = null;
                FastBuy.windowBuy = packet.getName();
                FastBuy.volume = -1;
                boostCount = 1;
            }
        } else if ((packet.getName().equals(FastBuy.windowBuy)) || (packet.getName().equals(FastBuy.windowTwo) && FastBuy.done != -1)) {
            ci.cancel();
            sendPacketToServer(new ClickSlotC2SPacket(packet.getSyncId(), 0, 1, 0, SlotActionType.PICKUP, ItemStack.EMPTY,
                    Int2ObjectMaps.emptyMap()));
            FastBuy.done = 1;
            if (packet.getName().equals(FastBuy.windowTwo) && FastBuy.done != -1) {
                ci.cancel();
                FastBuy.windowTwo = null;
                mc.execute(() -> {
                    if (mc.currentScreen != null) {
                        mc.currentScreen.close();
                    }
                });
                FastBuy.done = -1;
            }
        }
    }

    @Inject(method = "onInventory", at = @At("HEAD"))
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen != null && FastBuy.volume != -1) {
            boostCount = packet.getContents().get(4).getCount();
        }
    }
}
