package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.Collection;
import java.util.List;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_7923;

public class ParticleTypeListSettingScreen extends RegistryListSettingScreen<class_2396<?>> {
   public ParticleTypeListSettingScreen(GuiTheme theme, Setting<List<class_2396<?>>> setting) {
      super(theme, "Select Particles", setting, (Collection)setting.get(), class_7923.field_41180);
   }

   protected WWidget getValueWidget(class_2396<?> value) {
      return this.theme.label(this.getValueName(value));
   }

   protected String getValueName(class_2396<?> value) {
      return Names.get(value);
   }

   protected boolean skipValue(class_2396<?> value) {
      return !(value instanceof class_2394);
   }
}
