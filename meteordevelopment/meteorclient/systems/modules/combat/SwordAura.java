package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.managers.RotationManager;
import meteordevelopment.meteorclient.systems.managers.SwapManager;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1493;
import net.minecraft.class_1560;
import net.minecraft.class_1590;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2824;
import net.minecraft.class_3532;
import net.minecraft.class_476;
import net.minecraft.class_485;
import net.minecraft.class_5134;
import net.minecraft.class_9285;
import net.minecraft.class_9334;
import net.minecraft.class_2828.class_2830;
import org.apache.commons.lang3.mutable.MutableDouble;

public class SwordAura extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Double> range;
   private final Setting<Boolean> silentSwapOverrideDelay;
   private final Setting<Boolean> rotate;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<SortPriority> priority;
   private final Setting<Boolean> ignorePassive;
   private final Setting<Boolean> forcePauseEat;
   private final Setting<Boolean> pauseInAir;
   private final Setting<Boolean> pauseInventoryOepn;
   private final Setting<Boolean> wallCrits;
   private final Setting<Boolean> wallCritsPauseOnMove;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Double> fadeTime;
   private long lastAttackTime;
   private class_1297 target;
   private class_1297 lastAttackedEntity;

   public SwordAura() {
      super(Categories.Combat, "sword-aura", "Automatically attacks entities with your sword");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The maximum range the entity can be to attack it.")).defaultValue(2.85D).min(0.0D).sliderMax(6.0D).build());
      this.silentSwapOverrideDelay = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("silent-swap-override-delay")).description("Whether or not to use the held items delay when attacking with silent swap")).defaultValue(true)).visible(() -> {
         return MeteorClient.SWAP.getItemSwapMode() != SwapManager.SwapMode.None;
      })).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Whether or not to rotate to the entity to attack it.")).defaultValue(true)).build());
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to attack.")).onlyAttackable().defaultValue(class_1299.field_6097).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("priority")).description("How to filter targets within range.")).defaultValue(SortPriority.ClosestAngle)).build());
      this.ignorePassive = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-passive")).description("Does not attack passive mobs.")).defaultValue(false)).build());
      this.forcePauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("force-pause-on-eat")).description("Does not attack while using an item.")).defaultValue(false)).build());
      this.pauseInAir = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-in-air")).description("Does not attack while jumping or falling")).defaultValue(false)).build());
      this.pauseInventoryOepn = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-inventory")).description("Does not attack when the inventory is open. Disabling this may cause unhappiness.")).defaultValue(true)).build());
      this.wallCrits = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("wall-crits")).description("Grimv3 crits, but only in walls")).defaultValue(true)).build());
      this.wallCritsPauseOnMove = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("wall-crits-pause-on-move")).description("Grimv3 crits, but only in walls, but only when you're not moving")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Whether or not to render attacks")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).visible(() -> {
         return (Boolean)this.render.get();
      })).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the rendering.")).defaultValue(new SettingColor(160, 0, 225, 35)).visible(() -> {
         return ((ShapeMode)this.shapeMode.get()).sides();
      })).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the rendering.")).defaultValue(new SettingColor(255, 255, 255, 50)).visible(() -> {
         return (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).lines();
      })).build());
      this.fadeTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fade-time")).description("How long to fade the bounding box render.")).min(0.0D).sliderMax(2.0D).defaultValue(0.8D).build());
      this.lastAttackTime = 0L;
      this.target = null;
      this.lastAttackedEntity = null;
   }

   @EventHandler
   public void onTick(TickEvent.Pre event) {
      this.target = null;
      if (!this.mc.field_1724.method_29504() && !this.mc.field_1724.method_7325()) {
         if (!(Boolean)this.forcePauseEat.get() || !this.mc.field_1724.method_6115() || this.mc.field_1724.method_6058() != class_1268.field_5808) {
            if (!(Boolean)this.pauseInAir.get() || this.mc.field_1724.method_24828()) {
               FindItemResult result = MeteorClient.SWAP.getSlot(class_1802.field_22022);
               if (!result.found()) {
                  result = MeteorClient.SWAP.getSlot(class_1802.field_8802);
               }

               if (result.found()) {
                  this.target = TargetUtils.get((entity) -> {
                     if (!entity.equals(this.mc.field_1724) && !entity.equals(this.mc.field_1719)) {
                        if (entity instanceof class_1309) {
                           class_1309 livingEntity = (class_1309)entity;
                           if (livingEntity.method_29504()) {
                              return false;
                           }
                        }

                        if (entity.method_5805()) {
                           class_238 hitbox = entity.method_5829();
                           class_243 closestPointOnBoundingBox = this.getClosestPointOnBox(hitbox, this.mc.field_1724.method_33571());
                           if (!closestPointOnBoundingBox.method_55230(this.mc.field_1724.method_33571(), (Double)this.range.get(), (Double)this.range.get())) {
                              return false;
                           } else if (!((Set)this.entities.get()).contains(entity.method_5864())) {
                              return false;
                           } else {
                              if ((Boolean)this.ignorePassive.get()) {
                                 if (entity instanceof class_1560) {
                                    class_1560 enderman = (class_1560)entity;
                                    if (!enderman.method_7028()) {
                                       return false;
                                    }
                                 }

                                 if (entity instanceof class_1590) {
                                    class_1590 piglin = (class_1590)entity;
                                    if (!piglin.method_6510()) {
                                       return false;
                                    }
                                 }

                                 if (entity instanceof class_1493) {
                                    class_1493 wolf = (class_1493)entity;
                                    if (!wolf.method_6510()) {
                                       return false;
                                    }
                                 }
                              }

                              if (entity instanceof class_1657) {
                                 class_1657 player = (class_1657)entity;
                                 if (player.method_7337()) {
                                    return false;
                                 }

                                 if (!Friends.get().shouldAttack(player)) {
                                    return false;
                                 }
                              }

                              return true;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  }, (SortPriority)this.priority.get());
                  if (this.target != null && this.target.method_5805()) {
                     int delayCheckSlot = result.slot();
                     if ((Boolean)this.silentSwapOverrideDelay.get()) {
                        delayCheckSlot = this.mc.field_1724.method_31548().field_7545;
                     }

                     if (this.delayCheck(delayCheckSlot)) {
                        if ((Boolean)this.pauseInventoryOepn.get() && (this.mc.field_1755 instanceof class_485 || this.mc.field_1755 instanceof class_476)) {
                           return;
                        }

                        if ((Boolean)this.rotate.get()) {
                           MeteorClient.ROTATION.requestRotation(this.getClosestPointOnBox(this.target.method_5829(), this.mc.field_1724.method_33571()), 9.0D);
                           if (!MeteorClient.ROTATION.lookingAt(this.target.method_5829())) {
                              return;
                           }
                        }

                        if (MeteorClient.SWAP.beginSwap(result, true)) {
                           this.attack();
                           MeteorClient.SWAP.endSwap(true);
                        }
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if ((Boolean)this.render.get() && this.lastAttackedEntity != null) {
         double secondsSinceAttack = (double)(System.currentTimeMillis() - this.lastAttackTime) / 1000.0D;
         if (!(secondsSinceAttack > (Double)this.fadeTime.get())) {
            double alpha = 1.0D - secondsSinceAttack / (Double)this.fadeTime.get();
            double x = class_3532.method_16436((double)event.tickDelta, this.lastAttackedEntity.field_6038, this.lastAttackedEntity.method_23317()) - this.lastAttackedEntity.method_23317();
            double y = class_3532.method_16436((double)event.tickDelta, this.lastAttackedEntity.field_5971, this.lastAttackedEntity.method_23318()) - this.lastAttackedEntity.method_23318();
            double z = class_3532.method_16436((double)event.tickDelta, this.lastAttackedEntity.field_5989, this.lastAttackedEntity.method_23321()) - this.lastAttackedEntity.method_23321();
            class_238 box = this.lastAttackedEntity.method_5829();
            event.renderer.box(x + box.field_1323, y + box.field_1322, z + box.field_1321, x + box.field_1320, y + box.field_1325, z + box.field_1324, ((SettingColor)this.sideColor.get()).copy().a((int)((double)((SettingColor)this.sideColor.get()).a * alpha)), ((SettingColor)this.lineColor.get()).copy().a((int)((double)((SettingColor)this.lineColor.get()).a * alpha)), (ShapeMode)this.shapeMode.get(), 0);
         }
      }
   }

   public void attack() {
      boolean isMoving = this.mc.field_1724.field_3913.field_3905 > 1.0E-5F || (double)this.mc.field_1724.field_3913.field_3907 > 1.0E-5D;
      if ((Boolean)this.wallCrits.get() && PlayerUtils.isPlayerPhased() && RotationManager.lastGround) {
         class_243 pos = this.mc.field_1724.method_19538();
         if ((Boolean)this.wallCritsPauseOnMove.get() && !isMoving || !(Boolean)this.wallCritsPauseOnMove.get()) {
            this.mc.method_1562().method_52787(new class_2830(pos.field_1352, pos.field_1351, pos.field_1350, MeteorClient.ROTATION.lastYaw, MeteorClient.ROTATION.lastPitch, RotationManager.lastGround));
            this.mc.method_1562().method_52787(new class_2830(pos.field_1352, pos.field_1351 + 0.0625D, pos.field_1350, MeteorClient.ROTATION.lastYaw, MeteorClient.ROTATION.lastPitch, false));
            this.mc.method_1562().method_52787(new class_2830(pos.field_1352, pos.field_1351 + 0.045D, pos.field_1350, MeteorClient.ROTATION.lastYaw, MeteorClient.ROTATION.lastPitch, false));
         }
      }

      this.mc.method_1562().method_52787(class_2824.method_34206(this.target, this.mc.field_1724.method_5715()));
      this.mc.field_1724.method_6104(class_1268.field_5808);
      this.lastAttackedEntity = this.target;
      this.lastAttackTime = System.currentTimeMillis();
   }

   private boolean delayCheck(int slot) {
      class_1661 inventory = this.mc.field_1724.method_31548();
      class_1799 itemStack = inventory.method_5438(slot);
      MutableDouble attackSpeed = new MutableDouble(this.mc.field_1724.method_45326(class_5134.field_23723));
      class_9285 attributeModifiers = (class_9285)itemStack.method_57824(class_9334.field_49636);
      if (attributeModifiers != null) {
         attributeModifiers.method_57482(class_1304.field_6173, (entry, modifier) -> {
            if (entry == class_5134.field_23723) {
               attackSpeed.add(modifier.comp_2449());
            }

         });
      }

      double attackCooldownTicks = 1.0D / attackSpeed.getValue() * 20.0D;
      long currentTime = System.currentTimeMillis();
      return (double)(currentTime - this.lastAttackTime) / 50.0D > attackCooldownTicks;
   }

   public class_243 getClosestPointOnBox(class_238 box, class_243 point) {
      double x = Math.max(box.field_1323, Math.min(point.field_1352, box.field_1320));
      double y = Math.max(box.field_1322, Math.min(point.field_1351, box.field_1325));
      double z = Math.max(box.field_1321, Math.min(point.field_1350, box.field_1324));
      return new class_243(x, y, z);
   }

   public static enum SwitchMode {
      None,
      SilentHotbar,
      SilentSwap,
      Auto;

      // $FF: synthetic method
      private static SwordAura.SwitchMode[] $values() {
         return new SwordAura.SwitchMode[]{None, SilentHotbar, SilentSwap, Auto};
      }
   }
}
