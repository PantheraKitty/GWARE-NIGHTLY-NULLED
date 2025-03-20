package meteordevelopment.meteorclient.gui.tabs.builtin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.class_2487;
import net.minecraft.class_437;

public class GuiTab extends Tab {
   public GuiTab() {
      super("GUI");
   }

   public TabScreen createScreen(GuiTheme theme) {
      return new GuiTab.GuiScreen(theme, this);
   }

   public boolean isScreen(class_437 screen) {
      return screen instanceof GuiTab.GuiScreen;
   }

   private static class GuiScreen extends WindowTabScreen {
      public GuiScreen(GuiTheme theme, Tab tab) {
         super(theme, tab);
         theme.settings.onActivated();
      }

      public void initWidgets() {
         WTable table = (WTable)this.add(this.theme.table()).expandX().widget();
         table.add(this.theme.label("Theme:"));
         WDropdown<String> themeW = (WDropdown)table.add(this.theme.dropdown(GuiThemes.getNames(), GuiThemes.get().name)).widget();
         themeW.action = () -> {
            GuiThemes.select((String)themeW.get());
            MeteorClient.mc.method_1507((class_437)null);
            this.tab.openScreen(GuiThemes.get());
         };
         WButton reset = (WButton)this.add(this.theme.button("Reset GUI Layout")).widget();
         reset.action = () -> {
            this.theme.clearWindowConfigs();
         };
         this.add(this.theme.settings(this.theme.settings)).expandX();
      }

      public boolean toClipboard() {
         return NbtUtils.toClipboard(this.theme.name + " GUI Theme", this.theme.toTag());
      }

      public boolean fromClipboard() {
         class_2487 clipboard = NbtUtils.fromClipboard(this.theme.toTag());
         if (clipboard != null) {
            this.theme.fromTag(clipboard);
            return true;
         } else {
            return false;
         }
      }
   }
}
