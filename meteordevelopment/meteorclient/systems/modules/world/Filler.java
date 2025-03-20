package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2338.class_2339;

public class Filler extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Filler.FillerMode> mode;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Filler.HorizontalDirection> horizontalDirection;
   private final Setting<Filler.PlaneDirection> planeDirection;
   private final Setting<Integer> planeValue;
   private final Setting<Integer> planeThickness;
   private final Setting<Double> fadeTime;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final class_2339 mutablePos;
   private Map<class_2338, Long> renderLastPlacedBlock;

   public Filler() {
      super(Categories.World, "filler", "Places blocks to piss of NSO.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("What mode to use.")).defaultValue(Filler.FillerMode.Below)).build());
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("Which blocks to use.")).defaultValue(class_2246.field_10540).visible(() -> {
         return this.mode.get() != Filler.FillerMode.Litematica;
      })).build());
      this.horizontalDirection = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("horizontal-direction")).description("What direction to fill in horizontally.")).defaultValue(Filler.HorizontalDirection.East)).visible(() -> {
         return this.mode.get() == Filler.FillerMode.Horizontal || this.mode.get() == Filler.FillerMode.HorizontalSwim;
      })).build());
      this.planeDirection = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("plane-direction")).description("What axis to put the plane on.")).defaultValue(Filler.PlaneDirection.X)).visible(() -> {
         return this.mode.get() == Filler.FillerMode.Plane;
      })).build());
      this.planeValue = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("plane-value")).description("The value for the axis on the plane. Think Direction = X, value = -39 to mean place on X = -39.")).defaultValue(-39)).noSlider().visible(() -> {
         return this.mode.get() == Filler.FillerMode.Plane;
      })).build());
      this.planeThickness = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("plane-thickness")).description("How thick to build the plane. Useful for building walls.")).min(1).sliderMax(4).defaultValue(1)).visible(() -> {
         return this.mode.get() == Filler.FillerMode.Plane;
      })).build());
      this.fadeTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fade-time")).description("How many seconds it takes to fade.")).defaultValue(0.2D).min(0.0D).sliderMax(1.0D).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(85, 0, 255, 40)).visible(() -> {
         return this.shapeMode.get() != ShapeMode.Lines;
      })).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(255, 255, 255, 60)).visible(() -> {
         return this.shapeMode.get() != ShapeMode.Sides;
      })).build());
      this.mutablePos = new class_2339();
      this.renderLastPlacedBlock = new HashMap();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      long currentTime = System.currentTimeMillis();
      if (!this.mc.field_1724.method_6115()) {
         if (this.mode.get() != Filler.FillerMode.Litematica) {
            List<class_2338> placePoses = this.getBlockPoses();
            boolean canMove = true;
            Iterator var6 = this.renderLastPlacedBlock.entrySet().iterator();

            while(var6.hasNext()) {
               Entry<class_2338, Long> entry = (Entry)var6.next();
               if (!((double)(currentTime - (Long)entry.getValue()) > (Double)this.fadeTime.get() * 1000.0D)) {
                  canMove = false;
               }
            }

            if (!canMove) {
               this.mc.field_1724.field_3913.field_3905 = 0.0F;
               this.mc.field_1724.field_3913.field_3907 = 0.0F;
            }

            placePoses.sort((x, y) -> {
               return Double.compare(x.method_19770(this.mc.field_1724.method_19538()), y.method_19770(this.mc.field_1724.method_19538()));
            });
            class_1792 useItem = this.findUseItem();
            if (!MeteorClient.BLOCK.beginPlacement(placePoses, useItem)) {
               return;
            }

            placePoses.forEach((blockPos) -> {
               if (MeteorClient.BLOCK.placeBlock(useItem, blockPos)) {
                  this.renderLastPlacedBlock.put(blockPos, currentTime);
               }

            });
            MeteorClient.BLOCK.endPlacement();
         }

      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      long currentTime = System.currentTimeMillis();
      Iterator var4 = this.renderLastPlacedBlock.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<class_2338, Long> entry = (Entry)var4.next();
         if (!((double)(currentTime - (Long)entry.getValue()) > (Double)this.fadeTime.get() * 1000.0D)) {
            double time = (double)(currentTime - (Long)entry.getValue()) / 1000.0D;
            double timeCompletion = time / (Double)this.fadeTime.get();
            Color fadedSideColor = ((SettingColor)this.sideColor.get()).copy().a((int)((double)((SettingColor)this.sideColor.get()).a * (1.0D - timeCompletion)));
            Color fadedLineColor = ((SettingColor)this.lineColor.get()).copy().a((int)((double)((SettingColor)this.lineColor.get()).a * (1.0D - timeCompletion)));
            event.renderer.box((class_2338)((class_2338)entry.getKey()), fadedSideColor, fadedLineColor, (ShapeMode)this.shapeMode.get(), 0);
         }
      }

   }

   private List<class_2338> getBlockPoses() {
      List<class_2338> placePoses = new ArrayList();
      int r = 5;
      class_2338 eyePos = class_2338.method_49638(this.mc.field_1724.method_33571());
      int ex = eyePos.method_10263();
      int ey = eyePos.method_10264();
      int ez = eyePos.method_10260();

      for(int x = -r; x <= r; ++x) {
         for(int y = -r; y <= r; ++y) {
            for(int z = -r; z <= r; ++z) {
               class_2338 pos = this.mutablePos.method_10103(ex + x, ey + y, ez + z);
               switch(((Filler.FillerMode)this.mode.get()).ordinal()) {
               case 0:
                  if (pos.method_10264() >= this.mc.field_1724.method_31478()) {
                     continue;
                  }
                  break;
               case 1:
                  if (!this.directionCheck(pos)) {
                     continue;
                  }
                  break;
               case 2:
                  if (pos.method_10264() == this.mc.field_1724.method_31478() && !this.directionCheck(pos)) {
                     continue;
                  }
                  break;
               case 3:
                  if (!this.planeCheck(pos)) {
                     continue;
                  }
               }

               class_2680 state = this.mc.field_1687.method_8320(pos);
               if (MeteorClient.BLOCK.checkPlacement(class_1802.field_8281, pos, state) && this.inPlaceRange(pos)) {
                  placePoses.add(new class_2338(pos));
               }
            }
         }
      }

      return placePoses;
   }

   private boolean directionCheck(class_2338 blockPos) {
      switch(((Filler.HorizontalDirection)this.horizontalDirection.get()).ordinal()) {
      case 0:
         if (blockPos.method_10260() <= this.mc.field_1724.method_31479()) {
            return false;
         }
         break;
      case 1:
         if (blockPos.method_10260() >= this.mc.field_1724.method_31479()) {
            return false;
         }
         break;
      case 2:
         if (blockPos.method_10263() >= this.mc.field_1724.method_31477()) {
            return false;
         }
         break;
      case 3:
         if (blockPos.method_10263() <= this.mc.field_1724.method_31477()) {
            return false;
         }
      }

      return true;
   }

   private boolean planeCheck(class_2338 blockPos) {
      int blockValue = 0;
      switch(((Filler.PlaneDirection)this.planeDirection.get()).ordinal()) {
      case 0:
         blockValue = blockPos.method_10263();
         break;
      case 1:
         blockValue = blockPos.method_10264();
         break;
      case 2:
         blockValue = blockPos.method_10260();
      }

      return Math.abs((Integer)this.planeValue.get() - blockValue) <= (Integer)this.planeThickness.get() - 1;
   }

   private class_1792 findUseItem() {
      FindItemResult result = InvUtils.find((itemStack) -> {
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

   private boolean inPlaceRange(class_2338 blockPos) {
      class_243 from = this.mc.field_1724.method_33571();
      return blockPos.method_46558().method_1022(from) <= 5.1D;
   }

   private static enum FillerMode {
      Below,
      Horizontal,
      HorizontalSwim,
      Plane,
      Litematica;

      // $FF: synthetic method
      private static Filler.FillerMode[] $values() {
         return new Filler.FillerMode[]{Below, Horizontal, HorizontalSwim, Plane, Litematica};
      }
   }

   public static enum HorizontalDirection {
      North,
      South,
      East,
      West;

      // $FF: synthetic method
      private static Filler.HorizontalDirection[] $values() {
         return new Filler.HorizontalDirection[]{North, South, East, West};
      }
   }

   public static enum PlaneDirection {
      X,
      Y,
      Z;

      // $FF: synthetic method
      private static Filler.PlaneDirection[] $values() {
         return new Filler.PlaneDirection[]{X, Y, Z};
      }
   }
}
