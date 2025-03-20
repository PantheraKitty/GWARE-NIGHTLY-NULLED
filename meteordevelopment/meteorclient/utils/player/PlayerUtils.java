package meteordevelopment.meteorclient.utils.player;

import java.util.Iterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.NoFall;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.misc.text.TextUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1802;
import net.minecraft.class_1812;
import net.minecraft.class_1934;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_2587;
import net.minecraft.class_2680;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_640;
import net.minecraft.class_9334;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class PlayerUtils {
   private static final double diagonal = 1.0D / Math.sqrt(2.0D);
   private static final class_243 horizontalVelocity = new class_243(0.0D, 0.0D, 0.0D);
   private static final Color color = new Color();

   private PlayerUtils() {
   }

   public static Color getPlayerColor(class_1657 entity, Color defaultColor) {
      if (Friends.get().isFriend(entity)) {
         return color.set((Color)Config.get().friendColor.get()).a(defaultColor.a);
      } else if (Friends.get().isEnemy(entity)) {
         return color.set((Color)Config.get().enemyColor.get()).a(defaultColor.a);
      } else {
         return (Boolean)Config.get().useTeamColor.get() && !color.set(TextUtils.getMostPopularColor(entity.method_5476())).equals(Utils.WHITE) ? color.a(defaultColor.a) : defaultColor;
      }
   }

   public static class_243 getHorizontalVelocity(double bps) {
      float yaw = MeteorClient.mc.field_1724.method_36454();
      if (PathManagers.get().isPathing()) {
         yaw = PathManagers.get().getTargetYaw();
      }

      class_243 forward = class_243.method_1030(0.0F, yaw);
      class_243 right = class_243.method_1030(0.0F, yaw + 90.0F);
      double velX = 0.0D;
      double velZ = 0.0D;
      boolean a = false;
      if (MeteorClient.mc.field_1724.field_3913.field_3910) {
         velX += forward.field_1352 / 20.0D * bps;
         velZ += forward.field_1350 / 20.0D * bps;
         a = true;
      }

      if (MeteorClient.mc.field_1724.field_3913.field_3909) {
         velX -= forward.field_1352 / 20.0D * bps;
         velZ -= forward.field_1350 / 20.0D * bps;
         a = true;
      }

      boolean b = false;
      if (MeteorClient.mc.field_1724.field_3913.field_3906) {
         velX += right.field_1352 / 20.0D * bps;
         velZ += right.field_1350 / 20.0D * bps;
         b = true;
      }

      if (MeteorClient.mc.field_1724.field_3913.field_3908) {
         velX -= right.field_1352 / 20.0D * bps;
         velZ -= right.field_1350 / 20.0D * bps;
         b = true;
      }

      if (a && b) {
         velX *= diagonal;
         velZ *= diagonal;
      }

      ((IVec3d)horizontalVelocity).setXZ(velX, velZ);
      return horizontalVelocity;
   }

   public static void centerPlayer() {
      double x = (double)class_3532.method_15357(MeteorClient.mc.field_1724.method_23317()) + 0.5D;
      double z = (double)class_3532.method_15357(MeteorClient.mc.field_1724.method_23321()) + 0.5D;
      MeteorClient.mc.field_1724.method_5814(x, MeteorClient.mc.field_1724.method_23318(), z);
      MeteorClient.mc.field_1724.field_3944.method_52787(new class_2829(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318(), MeteorClient.mc.field_1724.method_23321(), MeteorClient.mc.field_1724.method_24828()));
   }

   public static boolean canSeeEntity(class_1297 entity) {
      class_243 vec1 = new class_243(0.0D, 0.0D, 0.0D);
      class_243 vec2 = new class_243(0.0D, 0.0D, 0.0D);
      ((IVec3d)vec1).set(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318() + (double)MeteorClient.mc.field_1724.method_5751(), MeteorClient.mc.field_1724.method_23321());
      ((IVec3d)vec2).set(entity.method_23317(), entity.method_23318(), entity.method_23321());
      boolean canSeeFeet = MeteorClient.mc.field_1687.method_17742(new class_3959(vec1, vec2, class_3960.field_17558, class_242.field_1348, MeteorClient.mc.field_1724)).method_17783() == class_240.field_1333;
      ((IVec3d)vec2).set(entity.method_23317(), entity.method_23318() + (double)entity.method_5751(), entity.method_23321());
      boolean canSeeEyes = MeteorClient.mc.field_1687.method_17742(new class_3959(vec1, vec2, class_3960.field_17558, class_242.field_1348, MeteorClient.mc.field_1724)).method_17783() == class_240.field_1333;
      return canSeeFeet || canSeeEyes;
   }

   public static float[] calculateAngle(class_243 target) {
      class_243 eyesPos = new class_243(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318() + (double)MeteorClient.mc.field_1724.method_18381(MeteorClient.mc.field_1724.method_18376()), MeteorClient.mc.field_1724.method_23321());
      double dX = target.field_1352 - eyesPos.field_1352;
      double dY = (target.field_1351 - eyesPos.field_1351) * -1.0D;
      double dZ = target.field_1350 - eyesPos.field_1350;
      double dist = Math.sqrt(dX * dX + dZ * dZ);
      return new float[]{(float)class_3532.method_15338(Math.toDegrees(Math.atan2(dZ, dX)) - 90.0D), (float)class_3532.method_15338(Math.toDegrees(Math.atan2(dY, dist)))};
   }

   public static boolean shouldPause(boolean ifBreaking, boolean ifEating, boolean ifDrinking) {
      if (ifBreaking && MeteorClient.mc.field_1761.method_2923()) {
         return true;
      } else if (!ifEating || !MeteorClient.mc.field_1724.method_6115() || !MeteorClient.mc.field_1724.method_6047().method_7909().method_57347().method_57832(class_9334.field_50075) && !MeteorClient.mc.field_1724.method_6079().method_7909().method_57347().method_57832(class_9334.field_50075)) {
         return ifDrinking && MeteorClient.mc.field_1724.method_6115() && (MeteorClient.mc.field_1724.method_6047().method_7909() instanceof class_1812 || MeteorClient.mc.field_1724.method_6079().method_7909() instanceof class_1812);
      } else {
         return true;
      }
   }

   public static boolean isMoving() {
      return MeteorClient.mc.field_1724.field_6250 != 0.0F || MeteorClient.mc.field_1724.field_6212 != 0.0F;
   }

   public static boolean isSprinting() {
      return MeteorClient.mc.field_1724.method_5624() && (MeteorClient.mc.field_1724.field_6250 != 0.0F || MeteorClient.mc.field_1724.field_6212 != 0.0F);
   }

   public static boolean isInHole(boolean doubles) {
      if (!Utils.canUpdate()) {
         return false;
      } else {
         class_2338 blockPos = MeteorClient.mc.field_1724.method_24515();
         int air = 0;
         class_2350[] var3 = class_2350.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            class_2350 direction = var3[var5];
            if (direction != class_2350.field_11036) {
               class_2680 state = MeteorClient.mc.field_1687.method_8320(blockPos.method_10093(direction));
               if (state.method_26204().method_9520() < 600.0F) {
                  if (!doubles || direction == class_2350.field_11033) {
                     return false;
                  }

                  ++air;
                  class_2350[] var8 = class_2350.values();
                  int var9 = var8.length;

                  for(int var10 = 0; var10 < var9; ++var10) {
                     class_2350 dir = var8[var10];
                     if (dir != direction.method_10153() && dir != class_2350.field_11036) {
                        class_2680 blockState1 = MeteorClient.mc.field_1687.method_8320(blockPos.method_10093(direction).method_10093(dir));
                        if (blockState1.method_26204().method_9520() < 600.0F) {
                           return false;
                        }
                     }
                  }
               }
            }
         }

         return air < 2;
      }
   }

   public static float possibleHealthReductions() {
      return possibleHealthReductions(true, true);
   }

   public static float possibleHealthReductions(boolean entities, boolean fall) {
      float damageTaken = 0.0F;
      if (entities) {
         Iterator var3 = MeteorClient.mc.field_1687.method_18112().iterator();

         while(var3.hasNext()) {
            class_1297 entity = (class_1297)var3.next();
            float attackDamage;
            if (entity instanceof class_1511) {
               attackDamage = DamageUtils.crystalDamage(MeteorClient.mc.field_1724, entity.method_19538());
               if (attackDamage > damageTaken) {
                  damageTaken = attackDamage;
               }
            } else if (entity instanceof class_1657) {
               class_1657 player = (class_1657)entity;
               if (!Friends.get().isFriend(player) && isWithin(entity, 5.0D)) {
                  attackDamage = DamageUtils.getAttackDamage(player, MeteorClient.mc.field_1724);
                  if (attackDamage > damageTaken) {
                     damageTaken = attackDamage;
                  }
               }
            }
         }

         if (getDimension() != Dimension.Overworld) {
            var3 = Utils.blockEntities().iterator();

            while(var3.hasNext()) {
               class_2586 blockEntity = (class_2586)var3.next();
               class_2338 bp = blockEntity.method_11016();
               class_243 pos = new class_243((double)bp.method_10263(), (double)bp.method_10264(), (double)bp.method_10260());
               if (blockEntity instanceof class_2587) {
                  float explosionDamage = DamageUtils.bedDamage(MeteorClient.mc.field_1724, pos);
                  if (explosionDamage > damageTaken) {
                     damageTaken = explosionDamage;
                  }
               }
            }
         }
      }

      if (fall && !Modules.get().isActive(NoFall.class) && MeteorClient.mc.field_1724.field_6017 > 3.0F) {
         float damage = DamageUtils.fallDamage(MeteorClient.mc.field_1724);
         if (damage > damageTaken && !EntityUtils.isAboveWater(MeteorClient.mc.field_1724)) {
            damageTaken = damage;
         }
      }

      return damageTaken;
   }

   public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
      return Math.sqrt(squaredDistance(x1, y1, z1, x2, y2, z2));
   }

   public static double distanceTo(class_1297 entity) {
      return distanceTo(entity.method_23317(), entity.method_23318(), entity.method_23321());
   }

   public static double distanceTo(class_2338 blockPos) {
      return distanceTo((double)blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260());
   }

   public static double distanceTo(class_243 vec3d) {
      return distanceTo(vec3d.method_10216(), vec3d.method_10214(), vec3d.method_10215());
   }

   public static double distanceTo(double x, double y, double z) {
      return Math.sqrt(squaredDistanceTo(x, y, z));
   }

   public static double squaredDistanceTo(class_1297 entity) {
      return squaredDistanceTo(entity.method_23317(), entity.method_23318(), entity.method_23321());
   }

   public static double squaredDistanceTo(class_2338 blockPos) {
      return squaredDistanceTo((double)blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260());
   }

   public static double squaredDistanceTo(double x, double y, double z) {
      return squaredDistance(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318(), MeteorClient.mc.field_1724.method_23321(), x, y, z);
   }

   public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
      double f = x1 - x2;
      double g = y1 - y2;
      double h = z1 - z2;
      return org.joml.Math.fma(f, f, org.joml.Math.fma(g, g, h * h));
   }

   public static boolean isWithin(class_1297 entity, double r) {
      return squaredDistanceTo(entity.method_23317(), entity.method_23318(), entity.method_23321()) <= r * r;
   }

   public static boolean isWithin(class_243 vec3d, double r) {
      return squaredDistanceTo(vec3d.method_10216(), vec3d.method_10214(), vec3d.method_10215()) <= r * r;
   }

   public static boolean isWithin(class_2338 blockPos, double r) {
      return squaredDistanceTo((double)blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260()) <= r * r;
   }

   public static boolean isWithin(double x, double y, double z, double r) {
      return squaredDistanceTo(x, y, z) <= r * r;
   }

   public static double distanceToCamera(double x, double y, double z) {
      return Math.sqrt(squaredDistanceToCamera(x, y, z));
   }

   public static double distanceToCamera(class_1297 entity) {
      return distanceToCamera(entity.method_23317(), entity.method_23318() + (double)entity.method_18381(entity.method_18376()), entity.method_23321());
   }

   public static double squaredDistanceToCamera(double x, double y, double z) {
      class_243 cameraPos = MeteorClient.mc.field_1773.method_19418().method_19326();
      return squaredDistance(cameraPos.field_1352, cameraPos.field_1351, cameraPos.field_1350, x, y, z);
   }

   public static double squaredDistanceToCamera(class_1297 entity) {
      return squaredDistanceToCamera(entity.method_23317(), entity.method_23318() + (double)entity.method_18381(entity.method_18376()), entity.method_23321());
   }

   public static boolean isWithinCamera(class_1297 entity, double r) {
      return squaredDistanceToCamera(entity.method_23317(), entity.method_23318(), entity.method_23321()) <= r * r;
   }

   public static boolean isWithinCamera(class_243 vec3d, double r) {
      return squaredDistanceToCamera(vec3d.method_10216(), vec3d.method_10214(), vec3d.method_10215()) <= r * r;
   }

   public static boolean isWithinCamera(class_2338 blockPos, double r) {
      return squaredDistanceToCamera((double)blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260()) <= r * r;
   }

   public static boolean isWithinCamera(double x, double y, double z, double r) {
      return squaredDistanceToCamera(x, y, z) <= r * r;
   }

   public static boolean isWithinReach(class_1297 entity) {
      return isWithinReach(entity.method_23317(), entity.method_23318(), entity.method_23321());
   }

   public static boolean isWithinReach(class_243 vec3d) {
      return isWithinReach(vec3d.method_10216(), vec3d.method_10214(), vec3d.method_10215());
   }

   public static boolean isWithinReach(class_2338 blockPos) {
      return isWithinReach((double)blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260());
   }

   public static boolean isWithinReach(double x, double y, double z) {
      return squaredDistance(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23320(), MeteorClient.mc.field_1724.method_23321(), x, y, z) <= MeteorClient.mc.field_1724.method_55754() * MeteorClient.mc.field_1724.method_55754();
   }

   public static Dimension getDimension() {
      if (MeteorClient.mc.field_1687 == null) {
         return Dimension.Overworld;
      } else {
         String var0 = MeteorClient.mc.field_1687.method_27983().method_29177().method_12832();
         byte var1 = -1;
         switch(var0.hashCode()) {
         case -1350117363:
            if (var0.equals("the_end")) {
               var1 = 1;
            }
            break;
         case 1272296422:
            if (var0.equals("the_nether")) {
               var1 = 0;
            }
         }

         Dimension var10000;
         switch(var1) {
         case 0:
            var10000 = Dimension.Nether;
            break;
         case 1:
            var10000 = Dimension.End;
            break;
         default:
            var10000 = Dimension.Overworld;
         }

         return var10000;
      }
   }

   public static class_1934 getGameMode() {
      if (MeteorClient.mc.field_1724 == null) {
         return null;
      } else {
         class_640 playerListEntry = MeteorClient.mc.method_1562().method_2871(MeteorClient.mc.field_1724.method_5667());
         return playerListEntry == null ? null : playerListEntry.method_2958();
      }
   }

   public static float getTotalHealth() {
      return MeteorClient.mc.field_1724.method_6032() + MeteorClient.mc.field_1724.method_6067();
   }

   public static boolean isAlive() {
      return MeteorClient.mc.field_1724.method_5805() && !MeteorClient.mc.field_1724.method_29504();
   }

   public static int getPing() {
      if (MeteorClient.mc.method_1562() == null) {
         return 0;
      } else {
         class_640 playerListEntry = MeteorClient.mc.method_1562().method_2871(MeteorClient.mc.field_1724.method_5667());
         return playerListEntry == null ? 0 : playerListEntry.method_2959();
      }
   }

   public static class_243 movementInputToVelocity(class_243 movementInput, float speed, float yaw) {
      double d = movementInput.method_1027();
      if (d < 1.0E-7D) {
         return class_243.field_1353;
      } else {
         class_243 vec3d = (d > 1.0D ? movementInput.method_1029() : movementInput).method_1021((double)speed);
         float f = class_3532.method_15374(yaw * 0.017453292F);
         float g = class_3532.method_15362(yaw * 0.017453292F);
         return new class_243(vec3d.field_1352 * (double)g - vec3d.field_1350 * (double)f, vec3d.field_1351, vec3d.field_1350 * (double)g + vec3d.field_1352 * (double)f);
      }
   }

   public static boolean silentSwapEquipChestplate() {
      if (!MeteorClient.mc.field_1724.method_6118(class_1304.field_6174).method_7909().equals(class_1802.field_8058) && !MeteorClient.mc.field_1724.method_6118(class_1304.field_6174).method_7909().equals(class_1802.field_22028)) {
         FindItemResult hotbarChestplateSlot = InvUtils.findInHotbar(class_1802.field_22028);
         if (!hotbarChestplateSlot.found()) {
            hotbarChestplateSlot = InvUtils.findInHotbar(class_1802.field_8058);
         }

         if (hotbarChestplateSlot.found()) {
            MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7498.field_7763, 6, hotbarChestplateSlot.slot(), class_1713.field_7791, MeteorClient.mc.field_1724);
            return true;
         } else {
            FindItemResult inventorySlot = InvUtils.find(class_1802.field_22028);
            if (!inventorySlot.found()) {
               inventorySlot = InvUtils.find(class_1802.field_8058);
            }

            if (!inventorySlot.found()) {
               return false;
            } else {
               FindItemResult hotbarSlot = InvUtils.findInHotbar((x) -> {
                  return x.method_7909() != class_1802.field_8288;
               });
               MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7498.field_7763, inventorySlot.slot(), hotbarSlot.found() ? hotbarSlot.slot() : 0, class_1713.field_7791, MeteorClient.mc.field_1724);
               MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7498.field_7763, 6, hotbarSlot.found() ? hotbarSlot.slot() : 0, class_1713.field_7791, MeteorClient.mc.field_1724);
               MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7498.field_7763, inventorySlot.slot(), hotbarSlot.found() ? hotbarSlot.slot() : 0, class_1713.field_7791, MeteorClient.mc.field_1724);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean silentSwapEquipElytra() {
      if (MeteorClient.mc.field_1724.method_6118(class_1304.field_6174).method_7909().equals(class_1802.field_8833)) {
         return false;
      } else {
         FindItemResult inventorySlot = InvUtils.findInHotbar(class_1802.field_8833);
         if (inventorySlot.found()) {
            MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7498.field_7763, 6, inventorySlot.slot(), class_1713.field_7791, MeteorClient.mc.field_1724);
            return true;
         } else {
            inventorySlot = InvUtils.find(class_1802.field_8833);
            if (!inventorySlot.found()) {
               return false;
            } else {
               FindItemResult hotbarSlot = InvUtils.findInHotbar((x) -> {
                  return x.method_7909() != class_1802.field_8288;
               });
               MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7498.field_7763, inventorySlot.slot(), hotbarSlot.found() ? hotbarSlot.slot() : 0, class_1713.field_7791, MeteorClient.mc.field_1724);
               MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7498.field_7763, 6, hotbarSlot.found() ? hotbarSlot.slot() : 0, class_1713.field_7791, MeteorClient.mc.field_1724);
               MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7498.field_7763, inventorySlot.slot(), hotbarSlot.found() ? hotbarSlot.slot() : 0, class_1713.field_7791, MeteorClient.mc.field_1724);
               return true;
            }
         }
      }
   }

   public static boolean isPlayerPhased() {
      return MeteorClient.mc.field_1687.method_20812(MeteorClient.mc.field_1724, MeteorClient.mc.field_1724.method_5829()).iterator().hasNext();
   }

   public static boolean isPlayerPhased(class_1657 player) {
      return MeteorClient.mc.field_1687.method_20812(player, player.method_5829()).iterator().hasNext();
   }
}
