package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.Collection;
import java.util.List;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.class_3917;
import net.minecraft.class_7923;

public class ScreenHandlerSettingScreen extends RegistryListSettingScreen<class_3917<?>> {
   public ScreenHandlerSettingScreen(GuiTheme theme, Setting<List<class_3917<?>>> setting) {
      super(theme, "Select Screen Handlers", setting, (Collection)setting.get(), class_7923.field_41187);
   }

   protected WWidget getValueWidget(class_3917<?> value) {
      return this.theme.label(this.getValueName(value));
   }

   protected String getValueName(class_3917<?> type) {
      return class_7923.field_41187.method_10221(type).toString();
   }
}
