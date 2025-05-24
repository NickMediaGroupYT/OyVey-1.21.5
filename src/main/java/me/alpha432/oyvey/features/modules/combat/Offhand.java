package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.HoleUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class Offhand extends Module {

    public enum Modes {
        Totem,
        Crystal,
        Gapple
    }

    public Setting<Enum<?>> mode = register(new Setting<>("Mode", Modes.Totem));
    public Setting<Float> hp = register(new Setting<>("Health", 12.0f, 1.0f, 20.0f));
    public Setting<Float> fall = register(new Setting<>("Fall", 10.0f, 5.0f, 30.0f));
    public Setting<Boolean> swordGap = register(new Setting<>("SwordGap", false));
    public Setting<Boolean> halfInHole = register(new Setting<>("HalfInHole", true));

    public Offhand() {
        super("Offhand", "Automatically switch items to your offhand.", Category.COMBAT, true, false, false);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            onTick();
        });
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        float currentHp = mc.player.getHealth() + mc.player.getAbsorptionAmount();

        ItemStack item = mc.player.getMainHandStack();
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= this.hp.getValue().floatValue()
                || (mc.player.fallDistance >= (float)this.fall.getValue().intValue() && !mc.player.getAbilities().flying || mc.player.isUsingRiptide() || mc.player.getVelocity().y > 0 && mc.player.isGliding() && !HoleUtil.isInHole(mc.player))) {

            if (mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
                InventoryUtil.switchToOffhand(Items.TOTEM_OF_UNDYING);
            }
            if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= hp.getValue().floatValue() / 2 && HoleUtil.isInHole(mc.player) && halfInHole.getValue()) {
                InventoryUtil.switchToOffhand(Items.TOTEM_OF_UNDYING);
            }
        } else if (item.getItem().toString().toLowerCase().contains("sword") && this.swordGap.getValue() && mc.mouse.wasRightButtonClicked()) {
            if (mc.player.getOffHandStack().getItem() != Items.GOLDEN_APPLE) {
                InventoryUtil.switchToOffhand(Items.GOLDEN_APPLE);
            }
        } else if (this.mode.getValue().equals(Modes.Totem) && mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
            InventoryUtil.switchToOffhand(Items.TOTEM_OF_UNDYING);
        } else if (this.mode.getValue().equals(Modes.Crystal) && mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL) {
            InventoryUtil.switchToOffhand(Items.END_CRYSTAL);
        } else if (this.mode.getValue().equals(Modes.Gapple) && mc.player.getOffHandStack().getItem() != Items.GOLDEN_APPLE) {
            InventoryUtil.switchToOffhand(Items.GOLDEN_APPLE);
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        int totemSlot = InventoryUtil.findItemSlot(Items.TOTEM_OF_UNDYING);
        if (totemSlot != -1) {
            mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    totemSlot,
                    40,
                    SlotActionType.SWAP,
                    mc.player
            );
        }
    }

    @Override
    public String getDisplayInfo() {
        return mode.getValue().toString();
    }
}

