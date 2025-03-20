package meteordevelopment.meteorclient.gui.tabs.builtin;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.config.AntiCheatConfig;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.class_437;

public class AntiCheatTab extends Tab {
   public AntiCheatTab() {
      super("AntiCheat");
   }

   public TabScreen createScreen(GuiTheme theme) {
      return new AntiCheatTab.ConfigScreen(theme, this);
   }

   public boolean isScreen(class_437 screen) {
      return screen instanceof AntiCheatTab.ConfigScreen;
   }

   public static class ConfigScreen extends WindowTabScreen {
      private final Settings settings;

      public ConfigScreen(GuiTheme theme, Tab tab) {
         super(theme, tab);
         this.settings = AntiCheatConfig.get().settings;
         this.settings.onActivated();
      }

      public void initWidgets() {
         this.add(this.theme.settings(this.settings)).expandX();
      }

      public void method_25393() {
         super.method_25393();
         this.settings.tick(this.window, this.theme);
      }

      public boolean toClipboard() {
         return NbtUtils.toClipboard(Config.get());
      }

      public boolean fromClipboard() {
         return NbtUtils.fromClipboard((System)Config.get());
      }
   }
}
