package meteordevelopment.meteorclient.settings;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2591;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_6880.class_6883;
import net.minecraft.class_6885.class_6888;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StorageBlockListSetting extends Setting<List<class_2591<?>>> {
   public static final class_2591<?>[] STORAGE_BLOCKS;
   public static final class_2378<class_2591<?>> REGISTRY;

   public StorageBlockListSetting(String name, String description, List<class_2591<?>> defaultValue, Consumer<List<class_2591<?>>> onChanged, Consumer<Setting<List<class_2591<?>>>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   public void resetImpl() {
      this.value = new ArrayList((Collection)this.defaultValue);
   }

   protected List<class_2591<?>> parseImpl(String str) {
      String[] values = str.split(",");
      ArrayList blocks = new ArrayList(values.length);

      try {
         String[] var4 = values;
         int var5 = values.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String value = var4[var6];
            class_2591<?> block = (class_2591)parseId(class_7923.field_41181, value);
            if (block != null) {
               blocks.add(block);
            }
         }
      } catch (Exception var9) {
      }

      return blocks;
   }

   protected boolean isValueValid(List<class_2591<?>> value) {
      return true;
   }

   public Iterable<class_2960> getIdentifierSuggestions() {
      return class_7923.field_41181.method_10235();
   }

   public class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();
      Iterator var3 = ((List)this.get()).iterator();

      while(var3.hasNext()) {
         class_2591<?> type = (class_2591)var3.next();
         class_2960 id = class_7923.field_41181.method_10221(type);
         if (id != null) {
            valueTag.add(class_2519.method_23256(id.toString()));
         }
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   public List<class_2591<?>> load(class_2487 tag) {
      ((List)this.get()).clear();
      class_2499 valueTag = tag.method_10554("value", 8);
      Iterator var3 = valueTag.iterator();

      while(var3.hasNext()) {
         class_2520 tagI = (class_2520)var3.next();
         class_2591<?> type = (class_2591)class_7923.field_41181.method_10223(class_2960.method_60654(tagI.method_10714()));
         if (type != null) {
            ((List)this.get()).add(type);
         }
      }

      return (List)this.get();
   }

   static {
      STORAGE_BLOCKS = new class_2591[]{class_2591.field_16411, class_2591.field_16415, class_2591.field_11894, class_2591.field_17380, class_2591.field_11914, class_2591.field_40329, class_2591.field_46808, class_2591.field_11887, class_2591.field_42781, class_2591.field_11899, class_2591.field_11901, class_2591.field_11903, class_2591.field_11888, class_2591.field_11896, class_2591.field_16414, class_2591.field_11891};
      REGISTRY = new StorageBlockListSetting.SRegistry();
   }

   private static class SRegistry extends class_2370<class_2591<?>> {
      public SRegistry() {
         super(class_5321.method_29180(MeteorClient.identifier("storage-blocks")), Lifecycle.stable());
      }

      public int method_10204() {
         return StorageBlockListSetting.STORAGE_BLOCKS.length;
      }

      @Nullable
      public class_2960 getId(class_2591<?> entry) {
         return null;
      }

      public Optional<class_5321<class_2591<?>>> getKey(class_2591<?> entry) {
         return Optional.empty();
      }

      public int getRawId(@Nullable class_2591<?> entry) {
         return 0;
      }

      @Nullable
      public class_2591<?> get(@Nullable class_5321<class_2591<?>> key) {
         return null;
      }

      @Nullable
      public class_2591<?> get(@Nullable class_2960 id) {
         return null;
      }

      public Lifecycle method_31138() {
         return null;
      }

      public Set<class_2960> method_10235() {
         return null;
      }

      public class_2591<?> getOrThrow(int index) {
         return (class_2591)super.method_39974(index);
      }

      public boolean method_10250(class_2960 id) {
         return false;
      }

      @Nullable
      public class_2591<?> get(int index) {
         return null;
      }

      @NotNull
      public Iterator<class_2591<?>> iterator() {
         return ObjectIterators.wrap(StorageBlockListSetting.STORAGE_BLOCKS);
      }

      public boolean method_35842(class_5321<class_2591<?>> key) {
         return false;
      }

      public Set<Entry<class_5321<class_2591<?>>, class_2591<?>>> method_29722() {
         return null;
      }

      public Optional<class_6883<class_2591<?>>> method_10240(class_5819 random) {
         return Optional.empty();
      }

      public class_2378<class_2591<?>> method_40276() {
         return null;
      }

      public class_6883<class_2591<?>> createEntry(class_2591<?> value) {
         return null;
      }

      public Optional<class_6883<class_2591<?>>> method_40265(int rawId) {
         return Optional.empty();
      }

      public Optional<class_6883<class_2591<?>>> method_40264(class_5321<class_2591<?>> key) {
         return Optional.empty();
      }

      public Stream<class_6883<class_2591<?>>> method_40270() {
         return null;
      }

      public Optional<class_6888<class_2591<?>>> method_40266(class_6862<class_2591<?>> tag) {
         return Optional.empty();
      }

      public class_6888<class_2591<?>> method_40260(class_6862<class_2591<?>> tag) {
         return null;
      }

      public Stream<Pair<class_6862<class_2591<?>>, class_6888<class_2591<?>>>> method_40272() {
         return null;
      }

      public Stream<class_6862<class_2591<?>>> method_40273() {
         return null;
      }

      public void method_40278() {
      }

      public void method_40257(Map<class_6862<class_2591<?>>, List<class_6880<class_2591<?>>>> tagEntries) {
      }

      public Set<class_5321<class_2591<?>>> method_42021() {
         return null;
      }
   }

   public static class Builder extends Setting.SettingBuilder<StorageBlockListSetting.Builder, List<class_2591<?>>, StorageBlockListSetting> {
      public Builder() {
         super(new ArrayList(0));
      }

      public StorageBlockListSetting.Builder defaultValue(class_2591<?>... defaults) {
         return (StorageBlockListSetting.Builder)this.defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList());
      }

      public StorageBlockListSetting build() {
         return new StorageBlockListSetting(this.name, this.description, (List)this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
