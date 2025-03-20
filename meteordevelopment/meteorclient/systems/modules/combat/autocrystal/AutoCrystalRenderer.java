package meteordevelopment.meteorclient.systems.modules.combat.autocrystal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.utils.player.Timer;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2338.class_2339;

public class AutoCrystalRenderer {
   private final Settings settings = new Settings();
   private final SettingGroup sgRender;
   private final Setting<AutoCrystalRenderer.RenderMode> renderMode;
   private final Setting<ShapeMode> simpleShapeMode;
   private final Setting<SettingColor> simpleColor;
   private final Setting<Double> simpleDrawTime;
   private final Setting<ShapeMode> placeDelayShapeMode;
   private final Setting<SettingColor> placeDelayColor;
   private final Setting<Double> placeDelayFadeTime;
   private final Setting<ShapeMode> breakDelayShapeMode;
   private final Setting<SettingColor> breakDelayColor;
   private final Setting<Double> breakDelayFadeTime;
   private final Setting<Double> breakDelayFadeExponent;
   private final Map<class_2338, Long> crystalRenderPlaceDelays;
   private final Map<AutoCrystalRenderer.CrystalBreakRender, Long> crystalRenderBreakDelays;
   private final AutoCrystal autoCrystal;
   private class_2338 simpleRenderPos;
   private Timer simpleRenderTimer;

