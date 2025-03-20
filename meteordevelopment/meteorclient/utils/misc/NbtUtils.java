package meteordevelopment.meteorclient.utils.misc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.utils.render.prompts.OkPrompt;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2505;
import net.minecraft.class_2507;
import net.minecraft.class_2520;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class NbtUtils {
   private NbtUtils() {
   }

   public static <T extends ISerializable<?>> class_2499 listToTag(Iterable<T> list) {
      class_2499 tag = new class_2499();
      Iterator var2 = list.iterator();

      while(var2.hasNext()) {
         T item = (ISerializable)var2.next();
         tag.add(item.toTag());
      }

      return tag;
   }

   public static <T> List<T> listFromTag(class_2499 tag, NbtUtils.ToValue<T> toItem) {
      List<T> list = new ArrayList(tag.size());
      Iterator var3 = tag.iterator();

      while(var3.hasNext()) {
         class_2520 itemTag = (class_2520)var3.next();
         T value = toItem.toValue(itemTag);
         if (value != null) {
            list.add(value);
         }
      }

      return list;
   }

   public static <K, V extends ISerializable<?>> class_2487 mapToTag(Map<K, V> map) {
      class_2487 tag = new class_2487();
      Iterator var2 = map.keySet().iterator();

      while(var2.hasNext()) {
         K key = var2.next();
         tag.method_10566(key.toString(), ((ISerializable)map.get(key)).toTag());
      }

      return tag;
   }

   public static <K, V> Map<K, V> mapFromTag(class_2487 tag, NbtUtils.ToKey<K> toKey, NbtUtils.ToValue<V> toValue) {
      Map<K, V> map = new HashMap(tag.method_10546());
      Iterator var4 = tag.method_10541().iterator();

      while(var4.hasNext()) {
         String key = (String)var4.next();
         map.put(toKey.toKey(key), toValue.toValue(tag.method_10580(key)));
      }

      return map;
   }

   public static boolean toClipboard(System<?> system) {
      return toClipboard(system.getName(), system.toTag());
   }

   public static boolean toClipboard(String name, class_2487 nbtCompound) {
      String preClipboard = MeteorClient.mc.field_1774.method_1460();

      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         class_2507.method_10634(nbtCompound, byteArrayOutputStream);
         MeteorClient.mc.field_1774.method_1455(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
         return true;
      } catch (Exception var4) {
         MeteorClient.LOG.error(String.format("Error copying %s NBT to clipboard!", name));
         ((OkPrompt)((OkPrompt)((OkPrompt)OkPrompt.create().title(String.format("Error copying %s NBT to clipboard!", name))).message("This shouldn't happen, please report it.")).id("nbt-copying")).show();
         MeteorClient.mc.field_1774.method_1455(preClipboard);
         return false;
      }
   }

   public static boolean fromClipboard(System<?> system) {
      class_2487 clipboard = fromClipboard(system.toTag());
      if (clipboard != null) {
         system.fromTag(clipboard);
         return true;
      } else {
         return false;
      }
   }

   public static class_2487 fromClipboard(class_2487 schema) {
      try {
         byte[] data = Base64.getDecoder().decode(MeteorClient.mc.field_1774.method_1460().trim());
         ByteArrayInputStream bis = new ByteArrayInputStream(data);
         class_2487 pasted = class_2507.method_10629(new DataInputStream(bis), class_2505.method_53898());
         Iterator var4 = schema.method_10541().iterator();

         String key;
         do {
            if (!var4.hasNext()) {
               if (!pasted.method_10558("name").equals(schema.method_10558("name"))) {
                  return null;
               }

               return pasted;
            }

            key = (String)var4.next();
         } while(pasted.method_10541().contains(key));

         return null;
      } catch (Exception var6) {
         MeteorClient.LOG.error("Invalid NBT data pasted!");
         ((OkPrompt)((OkPrompt)((OkPrompt)OkPrompt.create().title("Error pasting NBT data!")).message("Please check that the data you pasted is valid.")).id("nbt-pasting")).show();
         return null;
      }
   }

   public interface ToValue<T> {
      T toValue(class_2520 var1);
   }

   public interface ToKey<T> {
      T toKey(String var1);
   }
}
