package meteordevelopment.meteorclient.utils.entity.fakeplayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.Utils;

public class FakePlayerManager {
   private static final List<FakePlayerEntity> ENTITIES = new ArrayList();

   private FakePlayerManager() {
   }

   public static List<FakePlayerEntity> getFakePlayers() {
      return ENTITIES;
   }

   public static FakePlayerEntity get(String name) {
      Iterator var1 = ENTITIES.iterator();

      FakePlayerEntity fp;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         fp = (FakePlayerEntity)var1.next();
      } while(!fp.method_5477().getString().equals(name));

      return fp;
   }

   public static void add(String name, float health, boolean copyInv) {
      if (Utils.canUpdate()) {
         FakePlayerEntity fakePlayer = new FakePlayerEntity(MeteorClient.mc.field_1724, name, health, copyInv);
         fakePlayer.spawn();
         ENTITIES.add(fakePlayer);
      }
   }

   public static void remove(FakePlayerEntity fp) {
      ENTITIES.removeIf((fp1) -> {
         if (fp1.method_5477().getString().equals(fp.method_5477().getString())) {
            fp1.despawn();
            return true;
         } else {
            return false;
         }
      });
   }

   public static void clear() {
      if (!ENTITIES.isEmpty()) {
         ENTITIES.forEach(FakePlayerEntity::despawn);
         ENTITIES.clear();
      }
   }

   public static void forEach(Consumer<FakePlayerEntity> action) {
      Iterator var1 = ENTITIES.iterator();

      while(var1.hasNext()) {
         FakePlayerEntity fakePlayer = (FakePlayerEntity)var1.next();
         action.accept(fakePlayer);
      }

   }

   public static int count() {
      return ENTITIES.size();
   }

   public static Stream<FakePlayerEntity> stream() {
      return ENTITIES.stream();
   }

   public static boolean contains(FakePlayerEntity fp) {
      return ENTITIES.contains(fp);
   }
}
