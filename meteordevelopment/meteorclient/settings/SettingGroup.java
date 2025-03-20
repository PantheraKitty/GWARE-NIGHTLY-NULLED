package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2520;
import org.jetbrains.annotations.NotNull;

public class SettingGroup implements ISerializable<SettingGroup>, Iterable<Setting<?>> {
   public final String name;
   public boolean sectionExpanded;
   final List<Setting<?>> settings = new ArrayList(1);

   SettingGroup(String name, boolean sectionExpanded) {
      this.name = name;
      this.sectionExpanded = sectionExpanded;
   }

   public Setting<?> get(String name) {
      Iterator var2 = this.iterator();

      Setting setting;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         setting = (Setting)var2.next();
      } while(!setting.name.equals(name));

      return setting;
   }

   public <T> Setting<T> add(Setting<T> setting) {
      this.settings.add(setting);
      return setting;
   }

   public Setting<?> getByIndex(int index) {
      return (Setting)this.settings.get(index);
   }

   @NotNull
   public Iterator<Setting<?>> iterator() {
      return this.settings.iterator();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("name", this.name);
      tag.method_10556("sectionExpanded", this.sectionExpanded);
      class_2499 settingsTag = new class_2499();
      Iterator var3 = this.iterator();

      while(var3.hasNext()) {
         Setting<?> setting = (Setting)var3.next();
         if (setting.wasChanged()) {
            settingsTag.add(setting.toTag());
         }
      }

      tag.method_10566("settings", settingsTag);
      return tag;
   }

   public SettingGroup fromTag(class_2487 tag) {
      this.sectionExpanded = tag.method_10577("sectionExpanded");
      class_2499 settingsTag = tag.method_10554("settings", 10);
      Iterator var3 = settingsTag.iterator();

      while(var3.hasNext()) {
         class_2520 t = (class_2520)var3.next();
         class_2487 settingTag = (class_2487)t;
         Setting<?> setting = this.get(settingTag.method_10558("name"));
         if (setting != null) {
            setting.fromTag(settingTag);
         }
      }

      return this;
   }
}
