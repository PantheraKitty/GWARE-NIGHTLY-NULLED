package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_2885;
import net.minecraft.class_3612;
import net.minecraft.class_3965;
import net.minecraft.class_2846.class_2847;

public class SourceFiller extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Integer> places;
   private final Setting<Boolean> pauseEat;
   private final Setting<Boolean> grimBypass;
   private final Setting<Double> placeTime;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> normalSideColor;
   private final Setting<SettingColor> normalLineColor;
   private long lastPlaceTimeMS;
   private List<class_2338> placePoses;

   public SourceFiller() {
      super(Categories.World, "source-filler", "Places blocks in water and lava source blocks around you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.places = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("places")).description("Places to do each tick.")).min(1).defaultValue(1)).build());
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-eat")).description("Pauses while eating.")).defaultValue(true)).build());
      this.grimBypass = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("grim-bypass")).description("Bypasses Grim for airplace.")).defaultValue(true)).build());
      this.placeTime = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-time")).description("Time between places")).defaultValue(0.06D).min(0.0D).max(0.5D).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders a block overlay where the obsidian will be placed.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.normalSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("normal-side-color")).description("The side color for normal blocks.")).defaultValue(new SettingColor(0, 255, 238, 12)).visible(() -> {
         return (Boolean)this.render.get() && this.shapeMode.get() != ShapeMode.Lines;
      })).build());
      this.normalLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("normal-line-color")).description("The line color for normal blocks.")).defaultValue(new SettingColor(0, 255, 238, 100)).visible(() -> {
         return (Boolean)this.render.get() && this.shapeMode.get() != ShapeMode.Sides;
      })).build());
      this.lastPlaceTimeMS = 0L;
      this.placePoses = new ArrayList();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      this.update();
      if ((Boolean)this.render.get()) {
         this.draw(event);
      }

   }

   private void draw(Render3DEvent event) {
      Iterator var2 = this.placePoses.iterator();

      while(var2.hasNext()) {
         class_2338 pos = (class_2338)var2.next();
         event.renderer.box((class_2338)pos, (Color)this.normalSideColor.get(), (Color)this.normalLineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
      }

   }

   private void update() {
      this.placePoses.clear();
      int r = 5;
      long currentTime = System.currentTimeMillis();
      if ((double)(currentTime - this.lastPlaceTimeMS) / 1000.0D > (Double)this.placeTime.get()) {
         this.lastPlaceTimeMS = currentTime;
         if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
            class_2338 eyePos = class_2338.method_49638(this.mc.field_1724.method_33571());

            class_2338 placePos;
            for(int y = r; y > -r; --y) {
               for(int x = -r; x <= r; ++x) {
                  for(int z = -r; z <= r; ++z) {
                     placePos = eyePos.method_10069(x, y, z);
                     if (this.placePoses.size() < 2 && placePos.method_46558().method_24802(eyePos.method_46558(), 5.0D) && this.isWaterOrLavaSource(placePos)) {
                        this.placePoses.add(placePos);
                     }
                  }
               }
            }

            Iterator<class_2338> iterator = this.placePoses.iterator();
            boolean needSwapBack = false;
            byte placed = 0;

            while(placed < (Integer)this.places.get() && iterator.hasNext()) {
               placePos = (class_2338)iterator.next();
               if (BlockUtils.canPlace(placePos, true)) {
                  FindItemResult result = InvUtils.findInHotbar(class_1802.field_8328);
                  if (!result.found()) {
                     break;
                  }

                  if (!needSwapBack && this.mc.field_1724.method_31548().field_7545 != result.slot()) {
                     InvUtils.swap(result.slot(), true);
                     needSwapBack = true;
                  }

                  this.place(placePos);
               }
            }

            if (needSwapBack) {
               InvUtils.swapBack();
            }

         }
      }
   }

   private boolean place(class_2338 blockPos) {
      if (!BlockUtils.canPlace(blockPos, true)) {
         return false;
      } else {
         class_2350 dir = null;
         class_1268 hand = class_1268.field_5808;
         if (dir == null && (Boolean)this.grimBypass.get()) {
            this.mc.method_1562().method_52787(new class_2846(class_2847.field_12969, new class_2338(0, 0, 0), class_2350.field_11033));
            hand = class_1268.field_5810;
         }

         class_243 eyes = this.mc.field_1724.method_33571();
         boolean inside = eyes.field_1352 > (double)blockPos.method_10263() && eyes.field_1352 < (double)(blockPos.method_10263() + 1) && eyes.field_1351 > (double)blockPos.method_10264() && eyes.field_1351 < (double)(blockPos.method_10264() + 1) && eyes.field_1350 > (double)blockPos.method_10260() && eyes.field_1350 < (double)(blockPos.method_10260() + 1);
         int s = this.mc.field_1687.method_41925().method_41937().method_41942();
         this.mc.method_1562().method_52787(new class_2885(hand, new class_3965(blockPos.method_46558(), (class_2350)(dir == null ? class_2350.field_11033 : dir), blockPos, inside), s));
         if (dir == null && (Boolean)this.grimBypass.get()) {
            this.mc.method_1562().method_52787(new class_2846(class_2847.field_12969, new class_2338(0, 0, 0), class_2350.field_11033));
         }

         return true;
      }
   }

   public boolean isWaterOrLavaSource(class_2338 pos) {
      class_2680 blockState = this.mc.field_1687.method_8320(pos);
      return (blockState.method_26227().method_15772().method_15780(class_3612.field_15908) || blockState.method_26227().method_15772().method_15780(class_3612.field_15910)) && blockState.method_26227().method_15761() == 8;
   }
}
