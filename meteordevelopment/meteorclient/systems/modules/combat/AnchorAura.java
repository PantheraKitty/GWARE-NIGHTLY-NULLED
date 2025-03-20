package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.player.Safety;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3965;
import net.minecraft.class_2338.class_2339;
import org.jetbrains.annotations.Nullable;

public class AnchorAura extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlace;
   private final SettingGroup sgBreak;
   private final SettingGroup sgPause;
   private final SettingGroup sgRender;
   private final Setting<Double> targetRange;
   private final Setting<SortPriority> targetPriority;
   private final Setting<AnchorAura.RotationMode> rotationMode;
   private final Setting<Double> maxDamage;
   private final Setting<Double> minHealth;
   private final Setting<Boolean> place;
   private final Setting<Integer> placeDelay;
   private final Setting<Safety> placeMode;
   private final Setting<Double> placeRange;
   private final Setting<AnchorAura.PlaceMode> placePositions;
   private final Setting<Integer> breakDelay;
   private final Setting<Safety> breakMode;
   private final Setting<Double> breakRange;
   private final Setting<Boolean> pauseOnEat;
   private final Setting<Boolean> pauseOnDrink;
   private final Setting<Boolean> pauseOnMine;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<Boolean> renderPlace;
   private final Setting<SettingColor> placeSideColor;
   private final Setting<SettingColor> placeLineColor;
   private final Setting<Boolean> renderBreak;
   private final Setting<SettingColor> breakSideColor;
   private final Setting<SettingColor> breakLineColor;
   private int placeDelayLeft;
   private int breakDelayLeft;
   private class_1657 target;
   private final class_2339 mutable;

   public AnchorAura() {
      super(Categories.Combat, "anchor-aura", "Automatically places and breaks Respawn Anchors to harm entities.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlace = this.settings.createGroup("Place");
      this.sgBreak = this.settings.createGroup("Break");
      this.sgPause = this.settings.createGroup("Pause");
      this.sgRender = this.settings.createGroup("Render");
      this.targetRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The radius in which players get targeted.")).defaultValue(4.0D).min(0.0D).sliderMax(5.0D).build());
      this.targetPriority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to select the player to target.")).defaultValue(SortPriority.LowestHealth)).build());
      this.rotationMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("rotation-mode")).description("The mode to rotate you server-side.")).defaultValue(AnchorAura.RotationMode.Both)).build());
      this.maxDamage = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-self-damage")).description("The maximum self-damage allowed.")).defaultValue(8.0D).build());
      this.minHealth = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-health")).description("The minimum health you have to be for Anchor Aura to work.")).defaultValue(15.0D).build());
      this.place = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place")).description("Allows Anchor Aura to place anchors.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgPlace;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("The tick delay between placing anchors.")).defaultValue(2)).range(0, 10);
      Setting var10003 = this.place;
      Objects.requireNonNull(var10003);
      this.placeDelay = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgPlace;
      EnumSetting.Builder var1 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("place-mode")).description("The way anchors are allowed to be placed near you.")).defaultValue(Safety.Safe);
      var10003 = this.place;
      Objects.requireNonNull(var10003);
      this.placeMode = var10001.add(((EnumSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgPlace;
      DoubleSetting.Builder var2 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("The radius in which anchors are placed in.")).defaultValue(5.0D).min(0.0D).sliderMax(5.0D);
      var10003 = this.place;
      Objects.requireNonNull(var10003);
      this.placeRange = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgPlace;
      var1 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("placement-positions")).description("Where the Anchors will be placed on the entity.")).defaultValue(AnchorAura.PlaceMode.AboveAndBelow);
      var10003 = this.place;
      Objects.requireNonNull(var10003);
      this.placePositions = var10001.add(((EnumSetting.Builder)var1.visible(var10003::get)).build());
      this.breakDelay = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-delay")).description("The tick delay between breaking anchors.")).defaultValue(10)).range(0, 10).build());
      this.breakMode = this.sgBreak.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("break-mode")).description("The way anchors are allowed to be broken near you.")).defaultValue(Safety.Safe)).build());
      this.breakRange = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-range")).description("The radius in which anchors are broken in.")).defaultValue(5.0D).min(0.0D).sliderMax(5.0D).build());
      this.pauseOnEat = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-eat")).description("Pauses while eating.")).defaultValue(false)).build());
      this.pauseOnDrink = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-drink")).description("Pauses while drinking potions.")).defaultValue(false)).build());
      this.pauseOnMine = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-mine")).description("Pauses while mining blocks.")).defaultValue(false)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.renderPlace = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-place")).description("Renders the block where it is placing an anchor.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      ColorSetting.Builder var3 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("place-side-color")).description("The side color for positions to be placed.")).defaultValue(new SettingColor(255, 0, 0, 75));
      var10003 = this.renderPlace;
      Objects.requireNonNull(var10003);
      this.placeSideColor = var10001.add(((ColorSetting.Builder)var3.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var3 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("place-line-color")).description("The line color for positions to be placed.")).defaultValue(new SettingColor(255, 0, 0, 255));
      var10003 = this.renderPlace;
      Objects.requireNonNull(var10003);
      this.placeLineColor = var10001.add(((ColorSetting.Builder)var3.visible(var10003::get)).build());
      this.renderBreak = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-break")).description("Renders the block where it is breaking an anchor.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      var3 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("break-side-color")).description("The side color for anchors to be broken.")).defaultValue(new SettingColor(255, 0, 0, 75));
      var10003 = this.renderBreak;
      Objects.requireNonNull(var10003);
      this.breakSideColor = var10001.add(((ColorSetting.Builder)var3.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var3 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("break-line-color")).description("The line color for anchors to be broken.")).defaultValue(new SettingColor(255, 0, 0, 255));
      var10003 = this.renderBreak;
      Objects.requireNonNull(var10003);
      this.breakLineColor = var10001.add(((ColorSetting.Builder)var3.visible(var10003::get)).build());
      this.mutable = new class_2339();
   }

   public void onActivate() {
      this.placeDelayLeft = 0;
      this.breakDelayLeft = 0;
      this.target = null;
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1687.method_8597().comp_649()) {
         this.error("You are in the Nether... disabling.", new Object[0]);
         this.toggle();
      } else if (!PlayerUtils.shouldPause((Boolean)this.pauseOnMine.get(), (Boolean)this.pauseOnEat.get(), (Boolean)this.pauseOnDrink.get())) {
         if (!((double)EntityUtils.getTotalHealth(this.mc.field_1724) <= (Double)this.minHealth.get())) {
            if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
               this.target = TargetUtils.getPlayerTarget((Double)this.targetRange.get(), (SortPriority)this.targetPriority.get());
               if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
                  return;
               }
            }

            FindItemResult anchor = InvUtils.findInHotbar(class_1802.field_23141);
            FindItemResult glowStone = InvUtils.findInHotbar(class_1802.field_8801);
            if (anchor.found() && glowStone.found()) {
               class_2338 placePos;
               if (this.breakDelayLeft >= (Integer)this.breakDelay.get()) {
                  placePos = this.findBreakPos(this.target.method_24515());
                  if (placePos != null) {
                     this.breakDelayLeft = 0;
                     if (this.rotationMode.get() != AnchorAura.RotationMode.Both && this.rotationMode.get() != AnchorAura.RotationMode.Break) {
                        this.breakAnchor(placePos, anchor, glowStone);
                     } else {
                        class_2338 immutableBreakPos = placePos.method_10062();
                        Rotations.rotate(Rotations.getYaw(placePos), Rotations.getPitch(placePos), 50, () -> {
                           this.breakAnchor(immutableBreakPos, anchor, glowStone);
                        });
                     }
                  }
               }

               if (this.placeDelayLeft >= (Integer)this.placeDelay.get() && (Boolean)this.place.get()) {
                  placePos = this.findPlacePos(this.target.method_24515());
                  if (placePos != null) {
                     this.placeDelayLeft = 0;
                     BlockUtils.place(placePos.method_10062(), anchor, this.rotationMode.get() == AnchorAura.RotationMode.Place || this.rotationMode.get() == AnchorAura.RotationMode.Both, 50);
                  }
               }

               ++this.placeDelayLeft;
               ++this.breakDelayLeft;
            }
         }
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.target != null) {
         class_2338 breakPos;
         if ((Boolean)this.renderPlace.get()) {
            breakPos = this.findPlacePos(this.target.method_24515());
            if (breakPos == null) {
               return;
            }

            event.renderer.box((class_2338)breakPos, (Color)this.placeSideColor.get(), (Color)this.placeLineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         }

         if ((Boolean)this.renderBreak.get()) {
            breakPos = this.findBreakPos(this.target.method_24515());
            if (breakPos == null) {
               return;
            }

            event.renderer.box((class_2338)breakPos, (Color)this.breakSideColor.get(), (Color)this.breakLineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         }

      }
   }

   @Nullable
   private class_2338 findPlacePos(class_2338 targetPlacePos) {
      switch(((AnchorAura.PlaceMode)this.placePositions.get()).ordinal()) {
      case 0:
         if (this.isValidPlace(targetPlacePos, 0, 2, 0)) {
            return this.mutable;
         }
         break;
      case 1:
         if (this.isValidPlace(targetPlacePos, 0, 0, -1)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, 1, 0, 0)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, -1, 0, 0)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, 0, 0, 1)) {
            return this.mutable;
         }
         break;
      case 2:
         if (this.isValidPlace(targetPlacePos, 0, -1, 0)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, 0, 2, 0)) {
            return this.mutable;
         }
         break;
      case 3:
         if (this.isValidPlace(targetPlacePos, 0, -1, 0)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, 0, 2, 0)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, 1, 0, 0)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, -1, 0, 0)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, 0, 0, 1)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, 0, 0, -1)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, 1, 1, 0)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, -1, -1, 0)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, 0, 1, 1)) {
            return this.mutable;
         }

         if (this.isValidPlace(targetPlacePos, 0, 0, -1)) {
            return this.mutable;
         }
      }

      return null;
   }

   @Nullable
   private class_2338 findBreakPos(class_2338 targetPos) {
      if (this.isValidBreak(targetPos, 0, -1, 0)) {
         return this.mutable;
      } else if (this.isValidBreak(targetPos, 0, 2, 0)) {
         return this.mutable;
      } else if (this.isValidBreak(targetPos, 1, 0, 0)) {
         return this.mutable;
      } else if (this.isValidBreak(targetPos, -1, 0, 0)) {
         return this.mutable;
      } else if (this.isValidBreak(targetPos, 0, 0, 1)) {
         return this.mutable;
      } else if (this.isValidBreak(targetPos, 0, 0, -1)) {
         return this.mutable;
      } else if (this.isValidBreak(targetPos, 1, 1, 0)) {
         return this.mutable;
      } else if (this.isValidBreak(targetPos, -1, -1, 0)) {
         return this.mutable;
      } else if (this.isValidBreak(targetPos, 0, 1, 1)) {
         return this.mutable;
      } else {
         return this.isValidBreak(targetPos, 0, 0, -1) ? this.mutable : null;
      }
   }

   private boolean getDamagePlace(class_2338 pos) {
      return this.placeMode.get() == Safety.Suicide || (double)DamageUtils.bedDamage(this.mc.field_1724, pos.method_46558()) <= (Double)this.maxDamage.get();
   }

   private boolean getDamageBreak(class_2338 pos) {
      return this.breakMode.get() == Safety.Suicide || (double)DamageUtils.anchorDamage(this.mc.field_1724, pos.method_46558()) <= (Double)this.maxDamage.get();
   }

   private boolean isValidPlace(class_2338 origin, int xOffset, int yOffset, int zOffset) {
      BlockUtils.mutateAround(this.mutable, origin, xOffset, yOffset, zOffset);
      return Math.sqrt(this.mc.field_1724.method_24515().method_10262(this.mutable)) <= (Double)this.placeRange.get() && this.getDamagePlace(this.mutable) && BlockUtils.canPlace(this.mutable);
   }

   private boolean isValidBreak(class_2338 origin, int xOffset, int yOffset, int zOffset) {
      BlockUtils.mutateAround(this.mutable, origin, xOffset, yOffset, zOffset);
      return this.mc.field_1687.method_8320(this.mutable).method_26204() == class_2246.field_23152 && Math.sqrt(this.mc.field_1724.method_24515().method_10262(this.mutable)) <= (Double)this.breakRange.get() && this.getDamageBreak(this.mutable);
   }

   private void breakAnchor(class_2338 pos, FindItemResult anchor, FindItemResult glowStone) {
      if (pos != null && this.mc.field_1687.method_8320(pos).method_26204() == class_2246.field_23152) {
         this.mc.field_1724.method_5660(false);
         if (glowStone.isOffhand()) {
            this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5810, new class_3965(new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D), class_2350.field_11036, pos, true));
         } else {
            InvUtils.swap(glowStone.slot(), true);
            this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5808, new class_3965(new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D), class_2350.field_11036, pos, true));
         }

         if (anchor.isOffhand()) {
            this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5810, new class_3965(new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D), class_2350.field_11036, pos, true));
         } else {
            InvUtils.swap(anchor.slot(), true);
            this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5808, new class_3965(new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D), class_2350.field_11036, pos, true));
         }

         InvUtils.swapBack();
      }
   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }

   public static enum RotationMode {
      Place,
      Break,
      Both,
      None;

      // $FF: synthetic method
      private static AnchorAura.RotationMode[] $values() {
         return new AnchorAura.RotationMode[]{Place, Break, Both, None};
      }
   }

   public static enum PlaceMode {
      Above,
      Around,
      AboveAndBelow,
      All;

      // $FF: synthetic method
      private static AnchorAura.PlaceMode[] $values() {
         return new AnchorAura.PlaceMode[]{Above, Around, AboveAndBelow, All};
      }
   }
}
