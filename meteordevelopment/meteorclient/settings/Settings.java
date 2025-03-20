package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.render.color.RainbowColors;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2520;
import org.jetbrains.annotations.NotNull;

public class Settings implements ISerializable<Settings>, Iterable<SettingGroup> {
   private SettingGroup defaultGroup;
   public final List<SettingGroup> groups = new ArrayList(1);

   public void onActivated() {
      Iterator var1 = this.groups.iterator();

      while(var1.hasNext()) {
         SettingGroup group = (SettingGroup)var1.next();
         Iterator var3 = group.iterator();

         while(var3.hasNext()) {
            Setting<?> setting = (Setting)var3.next();
            setting.onActivated();
         }
      }

   }

   public Setting<?> get(String name) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         SettingGroup sg = (SettingGroup)var2.next();
         Iterator var4 = sg.iterator();

         while(var4.hasNext()) {
            Setting<?> setting = (Setting)var4.next();
            if (name.equalsIgnoreCase(setting.name)) {
               return setting;
            }
         }
      }

      return null;
   }

   public void reset() {
      Iterator var1 = this.groups.iterator();

      while(var1.hasNext()) {
         SettingGroup group = (SettingGroup)var1.next();
         Iterator var3 = group.iterator();

         while(var3.hasNext()) {
            Setting<?> setting = (Setting)var3.next();
            setting.reset();
         }
      }

   }

   public SettingGroup getGroup(String name) {
      Iterator var2 = this.iterator();

      SettingGroup sg;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         sg = (SettingGroup)var2.next();
      } while(!sg.name.equals(name));

      return sg;
   }

   public int sizeGroups() {
      return this.groups.size();
   }

   public SettingGroup getDefaultGroup() {
      if (this.defaultGroup == null) {
         this.defaultGroup = this.createGroup("General");
      }

      return this.defaultGroup;
   }

   public SettingGroup createGroup(String name, boolean expanded) {
      SettingGroup group = new SettingGroup(name, expanded);
      this.groups.add(group);
      return group;
   }

   public SettingGroup createGroup(String name) {
      return this.createGroup(name, true);
   }

   public void registerColorSettings(Module module) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         SettingGroup group = (SettingGroup)var2.next();
         Iterator var4 = group.iterator();

         while(var4.hasNext()) {
            Setting<?> setting = (Setting)var4.next();
            setting.module = module;
            if (setting instanceof ColorSetting) {
               RainbowColors.addSetting(setting);
            } else if (setting instanceof ColorListSetting) {
               RainbowColors.addSettingList(setting);
            }
         }
      }

   }

   public void unregisterColorSettings() {
      Iterator var1 = this.iterator();

      while(var1.hasNext()) {
         SettingGroup group = (SettingGroup)var1.next();
         Iterator var3 = group.iterator();

         while(var3.hasNext()) {
            Setting<?> setting = (Setting)var3.next();
            if (setting instanceof ColorSetting) {
               RainbowColors.removeSetting(setting);
            } else if (setting instanceof ColorListSetting) {
               RainbowColors.removeSettingList(setting);
            }
         }
      }

   }

   public void tick(WContainer settings, GuiTheme theme) {
      Iterator var3 = this.groups.iterator();

      while(var3.hasNext()) {
         SettingGroup group = (SettingGroup)var3.next();

         Setting setting;
         boolean visible;
         for(Iterator var5 = group.iterator(); var5.hasNext(); setting.lastWasVisible = visible) {
            setting = (Setting)var5.next();
            visible = setting.isVisible();
            if (visible != setting.lastWasVisible) {
               settings.clear();
               settings.add(theme.settings(this)).expandX();
            }
         }
      }

   }

   @NotNull
   public Iterator<SettingGroup> iterator() {
      return this.groups.iterator();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("groups", NbtUtils.listToTag(this.groups));
      return tag;
   }

   public Settings fromTag(class_2487 tag) {
      class_2499 groupsTag = tag.method_10554("groups", 10);
      Iterator var3 = groupsTag.iterator();

      while(var3.hasNext()) {
         class_2520 t = (class_2520)var3.next();
         class_2487 groupTag = (class_2487)t;
         SettingGroup sg = this.getGroup(groupTag.method_10558("name"));
         if (sg != null) {
            sg.fromTag(groupTag);
         }
      }

      return this;
   }
}
