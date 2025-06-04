package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HudModule extends Module {

    public Setting<Boolean> watermark = register(new Setting<>("Watermark", true));
    public Setting<Boolean> arrayList = register(new Setting<>("ArrayList", true));

    public HudModule() {
        super("Hud", "HUD Editor with ArrayList", Category.CLIENT, true, false, false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int y = 2;

        if (watermark.getValue()) {
            event.getContext().drawTextWithShadow(
                    mc.textRenderer,
                    OyVey.NAME + " " + OyVey.VERSION,
                    2, 2,
                    -1
            );
        }

        if (arrayList.getValue()) {
            int screenWidth = mc.getWindow().getScaledWidth();

            List<Module> enabledModules = OyVey.moduleManager.modules.stream()
                    .filter(Module::isEnabled)
                    .sorted(Comparator.comparingInt(m -> -mc.textRenderer.getWidth(m.getDisplayName())))
                    .collect(Collectors.toList());

            for (Module module : enabledModules) {
                String name = module.getDisplayName();
                int width = mc.textRenderer.getWidth(name);
                event.getContext().drawTextWithShadow(
                        mc.textRenderer,
                        name,
                        screenWidth - width - 2, 2,
                        -1
                );
                y += mc.textRenderer.fontHeight + 2;
            }
        }
    }
}