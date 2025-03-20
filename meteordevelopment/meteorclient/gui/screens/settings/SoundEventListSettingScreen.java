package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.Collection;
import java.util.List;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.class_3414;
import net.minecraft.class_7923;

public class SoundEventListSettingScreen extends RegistryListSettingScreen<class_3414> {
   public SoundEventListSettingScreen(GuiTheme theme, Setting<List<class_3414>> setting) {
      super(theme, "Select Sounds", setting, (Collection)setting.get(), class_7923.field_41172);
   }

   protected WWidget getValueWidget(class_3414 value) {
      return this.theme.label(this.getValueName(value));
   }

   protected String getValueName(class_3414 value) {
      return value.method_14833().method_12832();
   }
}
