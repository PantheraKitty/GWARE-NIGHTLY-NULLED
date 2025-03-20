package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.managers.SwapManager;
import meteordevelopment.meteorclient.systems.managers.TargetManager;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2824;
import net.minecraft.class_3532;
import net.minecraft.class_5134;
import net.minecraft.class_9285;
import net.minecraft.class_9334;
import net.minecraft.class_2828.class_2830;
import org.apache.commons.lang3.mutable.MutableDouble;

public class SwordAura extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Boolean> silentSwapOverrideDelay;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> snapRotation;
   private final Setting<Boolean> forcePauseEat;
   private final Setting<Boolean> pauseInAir;
   private final Setting<Boolean> pauseInventoryOepn;
   private final Setting<Boolean> crits;
   private final Setting<Boolean> critsPauseOnMove;
   private final Setting<Boolean> critsOnlyOnSword;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Double> fadeTime;
   private final TargetManager targetManager;
   private long lastAttackTime;
   private List<class_1297> targets;
   private class_1297 lastAttackedEntity;
   private int targetIndex;

   public SwordAura() {
      super(Categories.Combat, "sword-aura", "Automatically attacks entities with your sword");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.silentSwapOverrideDelay = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("silent-swap-override-delay")).description("Whether or not to use the held items delay when attacking with silent swap")).defaultValue(true)).visible(() -> {
         return MeteorClient.SWAP.getItemSwapMode() != SwapManager.SwapMode.None;
      })).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Whether or not to rotate to the entity to attack it.")).defaultValue(true)).build());
      this.snapRotation = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("snap-rotate")).description("Instantly rotates to the targeted entity.")).defaultValue(true)).visible(() -> {
         return (Boolean)this.rotate.get();
      })).build());
      this.forcePauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("force-pause-on-eat")).description("Does not attack while using an item.")).defaultValue(false)).build());
      this.pauseInAir = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-in-air")).description("Does not attack while jumping or falling")).defaultValue(false)).build());
      this.pauseInventoryOepn = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-inventory")).description("Does not attack when the inventory is open. Disabling this may cause unhappiness.")).defaultValue(true)).build());
      this.crits = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("crits")).description("Grimv3 crits, works everywhere")).defaultValue(true)).build());
      this.critsPauseOnMove = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("crits-pause-on-move")).description("Pause crits when moving")).defaultValue(true)).visible(() -> {
         return (Boolean)this.crits.get();
      })).build());
      this.critsOnlyOnSword = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("crits-only-on-sword")).description("Only perform crits when holding a sword")).defaultValue(true)).visible(() -> {
         return (Boolean)this.crits.get();
      })).build());
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
      this.targetManager = new TargetManager(this, true);
      this.lastAttackTime = 0L;
      this.targets = null;
      this.lastAttackedEntity = null;
      this.targetIndex = 0;
   }

   @EventHandler
   public void onTick(TickEvent.Pre event) {
      if (!this.mc.field_1724.method_29504() && !this.mc.field_1724.method_7325()) {
         if (!(Boolean)this.forcePauseEat.get() || !this.mc.field_1724.method_6115() || this.mc.field_1724.method_6058() != class_1268.field_5808) {
            if (!(Boolean)this.pauseInAir.get() || this.mc.field_1724.method_24828()) {
               FindItemResult result = MeteorClient.SWAP.getSlot(class_1802.field_22022);
               if (!result.found()) {
                  result = MeteorClient.SWAP.getSlot(class_1802.field_8802);
               }

               if (result.found()) {
                  this.targets = this.targetManager.getEntityTargets();
                  if (!this.targets.isEmpty()) {
                     class_1297 target = (class_1297)this.targets.get(this.targetIndex % this.targets.size());
                     int delayCheckSlot = result.slot();
                     if ((Boolean)this.silentSwapOverrideDelay.get()) {
                        delayCheckSlot = this.mc.field_1724.method_31548().field_7545;
                     }

                     if (this.delayCheck(delayCheckSlot)) {
                        if ((Boolean)this.rotate.get()) {
                           class_243 point = this.getClosestPointOnBox(target.method_5829(), this.mc.field_1724.method_33571());
                           if ((Boolean)this.snapRotation.get()) {
                              MeteorClient.ROTATION.snapAt(point);
                           }

                           MeteorClient.ROTATION.requestRotation(point, 9.0D);
                           if (!MeteorClient.ROTATION.lookingAt(target.method_5829())) {
                              return;
                           }
                        }

                        boolean isHolding = result.isMainHand();
                        if (MeteorClient.SWAP.beginSwap(result, true)) {
                           this.attack(target, !isHolding);
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

   public void attack(class_1297 target, boolean didSwap) {
      if ((Boolean)this.crits.get()) {
         this.sendCrits(didSwap);
      }

      this.mc.method_1562().method_52787(class_2824.method_34206(target, this.mc.field_1724.method_5715()));
      this.mc.field_1724.method_6104(class_1268.field_5808);
      this.lastAttackedEntity = target;
      this.lastAttackTime = System.currentTimeMillis();
      ++this.targetIndex;
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

   private void sendCrits(boolean didSwap) {
      boolean isMoving = this.mc.field_1724.field_3913.field_3905 > 1.0E-5F || (double)this.mc.field_1724.field_3913.field_3907 > 1.0E-5D;
      if (!(Boolean)this.critsPauseOnMove.get() || !isMoving) {
         if (!(Boolean)this.critsOnlyOnSword.get() || !didSwap) {
            class_243 pos = new class_243(MeteorClient.ROTATION.lastX, MeteorClient.ROTATION.lastY, MeteorClient.ROTATION.lastZ);
            this.mc.method_1562().method_52787(new class_2830(pos.field_1352, pos.field_1351, pos.field_1350, MeteorClient.ROTATION.lastYaw, MeteorClient.ROTATION.lastPitch, true));
            this.mc.method_1562().method_52787(new class_2830(pos.field_1352, pos.field_1351 + 0.0625D, pos.field_1350, MeteorClient.ROTATION.lastYaw, MeteorClient.ROTATION.lastPitch, false));
            this.mc.method_1562().method_52787(new class_2830(pos.field_1352, pos.field_1351 + 0.045D, pos.field_1350, MeteorClient.ROTATION.lastYaw, MeteorClient.ROTATION.lastPitch, false));
         }
      }
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
