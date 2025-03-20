package meteordevelopment.meteorclient.utils.entity;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.mixininterface.IExplosion;
import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1280;
import net.minecraft.class_1282;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1887;
import net.minecraft.class_1893;
import net.minecraft.class_1922;
import net.minecraft.class_1927;
import net.minecraft.class_1934;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3483;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_5134;
import net.minecraft.class_6880;
import net.minecraft.class_8103;
import net.minecraft.class_9285;
import net.minecraft.class_9334;
import net.minecraft.class_9362;
import net.minecraft.class_1927.class_4179;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2902.class_2903;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class DamageUtils {
   private static final class_243 vec3d = new class_243(0.0D, 0.0D, 0.0D);
   private static class_1927 explosion;
   public static class_3959 raycastContext;
   public static class_3959 bedRaycast;
   public static final DamageUtils.RaycastFactory HIT_FACTORY = (context, blockPos) -> {
      class_2680 blockState = MeteorClient.mc.field_1687.method_8320(blockPos);
      return blockState.method_26204().method_9520() < 600.0F ? null : blockState.method_26220(MeteorClient.mc.field_1687, blockPos).method_1092(context.start(), context.end(), blockPos);
   };

   private DamageUtils() {
   }

   @PreInit
   public static void init() {
      MeteorClient.EVENT_BUS.subscribe(DamageUtils.class);
   }

   @EventHandler
   private static void onGameJoined(GameJoinedEvent event) {
      explosion = new class_1927(MeteorClient.mc.field_1687, (class_1297)null, 0.0D, 0.0D, 0.0D, 6.0F, false, class_4179.field_18687);
      raycastContext = new class_3959((class_243)null, (class_243)null, class_3960.field_17558, class_242.field_1347, MeteorClient.mc.field_1724);
      bedRaycast = new class_3959((class_243)null, (class_243)null, class_3960.field_17558, class_242.field_1347, MeteorClient.mc.field_1724);
   }

   public static float crystalDamage(class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, DamageUtils.RaycastFactory raycastFactory) {
      return explosionDamage(target, targetPos, targetBox, explosionPos, 12.0F, raycastFactory);
   }

   public static float bedDamage(class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, DamageUtils.RaycastFactory raycastFactory) {
      return explosionDamage(target, targetPos, targetBox, explosionPos, 10.0F, raycastFactory);
   }

   public static float anchorDamage(class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, DamageUtils.RaycastFactory raycastFactory) {
      return explosionDamage(target, targetPos, targetBox, explosionPos, 10.0F, raycastFactory);
   }

   public static float explosionDamage(class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, float power, DamageUtils.RaycastFactory raycastFactory) {
      double modDistance = PlayerUtils.distance(targetPos.field_1352, targetPos.field_1351, targetPos.field_1350, explosionPos.field_1352, explosionPos.field_1351, explosionPos.field_1350);
      if (modDistance > (double)power) {
         return 0.0F;
      } else {
         double exposure = (double)getExposure(explosionPos, targetBox, raycastFactory);
         double impact = (1.0D - modDistance / (double)power) * exposure;
         float damage = (float)((int)((impact * impact + impact) / 2.0D * 7.0D * 12.0D + 1.0D));
         return calculateReductions(damage, target, MeteorClient.mc.field_1687.method_48963().method_48807((class_1927)null));
      }
   }

   public static float crystalDamage(class_1309 target, class_243 crystal, boolean predictMovement, class_2338 obsidianPos) {
      return overridingExplosionDamage(target, crystal, 12.0F, predictMovement, obsidianPos, class_2246.field_10540.method_9564());
   }

   public static float crystalDamage(class_1309 target, class_243 crystal) {
      return explosionDamage(target, crystal, 12.0F, false);
   }

   public static float bedDamage(class_1309 target, class_243 bed) {
      return explosionDamage(target, bed, 10.0F, false);
   }

   public static float anchorDamage(class_1309 target, class_243 anchor) {
      return overridingExplosionDamage(target, anchor, 10.0F, false, class_2338.method_49638(anchor), class_2246.field_10124.method_9564());
   }

   private static float overridingExplosionDamage(class_1309 target, class_243 explosionPos, float power, boolean predictMovement, class_2338 overridePos, class_2680 overrideState) {
      return explosionDamage(target, explosionPos, power, predictMovement, getOverridingHitFactory(overridePos, overrideState));
   }

   private static float explosionDamage(class_1309 target, class_243 explosionPos, float power, boolean predictMovement) {
      return explosionDamage(target, explosionPos, power, predictMovement, HIT_FACTORY);
   }

   private static float explosionDamage(class_1309 target, class_243 explosionPos, float power, boolean predictMovement, DamageUtils.RaycastFactory raycastFactory) {
      if (target == null) {
         return 0.0F;
      } else {
         if (target instanceof class_1657) {
            class_1657 player = (class_1657)target;
            if (EntityUtils.getGameMode(player) == class_1934.field_9220 && !(player instanceof FakePlayerEntity)) {
               return 0.0F;
            }
         }

         class_243 position = predictMovement ? target.method_19538().method_1019(target.method_18798()) : target.method_19538();
         class_238 box = target.method_5829();
         if (predictMovement) {
            box = box.method_997(target.method_18798());
         }

         return explosionDamage(target, position, box, explosionPos, power, raycastFactory);
      }
   }

   public static DamageUtils.RaycastFactory getOverridingHitFactory(class_2338 overridePos, class_2680 overrideState) {
      return (context, blockPos) -> {
         class_2680 blockState;
         if (blockPos.equals(overridePos)) {
            blockState = overrideState;
         } else {
            blockState = MeteorClient.mc.field_1687.method_8320(blockPos);
            if (blockState.method_26204().method_9520() < 600.0F) {
               return null;
            }
         }

         return blockState.method_26220(MeteorClient.mc.field_1687, blockPos).method_1092(context.start(), context.end(), blockPos);
      };
   }

   public static double newCrystalDamage(class_1657 player, class_238 boundingBox, class_243 crystal, Set<class_2338> ignorePos) {
      if (player == null) {
         return 0.0D;
      } else if (EntityUtils.getGameMode(player) == class_1934.field_9220 && !(player instanceof FakePlayerEntity)) {
         return 0.0D;
      } else {
         if (ignorePos != null && ignorePos.isEmpty()) {
            ignorePos = null;
         }

         ((IVec3d)vec3d).set((boundingBox.field_1323 + boundingBox.field_1320) / 2.0D, boundingBox.field_1322, (boundingBox.field_1321 + boundingBox.field_1324) / 2.0D);
         double modDistance = Math.sqrt(vec3d.method_1025(crystal));
         if (modDistance > 12.0D) {
            return 0.0D;
         } else {
            double exposure = getExposure(crystal, player, boundingBox, raycastContext, ignorePos);
            double impact = (1.0D - modDistance / 12.0D) * exposure;
            double damage = (impact * impact + impact) / 2.0D * 7.0D * 12.0D + 1.0D;
            damage = getDamageForDifficulty(damage);
            damage = (double)class_1280.method_5496(player, (float)damage, MeteorClient.mc.field_1687.method_48963().method_48807((class_1927)null), (float)player.method_6096(), (float)player.method_5996(class_5134.field_23725).method_6194());
            damage = resistanceReduction(player, damage);
            ((IExplosion)explosion).set(crystal, 6.0F, false);
            damage = blastProtReduction(player, damage, explosion);
            return damage < 0.0D ? 0.0D : damage;
         }
      }
   }

   public static double getExposure(class_243 source, class_1297 entity, class_238 box, class_3959 raycastContext, Set<class_2338> ignore) {
      double d = 1.0D / ((box.field_1320 - box.field_1323) * 2.0D + 1.0D);
      double e = 1.0D / ((box.field_1325 - box.field_1322) * 2.0D + 1.0D);
      double f = 1.0D / ((box.field_1324 - box.field_1321) * 2.0D + 1.0D);
      double g = (1.0D - Math.floor(1.0D / d) * d) / 2.0D;
      double h = (1.0D - Math.floor(1.0D / f) * f) / 2.0D;
      if (!(d < 0.0D) && !(e < 0.0D) && !(f < 0.0D)) {
         int i = 0;
         int j = 0;
         class_243 vec3d = new class_243(0.0D, 0.0D, 0.0D);

         for(double k = 0.0D; k <= 1.0D; k += d) {
            for(double l = 0.0D; l <= 1.0D; l += e) {
               for(double m = 0.0D; m <= 1.0D; m += f) {
                  double n = class_3532.method_16436(k, box.field_1323, box.field_1320);
                  double o = class_3532.method_16436(l, box.field_1322, box.field_1325);
                  double p = class_3532.method_16436(m, box.field_1321, box.field_1324);
                  ((IVec3d)vec3d).set(n + g, o, p + h);
                  ((IRaycastContext)raycastContext).set(vec3d, source, class_3960.field_17558, class_242.field_1348, entity);
                  if (raycast(raycastContext, ignore).method_17783() == class_240.field_1333) {
                     ++i;
                  }

                  ++j;
               }
            }
         }

         return (double)i / (double)j;
      } else {
         return 0.0D;
      }
   }

   public static class_3965 raycast(class_3959 context) {
      return (class_3965)class_1922.method_17744(context.method_17750(), context.method_17747(), context, (raycastContext, blockPos) -> {
         class_2680 blockState = MeteorClient.mc.field_1687.method_8320(blockPos);
         class_265 voxelShape = raycastContext.method_17748(blockState, MeteorClient.mc.field_1687, blockPos);
         class_3965 blockHitResult = MeteorClient.mc.field_1687.method_17745(raycastContext.method_17750(), raycastContext.method_17747(), blockPos, voxelShape, blockState);
         if (blockHitResult != null) {
            return blockHitResult;
         } else {
            class_265 voxelShape2 = class_259.method_1073();
            class_3965 blockHitResult2 = voxelShape2.method_1092(raycastContext.method_17750(), raycastContext.method_17747(), blockPos);
            return blockHitResult2 != null ? blockHitResult2 : class_3965.method_17778(raycastContext.method_17747(), class_2350.method_58251(raycastContext.method_17750().method_1020(raycastContext.method_17747())), class_2338.method_49638(raycastContext.method_17747()));
         }
      }, (raycastContext) -> {
         class_243 vec3d = raycastContext.method_17750().method_1020(raycastContext.method_17747());
         return class_3965.method_17778(raycastContext.method_17747(), class_2350.method_10142(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350), class_2338.method_49638(raycastContext.method_17747()));
      });
   }

   private static class_3965 raycast(class_3959 context, Set<class_2338> ignore) {
      return (class_3965)class_1922.method_17744(context.method_17750(), context.method_17747(), context, (raycastContext, blockPos) -> {
         class_2680 blockState;
         if (ignore != null && ignore.contains(blockPos)) {
            blockState = class_2246.field_10124.method_9564();
         } else {
            blockState = MeteorClient.mc.field_1687.method_8320(blockPos);
         }

         class_243 vec3d = raycastContext.method_17750();
         class_243 vec3d2 = raycastContext.method_17747();
         class_265 voxelShape = raycastContext.method_17748(blockState, MeteorClient.mc.field_1687, blockPos);
         class_3965 blockHitResult = MeteorClient.mc.field_1687.method_17745(vec3d, vec3d2, blockPos, voxelShape, blockState);
         class_265 voxelShape2 = class_259.method_1073();
         class_3965 blockHitResult2 = voxelShape2.method_1092(vec3d, vec3d2, blockPos);
         double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.method_17750().method_1025(blockHitResult.method_17784());
         double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.method_17750().method_1025(blockHitResult2.method_17784());
         return d <= e ? blockHitResult : blockHitResult2;
      }, (raycastContext) -> {
         class_243 vec3d = raycastContext.method_17750().method_1020(raycastContext.method_17747());
         return class_3965.method_17778(raycastContext.method_17747(), class_2350.method_10142(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350), class_2338.method_49638(raycastContext.method_17747()));
      });
   }

   private static double getDamageForDifficulty(double damage) {
      double var10000;
      switch(MeteorClient.mc.field_1687.method_8407()) {
      case field_5805:
         var10000 = Math.min(damage / 2.0D + 1.0D, damage);
         break;
      case field_5807:
      case field_5801:
         var10000 = damage * 3.0D / 2.0D;
         break;
      default:
         var10000 = damage;
      }

      return var10000;
   }

   private static double normalProtReduction(class_1309 player, double damage) {
      int protLevel = 10;
      if (protLevel > 20) {
         protLevel = 20;
      }

      damage *= 1.0D - (double)protLevel / 25.0D;
      return damage < 0.0D ? 0.0D : damage;
   }

   private static double blastProtReduction(class_1309 player, double damage, class_1927 explosion) {
      int protLevel = 10;
      if (protLevel > 20) {
         protLevel = 20;
      }

      damage *= 1.0D - (double)protLevel / 25.0D;
      return damage < 0.0D ? 0.0D : damage;
   }

   private static double resistanceReduction(class_1309 player, double damage) {
      if (player.method_6059(class_1294.field_5907)) {
         int lvl = player.method_6112(class_1294.field_5907).method_5578() + 1;
         damage *= 1.0D - (double)lvl * 0.2D;
      }

      return damage < 0.0D ? 0.0D : damage;
   }

   public static float getAttackDamage(class_1309 attacker, class_1309 target) {
      float itemDamage = (float)attacker.method_45325(class_5134.field_23721);
      class_1282 var10000;
      if (attacker instanceof class_1657) {
         class_1657 player = (class_1657)attacker;
         var10000 = MeteorClient.mc.field_1687.method_48963().method_48802(player);
      } else {
         var10000 = MeteorClient.mc.field_1687.method_48963().method_48812(attacker);
      }

      class_1282 damageSource = var10000;
      float damage = modifyAttackDamage(attacker, target, attacker.method_59958(), damageSource, itemDamage);
      return calculateReductions(damage, target, damageSource);
   }

   public static float getAttackDamage(class_1309 attacker, class_1309 target, class_1799 weapon) {
      class_1324 original = attacker.method_5996(class_5134.field_23721);
      class_1324 copy = new class_1324(class_5134.field_23721, (o) -> {
      });
      copy.method_6192(original.method_6201());
      Iterator var5 = original.method_6195().iterator();

      while(var5.hasNext()) {
         class_1322 modifier = (class_1322)var5.next();
         copy.method_26835(modifier);
      }

      copy.method_6200(class_1792.field_8006);
      class_9285 attributeModifiers = (class_9285)weapon.method_57824(class_9334.field_49636);
      if (attributeModifiers != null) {
         attributeModifiers.method_57482(class_1304.field_6173, (entry, modifierx) -> {
            if (entry == class_5134.field_23721) {
               copy.method_55696(modifierx);
            }

         });
      }

      float itemDamage = (float)copy.method_6194();
      class_1282 var10000;
      if (attacker instanceof class_1657) {
         class_1657 player = (class_1657)attacker;
         var10000 = MeteorClient.mc.field_1687.method_48963().method_48802(player);
      } else {
         var10000 = MeteorClient.mc.field_1687.method_48963().method_48812(attacker);
      }

      class_1282 damageSource = var10000;
      float damage = modifyAttackDamage(attacker, target, weapon, damageSource, itemDamage);
      return calculateReductions(damage, target, damageSource);
   }

   private static float modifyAttackDamage(class_1309 attacker, class_1309 target, class_1799 weapon, class_1282 damageSource, float damage) {
      Object2IntMap<class_6880<class_1887>> enchantments = new Object2IntOpenHashMap();
      Utils.getEnchantments(weapon, enchantments);
      float enchantDamage = 0.0F;
      int sharpness = Utils.getEnchantmentLevel((Object2IntMap)enchantments, class_1893.field_9118);
      if (sharpness > 0) {
         enchantDamage += 1.0F + 0.5F * (float)(sharpness - 1);
      }

      int baneOfArthropods = Utils.getEnchantmentLevel((Object2IntMap)enchantments, class_1893.field_9112);
      if (baneOfArthropods > 0 && target.method_5864().method_20210(class_3483.field_48285)) {
         enchantDamage += 2.5F * (float)baneOfArthropods;
      }

      int impaling = Utils.getEnchantmentLevel((Object2IntMap)enchantments, class_1893.field_9106);
      if (impaling > 0 && target.method_5864().method_20210(class_3483.field_48284)) {
         enchantDamage += 2.5F * (float)impaling;
      }

      int smite = Utils.getEnchantmentLevel((Object2IntMap)enchantments, class_1893.field_9123);
      if (smite > 0 && target.method_5864().method_20210(class_3483.field_49931)) {
         enchantDamage += 2.5F * (float)smite;
      }

      if (attacker instanceof class_1657) {
         class_1657 playerEntity = (class_1657)attacker;
         float charge = playerEntity.method_7261(0.5F);
         damage *= 0.2F + charge * charge * 0.8F;
         enchantDamage *= charge;
         class_1792 var14 = weapon.method_7909();
         if (var14 instanceof class_9362) {
            class_9362 item = (class_9362)var14;
            float bonusDamage = item.method_58403(target, damage, damageSource);
            if (bonusDamage > 0.0F) {
               int density = Utils.getEnchantmentLevel(weapon, class_1893.field_50157);
               if (density > 0) {
                  bonusDamage += 0.5F * attacker.field_6017;
               }

               damage += bonusDamage;
            }
         }

         if (charge > 0.9F && attacker.field_6017 > 0.0F && !attacker.method_24828() && !attacker.method_6101() && !attacker.method_5799() && !attacker.method_6059(class_1294.field_5919) && !attacker.method_5765()) {
            damage *= 1.5F;
         }
      }

      return damage + enchantDamage;
   }

   public static float fallDamage(class_1309 entity) {
      if (entity instanceof class_1657) {
         class_1657 player = (class_1657)entity;
         if (player.method_31549().field_7479) {
            return 0.0F;
         }
      }

      if (!entity.method_6059(class_1294.field_5906) && !entity.method_6059(class_1294.field_5902)) {
         int surface = MeteorClient.mc.field_1687.method_8500(entity.method_24515()).method_12032(class_2903.field_13197).method_12603(entity.method_31477() & 15, entity.method_31479() & 15);
         if (entity.method_31478() >= surface) {
            return fallDamageReductions(entity, surface);
         } else {
            class_3965 raycastResult = MeteorClient.mc.field_1687.method_17742(new class_3959(entity.method_19538(), new class_243(entity.method_23317(), (double)MeteorClient.mc.field_1687.method_31607(), entity.method_23321()), class_3960.field_17558, class_242.field_36338, entity));
            return raycastResult.method_17783() == class_240.field_1333 ? 0.0F : fallDamageReductions(entity, raycastResult.method_17777().method_10264());
         }
      } else {
         return 0.0F;
      }
   }

   private static float fallDamageReductions(class_1309 entity, int surface) {
      int fallHeight = (int)(entity.method_23318() - (double)surface + (double)entity.field_6017 - 3.0D);
      class_1293 jumpBoostInstance = entity.method_6112(class_1294.field_5913);
      if (jumpBoostInstance != null) {
         fallHeight -= jumpBoostInstance.method_5578() + 1;
      }

      return calculateReductions((float)fallHeight, entity, MeteorClient.mc.field_1687.method_48963().method_48827());
   }

   public static float calculateReductions(float damage, class_1309 entity, class_1282 damageSource) {
      if (damageSource.method_5514()) {
         switch(MeteorClient.mc.field_1687.method_8407()) {
         case field_5805:
            damage = Math.min(damage / 2.0F + 1.0F, damage);
            break;
         case field_5807:
            damage *= 1.5F;
         }
      }

      damage = class_1280.method_5496(entity, damage, damageSource, getArmor(entity), (float)entity.method_45325(class_5134.field_23725));
      damage = resistanceReduction(entity, damage);
      damage = protectionReduction(entity, damage, damageSource);
      return Math.max(damage, 0.0F);
   }

   private static float getArmor(class_1309 entity) {
      return (float)Math.floor(entity.method_45325(class_5134.field_23724));
   }

   private static float protectionReduction(class_1309 player, float damage, class_1282 source) {
      if (source.method_48789(class_8103.field_42242)) {
         return damage;
      } else {
         int damageProtection = 0;
         Iterator var4 = player.method_56674().iterator();

         while(var4.hasNext()) {
            class_1799 stack = (class_1799)var4.next();
            Object2IntMap<class_6880<class_1887>> enchantments = new Object2IntOpenHashMap();
            Utils.getEnchantments(stack, enchantments);
            int protection = Utils.getEnchantmentLevel((Object2IntMap)enchantments, class_1893.field_9111);
            if (protection > 0) {
               damageProtection += protection;
            }

            int fireProtection = Utils.getEnchantmentLevel((Object2IntMap)enchantments, class_1893.field_9095);
            if (fireProtection > 0 && source.method_48789(class_8103.field_42246)) {
               damageProtection += 2 * fireProtection;
            }

            int blastProtection = Utils.getEnchantmentLevel((Object2IntMap)enchantments, class_1893.field_9107);
            if (blastProtection > 0 && source.method_48789(class_8103.field_42249)) {
               damageProtection += 2 * blastProtection;
            }

            int projectileProtection = Utils.getEnchantmentLevel((Object2IntMap)enchantments, class_1893.field_9096);
            if (projectileProtection > 0 && source.method_48789(class_8103.field_42247)) {
               damageProtection += 2 * projectileProtection;
            }

            int featherFalling = Utils.getEnchantmentLevel((Object2IntMap)enchantments, class_1893.field_9129);
            if (featherFalling > 0 && source.method_48789(class_8103.field_42250)) {
               damageProtection += 3 * featherFalling;
            }
         }

         return class_1280.method_5497(damage, (float)damageProtection);
      }
   }

   private static float resistanceReduction(class_1309 player, float damage) {
      class_1293 resistance = player.method_6112(class_1294.field_5907);
      if (resistance != null) {
         int lvl = resistance.method_5578() + 1;
         damage *= 1.0F - (float)lvl * 0.2F;
      }

      return Math.max(damage, 0.0F);
   }

   private static float getExposure(class_243 source, class_238 box, DamageUtils.RaycastFactory raycastFactory) {
      double xDiff = box.field_1320 - box.field_1323;
      double yDiff = box.field_1325 - box.field_1322;
      double zDiff = box.field_1324 - box.field_1321;
      double xStep = 1.0D / (xDiff * 2.0D + 1.0D);
      double yStep = 1.0D / (yDiff * 2.0D + 1.0D);
      double zStep = 1.0D / (zDiff * 2.0D + 1.0D);
      if (xStep > 0.0D && yStep > 0.0D && zStep > 0.0D) {
         int misses = 0;
         int hits = 0;
         double xOffset = (1.0D - Math.floor(1.0D / xStep) * xStep) * 0.5D;
         double zOffset = (1.0D - Math.floor(1.0D / zStep) * zStep) * 0.5D;
         xStep *= xDiff;
         yStep *= yDiff;
         zStep *= zDiff;
         double startX = box.field_1323 + xOffset;
         double startY = box.field_1322;
         double startZ = box.field_1321 + zOffset;
         double endX = box.field_1320 + xOffset;
         double endY = box.field_1325;
         double endZ = box.field_1324 + zOffset;

         for(double x = startX; x <= endX; x += xStep) {
            for(double y = startY; y <= endY; y += yStep) {
               for(double z = startZ; z <= endZ; z += zStep) {
                  class_243 position = new class_243(x, y, z);
                  if (raycast(new DamageUtils.ExposureRaycastContext(position, source), raycastFactory) == null) {
                     ++misses;
                  }

                  ++hits;
               }
            }
         }

         return (float)misses / (float)hits;
      } else {
         return 0.0F;
      }
   }

   private static class_3965 raycast(DamageUtils.ExposureRaycastContext context, DamageUtils.RaycastFactory raycastFactory) {
      return (class_3965)class_1922.method_17744(context.start, context.end, context, raycastFactory, (ctx) -> {
         return null;
      });
   }

   @FunctionalInterface
   public interface RaycastFactory extends BiFunction<DamageUtils.ExposureRaycastContext, class_2338, class_3965> {
   }

   public static record ExposureRaycastContext(class_243 start, class_243 end) {
      public ExposureRaycastContext(class_243 start, class_243 end) {
         this.start = start;
         this.end = end;
      }

      public class_243 start() {
         return this.start;
      }

      public class_243 end() {
         return this.end;
      }
   }
}
