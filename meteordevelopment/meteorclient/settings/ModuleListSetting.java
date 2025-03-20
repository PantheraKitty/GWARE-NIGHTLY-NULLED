package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;

public class ModuleListSetting extends Setting<List<Module>> {
   private static List<String> suggestions;

   public ModuleListSetting(String name, String description, List<Module> defaultValue, Consumer<List<Module>> onChanged, Consumer<Setting<List<Module>>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   public void resetImpl() {
      this.value = new ArrayList((Collection)this.defaultValue);
   }

   protected List<Module> parseImpl(String str) {
      String[] values = str.split(",");
      ArrayList modules = new ArrayList(values.length);

      try {
         String[] var4 = values;
         int var5 = values.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String value = var4[var6];
            Module module = Modules.get().get(value.trim());
            if (module != null) {
               modules.add(module);
            }
         }
      } catch (Exception var9) {
      }

      return modules;
   }

   protected boolean isValueValid(List<Module> value) {
      return true;
   }

   public List<String> getSuggestions() {
      if (suggestions == null) {
         suggestions = new ArrayList(Modules.get().getAll().size());
         Iterator var1 = Modules.get().getAll().iterator();

         while(var1.hasNext()) {
            Module module = (Module)var1.next();
            suggestions.add(module.name);
         }
      }

      return suggestions;
   }

   public class_2487 save(class_2487 tag) {
      class_2499 modulesTag = new class_2499();
      Iterator var3 = ((List)this.get()).iterator();

      while(var3.hasNext()) {
         Module module = (Module)var3.next();
         modulesTag.add(class_2519.method_23256(module.name));
      }

      tag.method_10566("modules", modulesTag);
      return tag;
   }

   public List<Module> load(class_2487 tag) {
      ((List)this.get()).clear();
      class_2499 valueTag = tag.method_10554("modules", 8);
      Iterator var3 = valueTag.iterator();

      while(var3.hasNext()) {
         class_2520 tagI = (class_2520)var3.next();
         Module module = Modules.get().get(tagI.method_10714());
         if (module != null) {
            ((List)this.get()).add(module);
         }
      }

      return (List)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<ModuleListSetting.Builder, List<Module>, ModuleListSetting> {
      public Builder() {
         super(new ArrayList(0));
      }

      @SafeVarargs
      public final ModuleListSetting.Builder defaultValue(Class<? extends Module>... defaults) {
         List<Module> modules = new ArrayList();
         Class[] var3 = defaults;
         int var4 = defaults.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Class<? extends Module> klass = var3[var5];
            if (Modules.get().get(klass) != null) {
               modules.add(Modules.get().get(klass));
            }
         }

         return (ModuleListSetting.Builder)this.defaultValue(modules);
      }

      public ModuleListSetting build() {
         return new ModuleListSetting(this.name, this.description, (List)this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
