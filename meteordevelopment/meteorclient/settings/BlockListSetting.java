package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.class_2248;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class BlockListSetting extends Setting<List<class_2248>> {
   public final Predicate<class_2248> filter;

   public BlockListSetting(String name, String description, List<class_2248> defaultValue, Consumer<List<class_2248>> onChanged, Consumer<Setting<List<class_2248>>> onModuleActivated, Predicate<class_2248> filter, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.filter = filter;
   }

   public void resetImpl() {
      this.value = new ArrayList((Collection)this.defaultValue);
   }

   protected List<class_2248> parseImpl(String str) {
      String[] values = str.split(",");
      ArrayList blocks = new ArrayList(values.length);

      try {
         String[] var4 = values;
         int var5 = values.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String value = var4[var6];
            class_2248 block = (class_2248)parseId(class_7923.field_41175, value);
            if (block != null && (this.filter == null || this.filter.test(block))) {
               blocks.add(block);
            }
         }
      } catch (Exception var9) {
      }

      return blocks;
   }

   protected boolean isValueValid(List<class_2248> value) {
      return true;
   }

   public Iterable<class_2960> getIdentifierSuggestions() {
      return class_7923.field_41175.method_10235();
   }

   protected class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();
      Iterator var3 = ((List)this.get()).iterator();

      while(var3.hasNext()) {
         class_2248 block = (class_2248)var3.next();
         valueTag.add(class_2519.method_23256(class_7923.field_41175.method_10221(block).toString()));
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   protected List<class_2248> load(class_2487 tag) {
      ((List)this.get()).clear();
      class_2499 valueTag = tag.method_10554("value", 8);
      Iterator var3 = valueTag.iterator();

      while(true) {
         class_2248 block;
         do {
            if (!var3.hasNext()) {
               return (List)this.get();
            }

            class_2520 tagI = (class_2520)var3.next();
            block = (class_2248)class_7923.field_41175.method_10223(class_2960.method_60654(tagI.method_10714()));
         } while(this.filter != null && !this.filter.test(block));

         ((List)this.get()).add(block);
      }
   }

   public static class Builder extends Setting.SettingBuilder<BlockListSetting.Builder, List<class_2248>, BlockListSetting> {
      private Predicate<class_2248> filter;

      public Builder() {
         super(new ArrayList(0));
      }

      public BlockListSetting.Builder defaultValue(class_2248... defaults) {
         return (BlockListSetting.Builder)this.defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList());
      }

      public BlockListSetting.Builder filter(Predicate<class_2248> filter) {
         this.filter = filter;
         return this;
      }

      public BlockListSetting build() {
         return new BlockListSetting(this.name, this.description, (List)this.defaultValue, this.onChanged, this.onModuleActivated, this.filter, this.visible);
      }
   }
}
