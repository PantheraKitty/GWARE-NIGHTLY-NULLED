package meteordevelopment.meteorclient.utils.entity;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.EntityTrackingSectionAccessor;
import meteordevelopment.meteorclient.mixin.SectionedEntityCacheAccessor;
import meteordevelopment.meteorclient.mixin.SimpleEntityLookupAccessor;
import meteordevelopment.meteorclient.mixin.WorldAccessor;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1934;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_3611;
import net.minecraft.class_3612;
import net.minecraft.class_4076;
import net.minecraft.class_5572;
import net.minecraft.class_5573;
import net.minecraft.class_5577;
import net.minecraft.class_5578;
import net.minecraft.class_640;
import net.minecraft.class_2338.class_2339;

public class EntityUtils {
   private static class_2339 testPos = new class_2339();

   private EntityUtils() {
   }

   public static boolean isAttackable(class_1299<?> type) {
      return type != class_1299.field_6083 && type != class_1299.field_6122 && type != class_1299.field_6089 && type != class_1299.field_6133 && type != class_1299.field_6052 && type != class_1299.field_6124 && type != class_1299.field_6135 && type != class_1299.field_6082 && type != class_1299.field_6064 && type != class_1299.field_6045 && type != class_1299.field_6127 && type != class_1299.field_6112 && type != class_1299.field_6103 && type != class_1299.field_6044 && type != class_1299.field_6144;
   }

   public static boolean isRideable(class_1299<?> type) {
      return type == class_1299.field_6096 || type == class_1299.field_6121 || type == class_1299.field_40116 || type == class_1299.field_6067 || type == class_1299.field_6139 || type == class_1299.field_6074 || type == class_1299.field_6057 || type == class_1299.field_6093 || type == class_1299.field_6075 || type == class_1299.field_23214 || type == class_1299.field_6048;
   }

   public static float getTotalHealth(class_1309 target) {
      return target.method_6032() + target.method_6067();
   }

   public static int getPing(class_1657 player) {
      if (MeteorClient.mc.method_1562() == null) {
         return 0;
      } else {
         class_640 playerListEntry = MeteorClient.mc.method_1562().method_2871(player.method_5667());
         return playerListEntry == null ? 0 : playerListEntry.method_2959();
      }
   }

   public static class_1934 getGameMode(class_1657 player) {
      if (player == null) {
         return null;
      } else {
         class_640 playerListEntry = MeteorClient.mc.method_1562().method_2871(player.method_5667());
         return playerListEntry == null ? null : playerListEntry.method_2958();
      }
   }

   public static boolean isAboveWater(class_1297 entity) {
      class_2339 blockPos = entity.method_24515().method_25503();
      int i = 0;

      while(true) {
         if (i < 64) {
            class_2680 state = MeteorClient.mc.field_1687.method_8320(blockPos);
            if (!state.method_51366()) {
               class_3611 fluid = state.method_26227().method_15772();
               if (fluid != class_3612.field_15910 && fluid != class_3612.field_15909) {
                  blockPos.method_10100(0, -1, 0);
                  ++i;
                  continue;
               }

               return true;
            }
         }

         return false;
      }
   }

   public static boolean isInRenderDistance(class_1297 entity) {
      return entity == null ? false : isInRenderDistance(entity.method_23317(), entity.method_23321());
   }

   public static boolean isInRenderDistance(class_2586 entity) {
      return entity == null ? false : isInRenderDistance((double)entity.method_11016().method_10263(), (double)entity.method_11016().method_10260());
   }

   public static boolean isInRenderDistance(class_2338 pos) {
      return pos == null ? false : isInRenderDistance((double)pos.method_10263(), (double)pos.method_10260());
   }

   public static boolean isInRenderDistance(double posX, double posZ) {
      double x = Math.abs(MeteorClient.mc.field_1773.method_19418().method_19326().field_1352 - posX);
      double z = Math.abs(MeteorClient.mc.field_1773.method_19418().method_19326().field_1350 - posZ);
      double d = (double)(((Integer)MeteorClient.mc.field_1690.method_42503().method_41753() + 1) * 16);
      return x < d && z < d;
   }

