package meteordevelopment.meteorclient.systems.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1493;
import net.minecraft.class_1560;
import net.minecraft.class_1590;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_238;
import net.minecraft.class_243;

public class TargetManager {
   private final Settings settings = new Settings();
   private final SettingGroup sgTargets;
   private final Setting<Double> range;
   private final Setting<TargetManager.TargetMode> targetMode;
   private final Setting<TargetManager.TargetSortMode> targetSortMode;
   private final Setting<Integer> numTargets;
   private final Setting<Boolean> ignoreNakeds;
   private final Setting<Boolean> ignorePassive;
   private Setting<Set<class_1299<?>>> validEntities;

   public TargetManager(Module module, boolean entityListFilter) {
      this.sgTargets = this.settings.createGroup("Targets");
      this.range = this.sgTargets.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("Max range to target.")).defaultValue(6.5D).min(0.0D).sliderMax(7.0D).build());
      this.targetMode = this.sgTargets.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-mode")).description("How many targets to choose.")).defaultValue(TargetManager.TargetMode.Single)).build());
      this.targetSortMode = this.sgTargets.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-sort-mode")).description("How to sort the targets.")).defaultValue(TargetManager.TargetSortMode.ClosestAngle)).build());
      this.numTargets = this.sgTargets.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("num-targets")).description("Max range to target.")).defaultValue(2)).min(1).sliderMax(5).visible(() -> {
         return this.targetMode.get() == TargetManager.TargetMode.Multi;
      })).build());
      this.ignoreNakeds = this.sgTargets.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-nakeds")).description("Ignore players with no items.")).defaultValue(true)).build());
      this.ignorePassive = this.sgTargets.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-passive")).description("Does not attack passive mobs.")).defaultValue(false)).build());
      this.validEntities = null;
      module.settings.groups.addAll(this.settings.groups);
      this.validEntities = this.sgTargets.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to target.")).onlyAttackable().defaultValue(class_1299.field_6097).build());
   }

   public List<class_1657> getPlayerTargets() {
      return this.getPlayerTargets((entity) -> {
         return true;
      });
   }

   public List<class_1657> getPlayerTargets(Predicate<class_1657> isGood) {
      List<class_1657> entities = new ArrayList();
      class_243 pos = MeteorClient.mc.field_1724.method_19538();
      class_238 box = new class_238(pos.field_1352 - (Double)this.range.get(), pos.field_1351 - (Double)this.range.get(), pos.field_1350 - (Double)this.range.get(), pos.field_1352 + (Double)this.range.get(), pos.field_1351 + (Double)this.range.get(), pos.field_1350 + (Double)this.range.get());
      double rangeSqr = (Double)this.range.get() * (Double)this.range.get();
      Iterator var7 = MeteorClient.mc.field_1687.method_8390(class_1657.class, box, (e) -> {
         return !e.method_31481();
      }).iterator();

      while(true) {
         class_1657 entity;
         do {
            do {
               do {
                  do {
                     if (!var7.hasNext()) {
                        ((List)entities).sort((Comparator)this.targetSortMode.get());
                        switch(((TargetManager.TargetMode)this.targetMode.get()).ordinal()) {
                        case 0:
                           if (((List)entities).size() >= 1) {
                              entities = List.of((class_1657)((List)entities).get(0));
                           }
                           break;
                        case 1:
                           if (((List)entities).size() > (Integer)this.numTargets.get()) {
                              ((List)entities).subList((Integer)this.numTargets.get(), ((List)entities).size()).clear();
                           }
                        case 2:
                        }

                        return (List)entities;
                     }

                     entity = (class_1657)var7.next();
                  } while(entity == null);
               } while(!(entity.method_5829().method_49271(pos) < rangeSqr));
            } while(!isGood.test(entity));
         } while((Boolean)this.ignoreNakeds.get() && ((class_1799)entity.method_31548().field_7548.get(0)).method_7960() && ((class_1799)entity.method_31548().field_7548.get(1)).method_7960() && ((class_1799)entity.method_31548().field_7548.get(2)).method_7960() && ((class_1799)entity.method_31548().field_7548.get(3)).method_7960());

         if (!entity.method_7337() && Friends.get().shouldAttack(entity) && !entity.equals(MeteorClient.mc.field_1724) && !entity.equals(MeteorClient.mc.field_1719) && !entity.method_29504()) {
            ((List)entities).add(entity);
         }
      }
   }

   public List<class_1297> getEntityTargets() {
      return this.getEntityTargets((entity) -> {
         return true;
      });
   }

   public List<class_1297> getEntityTargets(Predicate<class_1297> isGood) {
      List<class_1297> entities = new ArrayList();
      class_243 pos = MeteorClient.mc.field_1724.method_19538();
      class_238 box = new class_238(pos.field_1352 - (Double)this.range.get(), pos.field_1351 - (Double)this.range.get(), pos.field_1350 - (Double)this.range.get(), pos.field_1352 + (Double)this.range.get(), pos.field_1351 + (Double)this.range.get(), pos.field_1350 + (Double)this.range.get());
      double rangeSqr = (Double)this.range.get() * (Double)this.range.get();
      Iterator var7 = MeteorClient.mc.field_1687.method_8390(class_1297.class, box, (e) -> {
         return !e.method_31481();
      }).iterator();

      while(true) {
         class_1297 entity;
         class_1657 player;
         do {
            while(true) {
               do {
                  class_1309 livingEntity;
                  do {
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    do {
                                       if (!var7.hasNext()) {
                                          ((List)entities).sort((Comparator)this.targetSortMode.get());
                                          switch(((TargetManager.TargetMode)this.targetMode.get()).ordinal()) {
                                          case 0:
                                             if (((List)entities).size() >= 1) {
                                                entities = List.of((class_1297)((List)entities).get(0));
                                             }
                                             break;
                                          case 1:
                                             if (((List)entities).size() > (Integer)this.numTargets.get()) {
                                                ((List)entities).subList((Integer)this.numTargets.get(), ((List)entities).size()).clear();
                                             }
                                          case 2:
                                          }

                                          return (List)entities;
                                       }

                                       entity = (class_1297)var7.next();
                                    } while(entity == null);
                                 } while(!(entity.method_5829().method_49271(pos) < rangeSqr));
                              } while(!isGood.test(entity));
                           } while(entity.equals(MeteorClient.mc.field_1724));
                        } while(entity.equals(MeteorClient.mc.field_1719));

                        if (!(entity instanceof class_1309)) {
                           break;
                        }

                        livingEntity = (class_1309)entity;
                     } while(livingEntity.method_29504());
                  } while(!entity.method_5805());
               } while(this.validEntities != null && !((Set)this.validEntities.get()).contains(entity.method_5864()));

               if (!(Boolean)this.ignorePassive.get()) {
                  break;
               }

               if (entity instanceof class_1560) {
                  class_1560 enderman = (class_1560)entity;
                  if (!enderman.method_7028()) {
                     continue;
                  }
               }

               if (entity instanceof class_1590) {
                  class_1590 piglin = (class_1590)entity;
                  if (!piglin.method_7076(MeteorClient.mc.field_1724)) {
                     continue;
                  }
               }

               if (!(entity instanceof class_1493)) {
                  break;
               }

               class_1493 wolf = (class_1493)entity;
               if (wolf.method_6510()) {
                  break;
               }
            }

            if (!(entity instanceof class_1657)) {
               break;
            }

            player = (class_1657)entity;
         } while(player.method_7337() || !Friends.get().shouldAttack(player));

         ((List)entities).add(entity);
      }
   }

   public static enum TargetMode {
      Single,
      Multi,
      All;

      // $FF: synthetic method
      private static TargetManager.TargetMode[] $values() {
         return new TargetManager.TargetMode[]{Single, Multi, All};
      }
   }

   public static enum TargetSortMode implements Comparator<class_1297> {
      LowestDistance(Comparator.comparingDouble((entity) -> {
         return entity.method_33571().method_1025(MeteorClient.mc.field_1724.method_33571());
      })),
      HighestDistance((e1, e2) -> {
         return Double.compare(e2.method_33571().method_1025(MeteorClient.mc.field_1724.method_33571()), e1.method_33571().method_1025(MeteorClient.mc.field_1724.method_33571()));
      }),
      ClosestAngle(TargetManager.TargetSortMode::sortAngle);

      private final Comparator<class_1297> comparator;

      private TargetSortMode(Comparator<class_1297> comparator) {
         this.comparator = comparator;
      }

      public int compare(class_1297 o1, class_1297 o2) {
         return this.comparator.compare(o1, o2);
      }

      private static int sortAngle(class_1297 e1, class_1297 e2) {
         float[] angle1 = MeteorClient.ROTATION.getRotation(e1.method_33571());
         float[] angle2 = MeteorClient.ROTATION.getRotation(e1.method_33571());
         double e1yaw = (double)Math.abs(angle1[0] - MeteorClient.mc.field_1724.method_36454());
         double e2yaw = (double)Math.abs(angle2[0] - MeteorClient.mc.field_1724.method_36454());
         return Double.compare(e1yaw * e1yaw, e2yaw * e2yaw);
      }

      // $FF: synthetic method
      private static TargetManager.TargetSortMode[] $values() {
         return new TargetManager.TargetSortMode[]{LowestDistance, HighestDistance, ClosestAngle};
      }
   }
}