   public AutoCrystalRenderer(AutoCrystal ac) {
      this.sgRender = this.settings.createGroup("Render");
      this.renderMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("render-mode")).description("Mode for rendering.")).defaultValue(AutoCrystalRenderer.RenderMode.DelayDraw)).build());
      this.simpleShapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("simple-shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).visible(() -> {
         return this.renderMode.get() == AutoCrystalRenderer.RenderMode.Simple;
      })).build());
      this.simpleColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("simple-color")).description("Color to render place delays in")).defaultValue(Color.RED.a(40)).visible(() -> {
         return this.renderMode.get() == AutoCrystalRenderer.RenderMode.Simple;
      })).build());
      this.simpleDrawTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("simple-draw-time")).description("How long to draw the box")).defaultValue(0.15D).min(0.0D).sliderMax(1.0D).visible(() -> {
         return this.renderMode.get() == AutoCrystalRenderer.RenderMode.Simple;
      })).build());
      this.placeDelayShapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("place-delay-shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).visible(() -> {
         return this.renderMode.get() == AutoCrystalRenderer.RenderMode.DelayDraw;
      })).build());
      this.placeDelayColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("place-delay-color")).description("Color to render place delays in")).defaultValue(new Color(110, 0, 255, 40)).visible(() -> {
         return this.renderMode.get() == AutoCrystalRenderer.RenderMode.DelayDraw;
      })).build());
      this.placeDelayFadeTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-delay-fade-time")).description("How long to fade the box")).defaultValue(0.7D).min(0.0D).sliderMax(2.0D).visible(() -> {
         return this.renderMode.get() == AutoCrystalRenderer.RenderMode.DelayDraw;
      })).build());
      this.breakDelayShapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("break-delay-shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).visible(() -> {
         return this.renderMode.get() == AutoCrystalRenderer.RenderMode.DelayDraw;
      })).build());
      this.breakDelayColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("break-delay-color")).description("Color to render break delays in")).defaultValue(Color.BLACK.a(0)).visible(() -> {
         return this.renderMode.get() == AutoCrystalRenderer.RenderMode.DelayDraw;
      })).build());
      this.breakDelayFadeTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-delay-fade-time")).description("How long to fade the box")).defaultValue(0.4D).min(0.0D).sliderMax(2.0D).visible(() -> {
         return this.renderMode.get() == AutoCrystalRenderer.RenderMode.DelayDraw;
      })).build());
      this.breakDelayFadeExponent = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-delay-fade-exponent")).description("Adds an exponent to the fade")).defaultValue(1.6D).min(0.2D).sliderMax(4.0D).visible(() -> {
         return this.renderMode.get() == AutoCrystalRenderer.RenderMode.DelayDraw;
      })).build());
      this.crystalRenderPlaceDelays = new HashMap();
      this.crystalRenderBreakDelays = new HashMap();
      this.simpleRenderPos = null;
      this.simpleRenderTimer = new Timer();
      ac.settings.groups.addAll(this.settings.groups);
      this.autoCrystal = ac;
   }

   public void onActivate() {
      this.crystalRenderPlaceDelays.clear();
      this.crystalRenderBreakDelays.clear();
   }

   public void onRender3D(Render3DEvent event) {
      switch(((AutoCrystalRenderer.RenderMode)this.renderMode.get()).ordinal()) {
      case 0:
      default:
         break;
      case 1:
         this.drawDelay(event);
         break;
      case 2:
         this.drawSimple(event);
         break;
      case 3:
         this.drawDebug(event);
      }

   }

   public void onBreakCrystal(class_1297 entity) {
      long currentTime = System.currentTimeMillis();
      AutoCrystalRenderer.CrystalBreakRender breakRender = new AutoCrystalRenderer.CrystalBreakRender(this);
      breakRender.pos = new class_243(0.0D, 0.0D, 0.0D);
      breakRender.entity = entity;
      this.crystalRenderBreakDelays.put(breakRender, currentTime);
   }

   public void onPlaceCrystal(class_2338 pos) {
      long currentTime = System.currentTimeMillis();
      this.crystalRenderPlaceDelays.put(pos, currentTime);
      this.simpleRenderPos = pos;
      this.simpleRenderTimer.reset();
   }

   private void drawSimple(Render3DEvent event) {
      if (this.simpleRenderPos != null && !this.simpleRenderTimer.passedS((Double)this.simpleDrawTime.get())) {
         event.renderer.box((class_2338)this.simpleRenderPos, (Color)this.simpleColor.get(), (Color)this.simpleColor.get(), (ShapeMode)this.simpleShapeMode.get(), 0);
      }

   }

   private void drawDelay(Render3DEvent event) {
      long currentTime = System.currentTimeMillis();
      Iterator var4 = this.crystalRenderPlaceDelays.entrySet().iterator();

      Entry breakDelay;
      while(var4.hasNext()) {
         breakDelay = (Entry)var4.next();
         if (!((double)(currentTime - (Long)breakDelay.getValue()) > (Double)this.placeDelayFadeTime.get() * 1000.0D)) {
            double time = (double)(currentTime - (Long)breakDelay.getValue()) / 1000.0D;
            double timeCompletion = time / (Double)this.placeDelayFadeTime.get();
            this.renderBoxSized(event, (class_2338)breakDelay.getKey(), 1.0D, 1.0D - timeCompletion, (Color)this.placeDelayColor.get(), (Color)this.placeDelayColor.get(), (ShapeMode)this.placeDelayShapeMode.get());
         }
      }

      var4 = this.crystalRenderBreakDelays.entrySet().iterator();

      while(var4.hasNext()) {
         breakDelay = (Entry)var4.next();
         if (!((double)(currentTime - (Long)breakDelay.getValue()) > (Double)this.breakDelayFadeTime.get() * 1000.0D)) {
            AutoCrystalRenderer.CrystalBreakRender render = (AutoCrystalRenderer.CrystalBreakRender)breakDelay.getKey();
            if (render.parts == null && render.entity != null) {
               render.parts = WireframeEntityRenderer.cloneEntityForRendering(event, render.entity, render.pos);
               render.entity = null;
            }

            double time = (double)(currentTime - (Long)breakDelay.getValue()) / 1000.0D;
            double timeCompletion = time / (Double)this.breakDelayFadeTime.get();
            Color color = ((SettingColor)this.breakDelayColor.get()).copy().a((int)((double)((SettingColor)this.breakDelayColor.get()).a * Math.pow(1.0D - timeCompletion, (Double)this.breakDelayFadeExponent.get())));
            WireframeEntityRenderer.render(event, render.pos, render.parts, 1.0D, color, color, (ShapeMode)this.breakDelayShapeMode.get());
         }
      }

   }

   private void drawDebug(Render3DEvent event) {
      int r = (int)Math.floor((Double)this.autoCrystal.placeRange.get());
      class_2338 eyePos = class_2338.method_49638(MeteorClient.mc.field_1724.method_33571());
      int ex = eyePos.method_10263();
      int ey = eyePos.method_10264();
      int ez = eyePos.method_10260();
      class_2339 mutablePos = new class_2339(0, 0, 0);

      for(int x = -r; x <= r; ++x) {
         for(int y = -r; y <= r; ++y) {
            for(int z = -r; z <= r; ++z) {
               if ((Boolean)this.autoCrystal.cachedValidSpots.get((x + r) * 2 * r * 2 * r + (y + r) * 2 * r + z + r)) {
                  class_2338 pos = mutablePos.method_10103(ex + x, ey + y, ez + z);
                  event.renderer.box((class_2338)pos, (Color)this.simpleColor.get(), (Color)this.simpleColor.get(), (ShapeMode)this.simpleShapeMode.get(), 0);
               }
            }
         }
      }

   }

   private void renderBoxSized(Render3DEvent event, class_2338 blockPos, double size, double alpha, Color sideColor, Color lineColor, ShapeMode shapeMode) {
      class_238 orig = new class_238(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
      double shrinkFactor = 1.0D - size;
      class_238 box = orig.method_1002(orig.method_17939() * shrinkFactor, orig.method_17940() * shrinkFactor, orig.method_17941() * shrinkFactor);
      double xShrink = orig.method_17939() * shrinkFactor / 2.0D;
      double yShrink = orig.method_17940() * shrinkFactor / 2.0D;
      double zShrink = orig.method_17941() * shrinkFactor / 2.0D;
      double x1 = (double)blockPos.method_10263() + box.field_1323 + xShrink;
      double y1 = (double)blockPos.method_10264() + box.field_1322 + yShrink;
      double z1 = (double)blockPos.method_10260() + box.field_1321 + zShrink;
      double x2 = (double)blockPos.method_10263() + box.field_1320 + xShrink;
      double y2 = (double)blockPos.method_10264() + box.field_1325 + yShrink;
      double z2 = (double)blockPos.method_10260() + box.field_1324 + zShrink;
      event.renderer.box(x1, y1, z1, x2, y2, z2, sideColor.copy().a((int)((double)sideColor.a * alpha)), sideColor.copy().a((int)((double)lineColor.a * alpha)), shapeMode, 0);
   }

   private static enum RenderMode {
      None,
      DelayDraw,
      Simple,
      Debug;

      // $FF: synthetic method
      private static AutoCrystalRenderer.RenderMode[] $values() {
         return new AutoCrystalRenderer.RenderMode[]{None, DelayDraw, Simple, Debug};
      }
   }

   private class CrystalBreakRender {
      public class_243 pos;
      public List<WireframeEntityRenderer.RenderablePart> parts;
      public class_1297 entity;

      private CrystalBreakRender(final AutoCrystalRenderer param1) {
      }
   }
}
