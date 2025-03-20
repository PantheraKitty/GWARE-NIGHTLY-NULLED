package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.mixin.IdentifierAccessor;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class BlockListSettingScreen extends RegistryListSettingScreen<class_2248> {
   private static final class_2960 ID = class_2960.method_60655("minecraft", "");

   public BlockListSettingScreen(GuiTheme theme, Setting<List<class_2248>> setting) {
      super(theme, "Select Blocks", setting, (Collection)setting.get(), class_7923.field_41175);
   }

   protected boolean includeValue(class_2248 value) {
      Predicate<class_2248> filter = ((BlockListSetting)this.setting).filter;
      if (filter == null) {
         return value != class_2246.field_10124;
      } else {
         return filter.test(value);
      }
   }

   protected WWidget getValueWidget(class_2248 value) {
      return this.theme.itemWithLabel(value.method_8389().method_7854(), this.getValueName(value));
   }

   protected String getValueName(class_2248 value) {
      return Names.get(value);
   }

   protected boolean skipValue(class_2248 value) {
      return class_7923.field_41175.method_10221(value).method_12832().endsWith("_wall_banner");
   }

   protected class_2248 getAdditionalValue(class_2248 value) {
      String path = class_7923.field_41175.method_10221(value).method_12832();
      if (!path.endsWith("_banner")) {
         return null;
      } else {
         IdentifierAccessor var10000 = (IdentifierAccessor)ID;
         String var10001 = path.substring(0, path.length() - 6);
         var10000.setPath(var10001 + "wall_banner");
         return (class_2248)class_7923.field_41175.method_10223(ID);
      }
   }
}
