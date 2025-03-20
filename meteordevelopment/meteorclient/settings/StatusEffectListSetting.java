package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_1291;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class StatusEffectListSetting extends Setting<List<class_1291>> {
   public StatusEffectListSetting(String name, String description, List<class_1291> defaultValue, Consumer<List<class_1291>> onChanged, Consumer<Setting<List<class_1291>>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   public void resetImpl() {
      this.value = new ArrayList((Collection)this.defaultValue);
   }

   protected List<class_1291> parseImpl(String str) {
      String[] values = str.split(",");
      ArrayList effects = new ArrayList(values.length);

      try {
         String[] var4 = values;
         int var5 = values.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String value = var4[var6];
            class_1291 effect = (class_1291)parseId(class_7923.field_41174, value);
            if (effect != null) {
               effects.add(effect);
            }
         }
      } catch (Exception var9) {
      }

      return effects;
   }

   protected boolean isValueValid(List<class_1291> value) {
      return true;
   }

   public Iterable<class_2960> getIdentifierSuggestions() {
      return class_7923.field_41174.method_10235();
   }

   public class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();
      Iterator var3 = ((List)this.get()).iterator();

      while(var3.hasNext()) {
         class_1291 effect = (class_1291)var3.next();
         class_2960 id = class_7923.field_41174.method_10221(effect);
         if (id != null) {
            valueTag.add(class_2519.method_23256(id.toString()));
         }
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   public List<class_1291> load(class_2487 tag) {
      ((List)this.get()).clear();
      class_2499 valueTag = tag.method_10554("value", 8);
      Iterator var3 = valueTag.iterator();

      while(var3.hasNext()) {
         class_2520 tagI = (class_2520)var3.next();
         class_1291 effect = (class_1291)class_7923.field_41174.method_10223(class_2960.method_60654(tagI.method_10714()));
         if (effect != null) {
            ((List)this.get()).add(effect);
         }
      }

      return (List)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<StatusEffectListSetting.Builder, List<class_1291>, StatusEffectListSetting> {
      public Builder() {
         super(new ArrayList(0));
      }

      public StatusEffectListSetting.Builder defaultValue(class_1291... defaults) {
         return (StatusEffectListSetting.Builder)this.defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList());
      }

      public StatusEffectListSetting build() {
         return new StatusEffectListSetting(this.name, this.description, (List)this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
