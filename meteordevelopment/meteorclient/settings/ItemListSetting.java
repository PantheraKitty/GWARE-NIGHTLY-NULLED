package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.class_1792;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class ItemListSetting extends Setting<List<class_1792>> {
   public final Predicate<class_1792> filter;
   private final boolean bypassFilterWhenSavingAndLoading;

   public ItemListSetting(String name, String description, List<class_1792> defaultValue, Consumer<List<class_1792>> onChanged, Consumer<Setting<List<class_1792>>> onModuleActivated, IVisible visible, Predicate<class_1792> filter, boolean bypassFilterWhenSavingAndLoading) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.filter = filter;
      this.bypassFilterWhenSavingAndLoading = bypassFilterWhenSavingAndLoading;
   }

   protected List<class_1792> parseImpl(String str) {
      String[] values = str.split(",");
      ArrayList items = new ArrayList(values.length);

      try {
         String[] var4 = values;
         int var5 = values.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String value = var4[var6];
            class_1792 item = (class_1792)parseId(class_7923.field_41178, value);
            if (item != null && (this.filter == null || this.filter.test(item))) {
               items.add(item);
            }
         }
      } catch (Exception var9) {
      }

      return items;
   }

   public void resetImpl() {
      this.value = new ArrayList((Collection)this.defaultValue);
   }

   protected boolean isValueValid(List<class_1792> value) {
      return true;
   }

   public Iterable<class_2960> getIdentifierSuggestions() {
      return class_7923.field_41178.method_10235();
   }

   public class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();
      Iterator var3 = ((List)this.get()).iterator();

      while(true) {
         class_1792 item;
         do {
            if (!var3.hasNext()) {
               tag.method_10566("value", valueTag);
               return tag;
            }

            item = (class_1792)var3.next();
         } while(!this.bypassFilterWhenSavingAndLoading && this.filter != null && !this.filter.test(item));

         valueTag.add(class_2519.method_23256(class_7923.field_41178.method_10221(item).toString()));
      }
   }

   public List<class_1792> load(class_2487 tag) {
      ((List)this.get()).clear();
      class_2499 valueTag = tag.method_10554("value", 8);
      Iterator var3 = valueTag.iterator();

      while(true) {
         class_1792 item;
         do {
            if (!var3.hasNext()) {
               return (List)this.get();
            }

            class_2520 tagI = (class_2520)var3.next();
            item = (class_1792)class_7923.field_41178.method_10223(class_2960.method_60654(tagI.method_10714()));
         } while(!this.bypassFilterWhenSavingAndLoading && this.filter != null && !this.filter.test(item));

         ((List)this.get()).add(item);
      }
   }

   public static class Builder extends Setting.SettingBuilder<ItemListSetting.Builder, List<class_1792>, ItemListSetting> {
      private Predicate<class_1792> filter;
      private boolean bypassFilterWhenSavingAndLoading;

      public Builder() {
         super(new ArrayList(0));
      }

      public ItemListSetting.Builder defaultValue(class_1792... defaults) {
         return (ItemListSetting.Builder)this.defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList());
      }

      public ItemListSetting.Builder filter(Predicate<class_1792> filter) {
         this.filter = filter;
         return this;
      }

      public ItemListSetting.Builder bypassFilterWhenSavingAndLoading() {
         this.bypassFilterWhenSavingAndLoading = true;
         return this;
      }

      public ItemListSetting build() {
         return new ItemListSetting(this.name, this.description, (List)this.defaultValue, this.onChanged, this.onModuleActivated, this.visible, this.filter, this.bypassFilterWhenSavingAndLoading);
      }
   }
}
