//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "E:\desktop\Minecraft-Deobfuscator3000-1.2.3\Minecraft-Deobfuscator3000-1.2.3\1.12 stable mappings"!

// 
// Decompiled by Procyon v0.5.36
// 

package me.alpha432.oyvey.features.modules.combat;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemEgg;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import me.alpha432.oyvey.features.command.Command;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumHand;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.features.modules.Module;

public class BowMcBomb extends Module
{
    private boolean shooting;
    private long lastShootTime;
    public Setting<Boolean> Bows;
    public Setting<Boolean> pearls;
    public Setting<Boolean> eggs;
    public Setting<Boolean> snowballs;
    public Setting<Integer> Timeout;
    public Setting<Integer> spoofs;
    public Setting<Boolean> bypass;
    public Setting<Boolean> debug;
    
    public BowMcBomb() {
        super("BowMcBomb", "Uno hitter w bows", Category.MISC, true, false, false);
        this.Bows = (Setting<Boolean>)this.register(new Setting("Bows", (T)true));
        this.pearls = (Setting<Boolean>)this.register(new Setting("Pearls", (T)true));
        this.eggs = (Setting<Boolean>)this.register(new Setting("Eggs", (T)true));
        this.snowballs = (Setting<Boolean>)this.register(new Setting("SnowBallz", (T)true));
        this.Timeout = (Setting<Integer>)this.register(new Setting("Timeout", (T)0, (T)100, (T)20000));
        this.spoofs = (Setting<Integer>)this.register(new Setting("Spoofs", (T)15, (T)1, (T)300));
        this.bypass = (Setting<Boolean>)this.register(new Setting("Bypass", (T)false));
        this.debug = (Setting<Boolean>)this.register(new Setting("Debug", (T)false));
    }
    
    @Override
    public void onEnable() {
        if (this.isEnabled()) {
            this.shooting = false;
            this.lastShootTime = System.currentTimeMillis();
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getStage() != 0) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            final CPacketPlayerDigging packet = event.getPacket();
            if (packet.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                final ItemStack handStack = BowMcBomb.mc.player.getHeldItem(EnumHand.MAIN_HAND);
                if (!handStack.isEmpty() && handStack.getItem() != null && handStack.getItem() instanceof ItemBow && this.Bows.getValue()) {
                    this.doSpoofs();
                    if (this.debug.getValue()) {
                        Command.sendMessage("trying to spoof");
                    }
                }
            }
        }
        else if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
            final CPacketPlayerTryUseItem packet2 = event.getPacket();
            if (packet2.getHand() == EnumHand.MAIN_HAND) {
                final ItemStack handStack = BowMcBomb.mc.player.getHeldItem(EnumHand.MAIN_HAND);
                if (!handStack.isEmpty() && handStack.getItem() != null) {
                    if (handStack.getItem() instanceof ItemEgg && this.eggs.getValue()) {
                        this.doSpoofs();
                    }
                    else if (handStack.getItem() instanceof ItemEnderPearl && this.pearls.getValue()) {
                        this.doSpoofs();
                    }
                    else if (handStack.getItem() instanceof ItemSnowball && this.snowballs.getValue()) {
                        this.doSpoofs();
                    }
                }
            }
        }
    }
    
    private void doSpoofs() {
        if (System.currentTimeMillis() - this.lastShootTime >= this.Timeout.getValue()) {
            this.shooting = true;
            this.lastShootTime = System.currentTimeMillis();
            BowMcBomb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BowMcBomb.mc.player, CPacketEntityAction.Action.START_SPRINTING));
            for (int index = 0; index < this.spoofs.getValue(); ++index) {
                if (this.bypass.getValue()) {
                    BowMcBomb.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BowMcBomb.mc.player.posX, BowMcBomb.mc.player.posY + 1.0E-10, BowMcBomb.mc.player.posZ, false));
                    BowMcBomb.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BowMcBomb.mc.player.posX, BowMcBomb.mc.player.posY - 1.0E-10, BowMcBomb.mc.player.posZ, true));
                }
                else {
                    BowMcBomb.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BowMcBomb.mc.player.posX, BowMcBomb.mc.player.posY - 1.0E-10, BowMcBomb.mc.player.posZ, true));
                    BowMcBomb.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BowMcBomb.mc.player.posX, BowMcBomb.mc.player.posY + 1.0E-10, BowMcBomb.mc.player.posZ, false));
                }
            }
            if (this.debug.getValue()) {
                Command.sendMessage("Spoofed");
            }
            this.shooting = false;
        }
    }
}
