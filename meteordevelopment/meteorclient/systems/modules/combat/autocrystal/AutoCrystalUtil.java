package meteordevelopment.meteorclient.systems.modules.combat.autocrystal;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3965;

public class AutoCrystalUtil {
   public static class_3965 getPlaceBlockHitResult(class_2338 blockPos) {
      class_2350 dir = getPlaceOnDirection(blockPos);
      class_243 pos = getPosForDir(blockPos, dir);
      return new class_3965(pos, dir, blockPos, true);
   }

   private static class_2350 getPlaceOnDirection(class_2338 blockPos) {
      if (blockPos != null && MeteorClient.mc.field_1687 != null && MeteorClient.mc.field_1724 != null) {
         class_2350 bestdir = null;
         double bestDist = -1.0D;
         class_2350[] var4 = class_2350.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            class_2350 dir = var4[var6];
            class_243 pos = getPosForDir(blockPos, dir);
            double dist = MeteorClient.mc.field_1724.method_33571().method_1025(pos);
            if (dist >= 0.0D && (bestDist < 0.0D || dist < bestDist)) {
               bestdir = dir;
               bestDist = dist;
            }
         }

         return bestdir;
      } else {
         return null;
      }
   }

   private static class_243 getPosForDir(class_2338 blockPos, class_2350 dir) {
      class_243 offset = new class_243((double)dir.method_10148() / 2.0D, (double)dir.method_10164() / 2.0D, (double)dir.method_10165() / 2.0D);
      return blockPos.method_46558().method_1019(offset);
   }
}
