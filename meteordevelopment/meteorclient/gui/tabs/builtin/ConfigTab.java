package meteordevelopment.meteorclient.gui.tabs.builtin;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.render.prompts.YesNoPrompt;
import net.minecraft.class_437;

public class ConfigTab extends Tab {
   public ConfigTab() {
      super("Config");
   }

   public TabScreen createScreen(GuiTheme theme) {
      return new ConfigTab.ConfigScreen(theme, this);
   }

   public boolean isScreen(class_437 screen) {
      return screen instanceof ConfigTab.ConfigScreen;
   }

   public static class ConfigScreen extends WindowTabScreen {
      private final Settings settings;

      public ConfigScreen(GuiTheme theme, Tab tab) {
         super(theme, tab);
         this.settings = Config.get().settings;
         this.settings.onActivated();
         this.onClosed(() -> {
            String prefix = (String)Config.get().prefix.get();
            if (prefix.isBlank()) {
               ((YesNoPrompt)((YesNoPrompt)((YesNoPrompt)((YesNoPrompt)((YesNoPrompt)YesNoPrompt.create(theme, this.parent).title("Empty command prefix")).message("You have set your command prefix to nothing.")).message("This WILL prevent you from sending chat messages.")).message("Do you want to reset your prefix back to '.'?")).onYes(() -> {
                  Config.get().prefix.set(".");
               }).id("empty-command-prefix")).show();
            } else if (prefix.equals("/")) {
               ((YesNoPrompt)((YesNoPrompt)((YesNoPrompt)((YesNoPrompt)((YesNoPrompt)YesNoPrompt.create(theme, this.parent).title("Potential prefix conflict")).message("You have set your command prefix to '/', which is used by minecraft.")).message("This can cause conflict issues between meteor and minecraft commands.")).message("Do you want to reset your prefix to '.'?")).onYes(() -> {
                  Config.get().prefix.set(".");
               }).id("minecraft-prefix-conflict")).show();
            } else if (prefix.length() > 7) {
               ((YesNoPrompt)((YesNoPrompt)((YesNoPrompt)((YesNoPrompt)((YesNoPrompt)YesNoPrompt.create(theme, this.parent).title("Long command prefix")).message("You have set your command prefix to a very long string.")).message("This means that in order to execute any command, you will need to type %s followed by the command you want to run.", new Object[]{prefix})).message("Do you want to reset your prefix back to '.'?")).onYes(() -> {
                  Config.get().prefix.set(".");
               }).id("long-command-prefix")).show();
            }

         });
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
