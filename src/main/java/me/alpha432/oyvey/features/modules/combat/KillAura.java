package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class KillAura extends Module {

    public Setting<Double> range = register(new Setting<>("Range", 4.5, 1.0, 6.0));
    public Setting<Boolean> playersOnly = register(new Setting<>("PlayersOnly", true));
    public Setting<Boolean> swing = register(new Setting<>("Swing", true));

    public KillAura() {
        super("KillAura", "Attacks nearby entities with a sword", Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        // Must be holding a sword
        Item mainItem = mc.player.getMainHandStack().getItem();
        if (mainItem != Items.NETHERITE_SWORD && mainItem != Items.DIAMOND_SWORD &&
                mainItem != Items.IRON_SWORD && mainItem != Items.STONE_SWORD &&
                mainItem != Items.GOLDEN_SWORD && mainItem != Items.WOODEN_SWORD) return;

        Entity target = null;
        double minDistance = range.getValue();

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player || !(entity instanceof LivingEntity)) continue;
            if (playersOnly.getValue() && !(entity instanceof PlayerEntity)) continue;
            if (entity.isInvisible()) continue;

            if (entity instanceof PlayerEntity && isFriend((PlayerEntity) entity)) continue;

            double distance = mc.player.distanceTo(entity);
            if (distance < minDistance) {
                minDistance = distance;
                target = entity;
            }
        }

        if (target != null && mc.player.getAttackCooldownProgress(0) >= 1.0f) {
            mc.interactionManager.attackEntity(mc.player, target);
            if (swing.getValue()) {
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    private boolean isFriend(PlayerEntity player) {
        return OyVey.friendManager.isFriend(player);
    }
}
