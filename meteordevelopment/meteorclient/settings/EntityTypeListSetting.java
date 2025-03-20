package meteordevelopment.meteorclient.settings;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import net.minecraft.class_1299;
import net.minecraft.class_1311;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class EntityTypeListSetting extends Setting<Set<class_1299<?>>> {
   public final Predicate<class_1299<?>> filter;
   private List<String> suggestions;
   private static final List<String> groups = List.of("animal", "wateranimal", "monster", "ambient", "misc");

   public EntityTypeListSetting(String name, String description, Set<class_1299<?>> defaultValue, Consumer<Set<class_1299<?>>> onChanged, Consumer<Setting<Set<class_1299<?>>>> onModuleActivated, IVisible visible, Predicate<class_1299<?>> filter) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.filter = filter;
   }

   public void resetImpl() {
      this.value = new ObjectOpenHashSet((Collection)this.defaultValue);
   }

   protected Set<class_1299<?>> parseImpl(String str) {
      String[] values = str.split(",");
      ObjectOpenHashSet entities = new ObjectOpenHashSet(values.length);

      try {
         String[] var4 = values;
         int var5 = values.length;

         label93:
         for(int var6 = 0; var6 < var5; ++var6) {
            String value = var4[var6];
            class_1299<?> entity = (class_1299)parseId(class_7923.field_41177, value);
            if (entity != null) {
               entities.add(entity);
            } else {
               String lowerValue = value.trim().toLowerCase();
               if (groups.contains(lowerValue)) {
                  Iterator var10 = class_7923.field_41177.iterator();

                  while(true) {
                     while(true) {
                        class_1299 entityType;
                        do {
                           if (!var10.hasNext()) {
                              continue label93;
                           }

                           entityType = (class_1299)var10.next();
                        } while(this.filter != null && !this.filter.test(entityType));

                        byte var13 = -1;
                        switch(lowerValue.hashCode()) {
                        case -1413116420:
                           if (lowerValue.equals("animal")) {
                              var13 = 0;
                           }
                           break;
                        case -892145000:
                           if (lowerValue.equals("ambient")) {
                              var13 = 3;
                           }
                           break;
                        case 3351788:
                           if (lowerValue.equals("misc")) {
                              var13 = 4;
                           }
                           break;
                        case 726132179:
                           if (lowerValue.equals("wateranimal")) {
                              var13 = 1;
                           }
                           break;
                        case 1236617178:
                           if (lowerValue.equals("monster")) {
                              var13 = 2;
                           }
                        }

                        switch(var13) {
                        case 0:
                           if (entityType.method_5891() == class_1311.field_6294) {
                              entities.add(entityType);
                           }
                           break;
                        case 1:
                           if (entityType.method_5891() == class_1311.field_24460 || entityType.method_5891() == class_1311.field_6300 || entityType.method_5891() == class_1311.field_30092 || entityType.method_5891() == class_1311.field_34447) {
                              entities.add(entityType);
                           }
                           break;
                        case 2:
                           if (entityType.method_5891() == class_1311.field_6302) {
                              entities.add(entityType);
                           }
                           break;
                        case 3:
                           if (entityType.method_5891() == class_1311.field_6303) {
                              entities.add(entityType);
                           }
                           break;
                        case 4:
                           if (entityType.method_5891() == class_1311.field_17715) {
                              entities.add(entityType);
                           }
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var14) {
      }

      return entities;
   }

   protected boolean isValueValid(Set<class_1299<?>> value) {
      return true;
   }

   public List<String> getSuggestions() {
      if (this.suggestions == null) {
         this.suggestions = new ArrayList(groups);
         Iterator var1 = class_7923.field_41177.iterator();

         while(true) {
            class_1299 entityType;
            do {
               if (!var1.hasNext()) {
                  return this.suggestions;
               }

               entityType = (class_1299)var1.next();
            } while(this.filter != null && !this.filter.test(entityType));

            this.suggestions.add(class_7923.field_41177.method_10221(entityType).toString());
         }
      } else {
         return this.suggestions;
      }
   }

   public class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();
      Iterator var3 = ((Set)this.get()).iterator();

      while(var3.hasNext()) {
         class_1299<?> entityType = (class_1299)var3.next();
         valueTag.add(class_2519.method_23256(class_7923.field_41177.method_10221(entityType).toString()));
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   public Set<class_1299<?>> load(class_2487 tag) {
      ((Set)this.get()).clear();
      class_2499 valueTag = tag.method_10554("value", 8);
      Iterator var3 = valueTag.iterator();

      while(true) {
         class_1299 type;
         do {
            if (!var3.hasNext()) {
               return (Set)this.get();
            }

            class_2520 tagI = (class_2520)var3.next();
            type = (class_1299)class_7923.field_41177.method_10223(class_2960.method_60654(tagI.method_10714()));
         } while(this.filter != null && !this.filter.test(type));

         ((Set)this.get()).add(type);
      }
   }

   public static class Builder extends Setting.SettingBuilder<EntityTypeListSetting.Builder, Set<class_1299<?>>, EntityTypeListSetting> {
      private Predicate<class_1299<?>> filter;

      public Builder() {
         super(new ObjectOpenHashSet(0));
      }

      public EntityTypeListSetting.Builder defaultValue(class_1299<?>... defaults) {
         return (EntityTypeListSetting.Builder)this.defaultValue(defaults != null ? new ObjectOpenHashSet(defaults) : new ObjectOpenHashSet(0));
      }

      public EntityTypeListSetting.Builder onlyAttackable() {
         this.filter = EntityUtils::isAttackable;
         return this;
      }

      public EntityTypeListSetting.Builder filter(Predicate<class_1299<?>> filter) {
         this.filter = filter;
         return this;
      }

      public EntityTypeListSetting build() {
         return new EntityTypeListSetting(this.name, this.description, (Set)this.defaultValue, this.onChanged, this.onModuleActivated, this.visible, this.filter);
      }
   }
}