   public static class_2338 getCityBlock(class_1657 self, class_1657 player, class_2338 excludeBlockPos) {
      if (player == null) {
         return null;
      } else {
         double bestDistanceSquared = 36.0D;
         class_2350 bestDirection = null;
         class_2350[] var6 = class_2350.field_11041;
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            class_2350 direction = var6[var8];
            testPos.method_10101(player.method_24515().method_10093(direction));
            if (excludeBlockPos == null || !testPos.equals(excludeBlockPos)) {
               class_2248 block = MeteorClient.mc.field_1687.method_8320(testPos).method_26204();
               if (block != class_2246.field_10540 && block != class_2246.field_22108 && block != class_2246.field_22423 && block != class_2246.field_23152 && block != class_2246.field_22109) {
                  if (block == class_2246.field_10124 && MeteorClient.mc.field_1687.method_8320(player.method_24515()).method_26204() == class_2246.field_10540) {
                     return player.method_24515();
                  }
               } else {
                  double testDistanceSquared = PlayerUtils.squaredDistanceTo((class_2338)testPos);
                  class_2350[] var13 = class_2350.field_11041;
                  int var14 = var13.length;

                  for(int var15 = 0; var15 < var14; ++var15) {
                     class_2350 direction2 = var13[var15];
                     class_2338 selfBlockPos = self.method_24515().method_10093(direction2);
                     if (selfBlockPos.equals(testPos)) {
                        testDistanceSquared += 2.0D;
                     }
                  }

                  if (testDistanceSquared < bestDistanceSquared) {
                     bestDistanceSquared = testDistanceSquared;
                     bestDirection = direction;
                  }
               }
            }
         }

         if (bestDirection == null) {
            return null;
         } else {
            return player.method_24515().method_10093(bestDirection);
         }
      }
   }

   public static String getName(class_1297 entity) {
      if (entity == null) {
         return null;
      } else {
         return entity instanceof class_1657 ? entity.method_5477().getString() : entity.method_5864().method_5897().getString();
      }
   }

   public static Color getColorFromDistance(class_1297 entity) {
      Color distanceColor = new Color(255, 255, 255);
      double distance = PlayerUtils.distanceToCamera(entity);
      double percent = distance / 60.0D;
      if (!(percent < 0.0D) && !(percent > 1.0D)) {
         int r;
         int g;
         if (percent < 0.5D) {
            r = 255;
            g = (int)(255.0D * percent / 0.5D);
         } else {
            g = 255;
            r = 255 - (int)(255.0D * (percent - 0.5D) / 0.5D);
         }

         distanceColor.set(r, g, 0, 255);
         return distanceColor;
      } else {
         distanceColor.set(0, 255, 0, 255);
         return distanceColor;
      }
   }

   public static boolean intersectsWithEntity(class_238 box, Predicate<class_1297> predicate) {
      class_5577<class_1297> entityLookup = ((WorldAccessor)MeteorClient.mc.field_1687).getEntityLookup();
      if (!(entityLookup instanceof class_5578)) {
         AtomicBoolean found = new AtomicBoolean(false);
         entityLookup.method_31807(box, (entityx) -> {
            if (!found.get() && predicate.test(entityx)) {
               found.set(true);
            }

         });
         return found.get();
      } else {
         class_5578<class_1297> simpleEntityLookup = (class_5578)entityLookup;
         class_5573<class_1297> cache = ((SimpleEntityLookupAccessor)simpleEntityLookup).getCache();
         LongSortedSet trackedPositions = ((SectionedEntityCacheAccessor)cache).getTrackedPositions();
         Long2ObjectMap<class_5572<class_1297>> trackingSections = ((SectionedEntityCacheAccessor)cache).getTrackingSections();
         int i = class_4076.method_32204(box.field_1323 - 2.0D);
         int j = class_4076.method_32204(box.field_1322 - 2.0D);
         int k = class_4076.method_32204(box.field_1321 - 2.0D);
         int l = class_4076.method_32204(box.field_1320 + 2.0D);
         int m = class_4076.method_32204(box.field_1325 + 2.0D);
         int n = class_4076.method_32204(box.field_1324 + 2.0D);

         label66:
         for(int o = i; o <= l; ++o) {
            long p = class_4076.method_18685(o, 0, 0);
            long q = class_4076.method_18685(o, -1, -1);
            LongBidirectionalIterator longIterator = trackedPositions.subSet(p, q + 1L).iterator();

            while(true) {
               class_5572 entityTrackingSection;
               do {
                  do {
                     long r;
                     int t;
                     do {
                        int s;
                        do {
                           do {
                              do {
                                 if (!longIterator.hasNext()) {
                                    continue label66;
                                 }

                                 r = longIterator.nextLong();
                                 s = class_4076.method_18689(r);
                                 t = class_4076.method_18690(r);
                              } while(s < j);
                           } while(s > m);
                        } while(t < k);
                     } while(t > n);

                     entityTrackingSection = (class_5572)trackingSections.get(r);
                  } while(entityTrackingSection == null);
               } while(!entityTrackingSection.method_31768().method_31885());

               Iterator var24 = ((EntityTrackingSectionAccessor)entityTrackingSection).getCollection().iterator();

               while(var24.hasNext()) {
                  class_1297 entity = (class_1297)var24.next();
                  if (entity.method_5829().method_994(box) && predicate.test(entity)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   public static class_1299<?> getGroup(class_1297 entity) {
      return entity.method_5864();
   }
}
