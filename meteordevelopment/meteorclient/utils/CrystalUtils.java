package meteordevelopment.meteorclient.utils;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public class CrystalUtils {
   public static double calculateCrystalDamage(class_243 crystalPos, class_1657 player) {
      if (MeteorClient.mc.field_1687 != null && player != null) {
         double distance = Math.sqrt(player.method_5707(crystalPos));
         if (distance > 12.0D) {
            return 0.0D;
         } else {
            double explosionPower = 6.0D;
            double impact = (1.0D - distance / 12.0D) * (double)getExposure(crystalPos, player);
            double damage = (impact * impact + impact) / 2.0D * 7.0D * explosionPower + 1.0D;
            damage = class_3532.method_15350(damage, 0.0D, 36.0D);
            return applyDamageReduction(damage, player);
         }
      } else {
         return 0.0D;
      }
   }

   private static float getExposure(class_243 source, class_1657 player) {
      class_238 box = player.method_5829();
      double stepX = 1.0D / ((box.field_1320 - box.field_1323) * 2.0D + 1.0D);
      double stepY = 1.0D / ((box.field_1325 - box.field_1322) * 2.0D + 1.0D);
      double stepZ = 1.0D / ((box.field_1324 - box.field_1321) * 2.0D + 1.0D);
      if (!(stepX <= 0.0D) && !(stepY <= 0.0D) && !(stepZ <= 0.0D)) {
         int hits = 0;
         int total = 0;

         for(double x = 0.0D; x <= 1.0D; x += stepX) {
            for(double y = 0.0D; y <= 1.0D; y += stepY) {
               for(double z = 0.0D; z <= 1.0D; z += stepZ) {
                  double pointX = class_3532.method_16436(x, box.field_1323, box.field_1320);
                  double pointY = class_3532.method_16436(y, box.field_1322, box.field_1325);
                  double pointZ = class_3532.method_16436(z, box.field_1321, box.field_1324);
                  class_243 point = new class_243(pointX, pointY, pointZ);
                  if (!isOccluded(source, point)) {
                     ++hits;
                  }

                  ++total;
               }
            }
         }

         return (float)hits / (float)total;
      } else {
         return 0.0F;
      }
   }

   private static boolean isOccluded(class_243 start, class_243 end) {
      class_2338 startPos = new class_2338((int)start.field_1352, (int)start.field_1351, (int)start.field_1350);
      class_2338 endPos = new class_2338((int)end.field_1352, (int)end.field_1351, (int)end.field_1350);
      if (!startPos.equals(endPos)) {
         class_243 direction = end.method_1020(start).method_1029();
         double distance = start.method_1022(end);
         double step = 0.1D;

         for(double d = 0.0D; d <= distance; d += step) {
            class_243 point = start.method_1019(direction.method_1021(d));
            class_2338 pos = new class_2338((int)point.field_1352, (int)point.field_1351, (int)point.field_1350);
            if (MeteorClient.mc.field_1687.method_8320(pos).method_26225()) {
               return true;
            }
         }
      }

      return false;
   }

   private static double applyDamageReduction(double damage, class_1657 player) {
      double armorValue = (double)player.method_6096();
      double toughness = 2.0D;
      double reductionFactor = armorValue * (1.0D - damage / (damage + toughness + 8.0D));
      return Math.max(damage - reductionFactor / 2.0D, damage * 0.2D);
   }
}
