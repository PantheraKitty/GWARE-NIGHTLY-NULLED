package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2487;
import net.minecraft.class_2520;

public class ColorListSetting extends Setting<List<SettingColor>> {
   public ColorListSetting(String name, String description, List<SettingColor> defaultValue, Consumer<List<SettingColor>> onChanged, Consumer<Setting<List<SettingColor>>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   protected List<SettingColor> parseImpl(String str) {
      ArrayList colors = new ArrayList();

      try {
         String[] colorsStr = str.replaceAll("\\s+", "").split(";");
         String[] var4 = colorsStr;
         int var5 = colorsStr.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String colorStr = var4[var6];
            String[] strs = colorStr.split(",");
            colors.add(new SettingColor(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[3])));
         }
      } catch (NumberFormatException | IndexOutOfBoundsException var9) {
      }

      return colors;
   }

   protected boolean isValueValid(List<SettingColor> value) {
      return true;
   }

   protected void resetImpl() {
      this.value = new ArrayList(((List)this.defaultValue).size());
      Iterator var1 = ((List)this.defaultValue).iterator();

      while(var1.hasNext()) {
         SettingColor settingColor = (SettingColor)var1.next();
         ((List)this.value).add(new SettingColor(settingColor));
      }

   }

   protected class_2487 save(class_2487 tag) {
      tag.method_10566("value", NbtUtils.listToTag((Iterable)this.get()));
      return tag;
   }

   protected List<SettingColor> load(class_2487 tag) {
      ((List)this.get()).clear();
      Iterator var2 = tag.method_10554("value", 10).iterator();

      while(var2.hasNext()) {
         class_2520 e = (class_2520)var2.next();
         ((List)this.get()).add((new SettingColor()).fromTag((class_2487)e));
      }

      return (List)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<ColorListSetting.Builder, List<SettingColor>, ColorListSetting> {
      public Builder() {
         super(new ArrayList());
      }

      public ColorListSetting build() {
         return new ColorListSetting(this.name, this.description, (List)this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
