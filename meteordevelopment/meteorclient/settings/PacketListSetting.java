package meteordevelopment.meteorclient.settings;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2596;

public class PacketListSetting extends Setting<Set<Class<? extends class_2596<?>>>> {
   public final Predicate<Class<? extends class_2596<?>>> filter;
   private static List<String> suggestions;

   public PacketListSetting(String name, String description, Set<Class<? extends class_2596<?>>> defaultValue, Consumer<Set<Class<? extends class_2596<?>>>> onChanged, Consumer<Setting<Set<Class<? extends class_2596<?>>>>> onModuleActivated, Predicate<Class<? extends class_2596<?>>> filter, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.filter = filter;
   }

   public void resetImpl() {
      this.value = new ObjectOpenHashSet((Collection)this.defaultValue);
   }

   protected Set<Class<? extends class_2596<?>>> parseImpl(String str) {
      String[] values = str.split(",");
      ObjectOpenHashSet packets = new ObjectOpenHashSet(values.length);

      try {
         String[] var4 = values;
         int var5 = values.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String value = var4[var6];
            Class<? extends class_2596<?>> packet = PacketUtils.getPacket(value.trim());
            if (packet != null && (this.filter == null || this.filter.test(packet))) {
               packets.add(packet);
            }
         }
      } catch (Exception var9) {
      }

      return packets;
   }

   protected boolean isValueValid(Set<Class<? extends class_2596<?>>> value) {
      return true;
   }

   public List<String> getSuggestions() {
      if (suggestions == null) {
         suggestions = new ArrayList(PacketUtils.getC2SPackets().size() + PacketUtils.getS2CPackets().size());
         Iterator var1 = PacketUtils.getC2SPackets().iterator();

         Class packet;
         while(var1.hasNext()) {
            packet = (Class)var1.next();
            suggestions.add(PacketUtils.getName(packet));
         }

         var1 = PacketUtils.getS2CPackets().iterator();

         while(var1.hasNext()) {
            packet = (Class)var1.next();
            suggestions.add(PacketUtils.getName(packet));
         }
      }

      return suggestions;
   }

   public class_2487 save(class_2487 tag) {
      class_2499 valueTag = new class_2499();
      Iterator var3 = ((Set)this.get()).iterator();

      while(var3.hasNext()) {
         Class<? extends class_2596<?>> packet = (Class)var3.next();
         valueTag.add(class_2519.method_23256(PacketUtils.getName(packet)));
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   public Set<Class<? extends class_2596<?>>> load(class_2487 tag) {
      ((Set)this.get()).clear();
      class_2520 valueTag = tag.method_10580("value");
      if (valueTag instanceof class_2499) {
         Iterator var3 = ((class_2499)valueTag).iterator();

         while(true) {
            Class packet;
            do {
               do {
                  if (!var3.hasNext()) {
                     return (Set)this.get();
                  }

                  class_2520 t = (class_2520)var3.next();
                  packet = PacketUtils.getPacket(t.method_10714());
               } while(packet == null);
            } while(this.filter != null && !this.filter.test(packet));

            ((Set)this.get()).add(packet);
         }
      } else {
         return (Set)this.get();
      }
   }

   public static class Builder extends Setting.SettingBuilder<PacketListSetting.Builder, Set<Class<? extends class_2596<?>>>, PacketListSetting> {
      private Predicate<Class<? extends class_2596<?>>> filter;

      public Builder() {
         super(new ObjectOpenHashSet(0));
      }

      public PacketListSetting.Builder filter(Predicate<Class<? extends class_2596<?>>> filter) {
         this.filter = filter;
         return this;
      }

      public PacketListSetting build() {
         return new PacketListSetting(this.name, this.description, (Set)this.defaultValue, this.onChanged, this.onModuleActivated, this.filter, this.visible);
      }
   }
}
