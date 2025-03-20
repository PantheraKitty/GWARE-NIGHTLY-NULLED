package meteordevelopment.meteorclient.gui.tabs.builtin;

import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.screens.EditSystemScreen;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.macros.Macro;
import meteordevelopment.meteorclient.systems.macros.Macros;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.class_437;

public class MacrosTab extends Tab {
   public MacrosTab() {
      super("Macros");
   }

   public TabScreen createScreen(GuiTheme theme) {
      return new MacrosTab.MacrosScreen(theme, this);
   }

   public boolean isScreen(class_437 screen) {
      return screen instanceof MacrosTab.MacrosScreen;
   }

   private static class MacrosScreen extends WindowTabScreen {
      public MacrosScreen(GuiTheme theme, Tab tab) {
         super(theme, tab);
      }

      public void initWidgets() {
         WTable table = (WTable)this.add(this.theme.table()).expandX().minWidth(400.0D).widget();
         this.initTable(table);
         this.add(this.theme.horizontalSeparator()).expandX();
         WButton create = (WButton)this.add(this.theme.button("Create")).expandX().widget();
         create.action = () -> {
            MeteorClient.mc.method_1507(new MacrosTab.EditMacroScreen(this.theme, (Macro)null, this::reload));
         };
      }

      private void initTable(WTable table) {
         table.clear();
         if (!Macros.get().isEmpty()) {
            Iterator var2 = Macros.get().iterator();

            while(var2.hasNext()) {
               Macro macro = (Macro)var2.next();
               GuiTheme var10001 = this.theme;
               String var10002 = (String)macro.name.get();
               table.add(var10001.label(var10002 + " (" + String.valueOf(macro.keybind.get()) + ")"));
               WButton edit = (WButton)table.add(this.theme.button(GuiRenderer.EDIT)).expandCellX().right().widget();
               edit.action = () -> {
                  MeteorClient.mc.method_1507(new MacrosTab.EditMacroScreen(this.theme, macro, this::reload));
               };
               WMinus remove = (WMinus)table.add(this.theme.minus()).widget();
               remove.action = () -> {
                  Macros.get().remove(macro);
                  this.reload();
               };
               table.row();
            }

         }
      }

      public boolean toClipboard() {
         return NbtUtils.toClipboard(Macros.get());
      }

      public boolean fromClipboard() {
         return NbtUtils.fromClipboard((System)Macros.get());
      }
   }

   private static class EditMacroScreen extends EditSystemScreen<Macro> {
      public EditMacroScreen(GuiTheme theme, Macro value, Runnable reload) {
         super(theme, value, reload);
      }

      public Macro create() {
         return new Macro();
      }

      public boolean save() {
         if (!((String)((Macro)this.value).name.get()).isBlank() && !((List)((Macro)this.value).messages.get()).isEmpty() && ((Keybind)((Macro)this.value).keybind.get()).isSet()) {
            if (this.isNew) {
               Iterator var1 = Macros.get().iterator();

               while(var1.hasNext()) {
                  Macro m = (Macro)var1.next();
                  if (((Macro)this.value).equals(m)) {
                     return false;
                  }
               }
            }

            if (this.isNew) {
               Macros.get().add((Macro)this.value);
            } else {
               Macros.get().save();
            }

            return true;
         } else {
            return false;
         }
      }

      public Settings getSettings() {
         return ((Macro)this.value).settings;
      }
   }
}
