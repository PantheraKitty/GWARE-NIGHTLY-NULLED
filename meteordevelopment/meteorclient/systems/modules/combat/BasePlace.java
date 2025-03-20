package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.managers.RotationManager;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.autocrystal.AutoCrystal;
import meteordevelopment.meteorclient.systems.modules.player.SilentMine;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;

public class BasePlace extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Integer> range;
   private final Setting<SortPriority> priority;
   private final Setting<Boolean> pauseEat;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final SettingGroup sgPlace;
   private final Setting<Double> minPlace;
   private class_1657 target;

   public BasePlace() {
      super(Categories.Combat, "base-place", "Places blocks next to enemies to allow for crystal placement.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("whitelist")).description("Which blocks to use.")).defaultValue(class_2246.field_10540).build());
      this.range = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("target-range")).description("The range players can be targeted.")).defaultValue(4)).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to select the player to target.")).defaultValue(SortPriority.LowestHealth)).build());
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-eat")).description("Pauses while eating.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232, 10)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232)).build());
      this.sgPlace = this.settings.createGroup("Place");
      this.minPlace = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-place")).description("Minimum enemy damage to place.")).defaultValue(8.0D).min(0.0D).sliderRange(0.0D, 20.0D).build());
   }

   public void onActivate() {
      this.target = null;
   }

   public void onDeactivate() {
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.target == null || TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get())) {
         this.target = TargetUtils.getPlayerTarget((double)(Integer)this.range.get(), (SortPriority)this.priority.get());
         if (TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get())) {
            return;
         }
      }

      class_2338 bestPos = this.findBestObsidianPlacement(this.target);
      if (bestPos != null) {
         if (this.mc.field_1687.method_8320(bestPos).method_26215()) {
            class_1792 useItem = this.findUseItem();
            if (useItem != null) {
               if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
                  SilentMine silentMine = (SilentMine)Modules.get().get(SilentMine.class);
                  if ((silentMine.getDelayedDestroyBlockPos() == null || !bestPos.equals(silentMine.getDelayedDestroyBlockPos())) && (silentMine.getRebreakBlockPos() == null || !bestPos.equals(silentMine.getRebreakBlockPos()))) {
                     class_243 centerPos = bestPos.method_46558();
                     class_238 boundingBox = new class_238(centerPos.method_1023(0.5D, 0.5D, 0.5D), centerPos.method_1031(0.5D, 0.5D, 0.5D));
                     MeteorClient.ROTATION.requestRotation(centerPos, 11.0D);
                     if (!MeteorClient.ROTATION.lookingAt(boundingBox) && RotationManager.lastGround) {
                        MeteorClient.ROTATION.snapAt(centerPos);
                     }

                     if (MeteorClient.ROTATION.lookingAt(boundingBox)) {
                        if (!MeteorClient.BLOCK.beginPlacement(bestPos, this.mc.field_1687.method_8320(bestPos), useItem)) {
                           return;
                        }

                        MeteorClient.BLOCK.placeBlock(useItem, bestPos, this.mc.field_1687.method_8320(bestPos));
                        MeteorClient.BLOCK.endPlacement();
                     }

                  }
               }
            }
         }
      }
   }

   private class_1792 findUseItem() {
      FindItemResult result = InvUtils.findInHotbar((itemStack) -> {
         Iterator var2 = ((List)this.blocks.get()).iterator();

         class_2248 blocks;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            blocks = (class_2248)var2.next();
         } while(blocks.method_8389() != itemStack.method_7909());

         return true;
      });
      return !result.found() ? null : this.mc.field_1724.method_31548().method_5438(result.slot()).method_7909();
   }

   private class_2338 findBestObsidianPlacement(class_1657 target) {
      class_2338 targetPos = target.method_24515();
      class_2338 bestPos = null;
      double bestDamage = 0.0D;
      class_2350[] var6 = class_2350.values();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         class_2350 direction = var6[var8];
         if (direction != class_2350.field_11036 && direction != class_2350.field_11033) {
            class_2338 adjacentPos = targetPos.method_10093(direction);
            if (this.mc.field_1687.method_8320(adjacentPos).method_26215() && this.mc.field_1687.method_8320(adjacentPos.method_10084()).method_26215()) {
               double damage = this.calculateCrystalDamage(target, adjacentPos);
               if (damage > bestDamage) {
                  bestDamage = damage;
                  bestPos = adjacentPos;
               }
            }
         }
      }

      AutoCrystal autoCrystal = (AutoCrystal)Modules.get().get(AutoCrystal.class);
      if (autoCrystal != null && bestPos != null) {
         double minDamage = (Double)this.minPlace.get();
         if (bestDamage >= minDamage) {
            class_2338 supportPos = bestPos.method_10074();
            if (this.mc.field_1687.method_8320(supportPos).method_26215()) {
               double supportDamage = this.calculateCrystalDamage(target, supportPos);
               if (supportDamage >= minDamage) {
                  bestPos = supportPos;
               }
            }
         }
      }

      return bestPos;
   }

   private double calculateCrystalDamage(class_1657 target, class_2338 crystalPos) {
      class_243 crystalVec = new class_243((double)crystalPos.method_10263() + 0.5D, (double)crystalPos.method_10264(), (double)crystalPos.method_10260() + 0.5D);
      return DamageUtils.newCrystalDamage(target, target.method_5829(), crystalVec, (Set)null);
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         if (this.target != null) {
            class_2338 pos = this.findBestObsidianPlacement(this.target);
            if (pos != null) {
               event.renderer.box((class_2338)pos, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
            }
         }
      }
   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }
}
