package meteordevelopment.meteorclient.gui.screens;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.ActiveModulesChangedEvent;
import meteordevelopment.meteorclient.events.meteor.ModuleBindChangedEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WKeybind;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2487;

public class ModuleScreen extends WindowScreen {
   private final Module module;
   private WContainer settingsContainer;
   private WKeybind keybind;
   private WCheckbox active;

   public ModuleScreen(GuiTheme theme, Module module) {
      super(theme, theme.favorite(module.favorite), module.title);
      ((WFavorite)this.window.icon).action = () -> {
         module.favorite = ((WFavorite)this.window.icon).checked;
      };
      this.module = module;
   }

   public void initWidgets() {
      this.add(this.theme.label(this.module.description, (double)Utils.getWindowWidth() / 2.0D));
      if (!this.module.settings.groups.isEmpty()) {
         this.settingsContainer = (WContainer)this.add(this.theme.verticalList()).expandX().widget();
         this.settingsContainer.add(this.theme.settings(this.module.settings)).expandX();
      }

      WWidget widget = this.module.getWidget(this.theme);
      if (widget != null) {
         this.add(this.theme.horizontalSeparator()).expandX();
         Cell<WWidget> cell = this.add(widget);
         if (widget instanceof WContainer) {
            cell.expandX();
         }
      }

      WSection section = (WSection)this.add(this.theme.section("Bind", true)).expandX().widget();
      WHorizontalList bind = (WHorizontalList)section.add(this.theme.horizontalList()).expandX().widget();
      bind.add(this.theme.label("Bind: "));
      this.keybind = (WKeybind)bind.add(this.theme.keybind(this.module.keybind)).expandX().widget();
      this.keybind.actionOnSet = () -> {
         Modules.get().setModuleToBind(this.module);
      };
      WButton reset = (WButton)bind.add(this.theme.button(GuiRenderer.RESET)).expandCellX().right().widget();
      WKeybind var10001 = this.keybind;
      Objects.requireNonNull(var10001);
      reset.action = var10001::resetBind;
      WHorizontalList tobr = (WHorizontalList)section.add(this.theme.horizontalList()).widget();
      tobr.add(this.theme.label("Toggle on bind release: "));
      WCheckbox tobrC = (WCheckbox)tobr.add(this.theme.checkbox(this.module.toggleOnBindRelease)).widget();
      tobrC.action = () -> {
         this.module.toggleOnBindRelease = tobrC.checked;
      };
      WHorizontalList cf = (WHorizontalList)section.add(this.theme.horizontalList()).widget();
      cf.add(this.theme.label("Chat Feedback: "));
      WCheckbox cfC = (WCheckbox)cf.add(this.theme.checkbox(this.module.chatFeedback)).widget();
      cfC.action = () -> {
         this.module.chatFeedback = cfC.checked;
      };
      this.add(this.theme.horizontalSeparator()).expandX();
      WHorizontalList bottom = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
      bottom.add(this.theme.label("Active: "));
      this.active = (WCheckbox)bottom.add(this.theme.checkbox(this.module.isActive())).expandCellX().widget();
      this.active.action = () -> {
         if (this.module.isActive() != this.active.checked) {
            this.module.toggle();
         }

      };
      if (this.module.addon != null && this.module.addon != MeteorClient.ADDON) {
         bottom.add(this.theme.label("From: ")).right().widget();
         bottom.add(this.theme.label(this.module.addon.name).color(this.theme.textSecondaryColor())).right().widget();
      }

   }

   public boolean method_25422() {
      return !Modules.get().isBinding();
   }

   public void method_25393() {
      super.method_25393();
      this.module.settings.tick(this.settingsContainer, this.theme);
   }

   @EventHandler
   private void onModuleBindChanged(ModuleBindChangedEvent event) {
      this.keybind.reset();
   }

   @EventHandler
   private void onActiveModulesChanged(ActiveModulesChangedEvent event) {
      this.active.checked = this.module.isActive();
   }

   public boolean toClipboard() {
      return NbtUtils.toClipboard(this.module.title, this.module.toTagConfig());
   }

   public boolean fromClipboard() {
      class_2487 clipboard = NbtUtils.fromClipboard(this.module.toTagConfig());
      if (clipboard != null) {
         this.module.fromTagConfig(clipboard);
         return true;
      } else {
         return false;
      }
   }
}
