package meteordevelopment.meteorclient.utils.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1934;

public class TargetUtils {
   private static final List<class_1297> ENTITIES = new ArrayList();

   private TargetUtils() {
   }

   @Nullable
   public static class_1297 get(Predicate<class_1297> isGood, SortPriority sortPriority) {
      ENTITIES.clear();
      getList(ENTITIES, isGood, sortPriority, 1);
      return !ENTITIES.isEmpty() ? (class_1297)ENTITIES.getFirst() : null;
   }

   public static void getList(List<class_1297> targetList, Predicate<class_1297> isGood, SortPriority sortPriority, int maxCount) {
      targetList.clear();
      Iterator var4 = MeteorClient.mc.field_1687.method_18112().iterator();

      while(var4.hasNext()) {
         class_1297 entity = (class_1297)var4.next();
         if (entity != null && isGood.test(entity)) {
            targetList.add(entity);
         }
      }

      FakePlayerManager.forEach((fp) -> {
         if (fp != null && isGood.test(fp)) {
            targetList.add(fp);
         }

      });
      targetList.sort(sortPriority);

      for(int i = targetList.size() - 1; i >= maxCount; --i) {
         targetList.remove(i);
      }

   }

   @Nullable
   public static class_1657 getPlayerTarget(double range, SortPriority priority) {
      return !Utils.canUpdate() ? null : (class_1657)get((entity) -> {
         if (entity instanceof class_1657 && entity != MeteorClient.mc.field_1724) {
            if (!((class_1657)entity).method_29504() && !(((class_1657)entity).method_6032() <= 0.0F)) {
               if (!PlayerUtils.isWithin(entity, range)) {
                  return false;
               } else if (!Friends.get().shouldAttack((class_1657)entity)) {
                  return false;
               } else {
                  return EntityUtils.getGameMode((class_1657)entity) == class_1934.field_9215 || entity instanceof FakePlayerEntity;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }, priority);
   }

   public static boolean isBadTarget(class_1657 target, double range) {
      if (target == null) {
         return true;
      } else {
         return !PlayerUtils.isWithin((class_1297)target, range) || !target.method_5805() || target.method_29504() || target.method_6032() <= 0.0F;
      }
   }
}
