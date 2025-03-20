package meteordevelopment.meteorclient.gui.tabs.builtin;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.screens.HudEditorScreen;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.class_2487;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class HudTab extends Tab {
   public HudTab() {
      super("HUD");
   }

   public TabScreen createScreen(GuiTheme theme) {
      return new HudTab.HudScreen(theme, this);
   }

   public boolean isScreen(class_437 screen) {
      return screen instanceof HudTab.HudScreen;
   }

   public static class HudScreen extends WindowTabScreen {
      private final Hud hud = Hud.get();

      public HudScreen(GuiTheme theme, Tab tab) {
         super(theme, tab);
         this.hud.settings.onActivated();
      }

      public void initWidgets() {
         this.add(this.theme.settings(this.hud.settings)).expandX();
         this.add(this.theme.horizontalSeparator()).expandX();
         WButton openEditor = (WButton)this.add(this.theme.button("Edit")).expandX().widget();
         openEditor.action = () -> {
            MeteorClient.mc.method_1507(new HudEditorScreen(this.theme));
         };
         WHorizontalList buttons = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
         WButton var10000 = (WButton)buttons.add(this.theme.button("Clear")).expandX().widget();
         Hud var10001 = this.hud;
         Objects.requireNonNull(var10001);
         var10000.action = var10001::clear;
         var10000 = (WButton)buttons.add(this.theme.button("Reset to default elements")).expandX().widget();
         var10001 = this.hud;
         Objects.requireNonNull(var10001);
         var10000.action = var10001::resetToDefaultElements;
         this.add(this.theme.horizontalSeparator()).expandX();
         WHorizontalList bottom = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
         bottom.add(this.theme.label("Active: "));
         WCheckbox active = (WCheckbox)bottom.add(this.theme.checkbox(this.hud.active)).expandCellX().widget();
         active.action = () -> {
            this.hud.active = active.checked;
         };
         WButton resetSettings = (WButton)bottom.add(this.theme.button(GuiRenderer.RESET)).widget();
         Settings var6 = this.hud.settings;
         Objects.requireNonNull(var6);
         resetSettings.action = var6::reset;
      }

      protected void onRenderBefore(class_332 drawContext, float delta) {
         HudEditorScreen.renderElements(drawContext);
      }

      public boolean toClipboard() {
         return NbtUtils.toClipboard("hud-settings", this.hud.settings.toTag());
      }

      public boolean fromClipboard() {
         class_2487 clipboard = NbtUtils.fromClipboard(this.hud.settings.toTag());
         if (clipboard != null) {
            this.hud.settings.fromTag(clipboard);
            return true;
         } else {
            return false;
         }
      }
   }
}
