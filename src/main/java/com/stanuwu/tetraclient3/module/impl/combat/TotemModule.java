package com.stanuwu.tetraclient3.module.impl.combat;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.PreTickEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Random;

public class TotemModule extends AbstractModule {
    public TotemModule() {
        super(ModuleCategory.COMBAT, "Totem");
    }

    private enum TotemModes {
        LEGIT,
        INSTANT
    }

    public final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));
    private final EnumValue<TotemModes> mode = reg(new EnumValue<>("Mode", TotemModes.LEGIT, TotemModes.class));

    int delay = 0;


    @EventSubscriber(event = PreTickEvent.class)
    private void doPreTick(PreTickEvent event) {
        if (!enabled.getValue()) return;
        LocalPlayer player = event.getData().player;
        MultiPlayerGameMode gameMode = event.getData().gameMode;
        if (player == null || gameMode == null) return;

        if (player.getOffhandItem().isEmpty()) {
            if (delay > 0) {
                delay--;
                return;
            }
            if (mode.getValue().equals(TotemModes.LEGIT))
                delay = new Random(System.currentTimeMillis()).nextInt(3, 10);

            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);

                if (!stack.isEmpty() && stack.is(Items.TOTEM_OF_UNDYING)) {
                    if (mode.getValue().equals(TotemModes.LEGIT)) {
                        event.getData().setScreen(new InventoryScreen(player));
                    }
                    int slot = i < 9 ? i + 36 : i;
                    gameMode.handleContainerInput(player.inventoryMenu.containerId, slot, 40, ContainerInput.SWAP, player);
                    if (mode.getValue().equals(TotemModes.LEGIT)) {
                        player.closeContainer();
                        player.setSprinting(false);
                    }
                    break;
                }
            }

        }
    }
}
