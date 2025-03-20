package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.MouseScrollEvent;
import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_3532;

public class Zoom extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> zoom;
   private final Setting<Double> scrollSensitivity;
   private final Setting<Boolean> smooth;
   private final Setting<Boolean> cinematic;
   private final Setting<Boolean> renderHands;
   private boolean enabled;
   private boolean preCinematic;
   private double preMouseSensitivity;
   private double value;
   private double lastFov;
   private double time;

   public Zoom() {
      super(Categories.Render, "zoom", "Zooms your view.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.zoom = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("zoom")).description("How much to zoom.")).defaultValue(6.0D).min(1.0D).build());
      this.scrollSensitivity = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scroll-sensitivity")).description("Allows you to change zoom value using scroll wheel. 0 to disable.")).defaultValue(1.0D).min(0.0D).build());
      this.smooth = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smooth")).description("Smooth transition.")).defaultValue(true)).build());
      this.cinematic = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("cinematic")).description("Enables cinematic camera.")).defaultValue(false)).build());
      this.renderHands = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-hands")).description("Whether or not to render your hands.")).defaultValue(false)).build());
      this.autoSubscribe = false;
   }

   public void onActivate() {
      if (!this.enabled) {
         this.preCinematic = this.mc.field_1690.field_1914;
         this.preMouseSensitivity = (Double)this.mc.field_1690.method_42495().method_41753();
         this.value = (Double)this.zoom.get();
         this.lastFov = (double)(Integer)this.mc.field_1690.method_41808().method_41753();
         this.time = 0.001D;
         MeteorClient.EVENT_BUS.subscribe((Object)this);
         this.enabled = true;
      }

   }

   public void onStop() {
      this.mc.field_1690.field_1914 = this.preCinematic;
      this.mc.field_1690.method_42495().method_41748(this.preMouseSensitivity);
      this.mc.field_1769.method_3292();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      this.mc.field_1690.field_1914 = (Boolean)this.cinematic.get();
      if (!(Boolean)this.cinematic.get()) {
         this.mc.field_1690.method_42495().method_41748(this.preMouseSensitivity / Math.max(this.getScaling() * 0.5D, 1.0D));
      }

      if (this.time == 0.0D) {
         MeteorClient.EVENT_BUS.unsubscribe((Object)this);
         this.enabled = false;
         this.onStop();
      }

   }

   @EventHandler
   private void onMouseScroll(MouseScrollEvent event) {
      if ((Double)this.scrollSensitivity.get() > 0.0D && this.isActive()) {
         this.value += event.value * 0.25D * (Double)this.scrollSensitivity.get() * this.value;
         if (this.value < 1.0D) {
            this.value = 1.0D;
         }

         event.cancel();
      }

   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if (!(Boolean)this.smooth.get()) {
         this.time = this.isActive() ? 1.0D : 0.0D;
      } else {
         if (this.isActive()) {
            this.time += event.frameTime * 5.0D;
         } else {
            this.time -= event.frameTime * 5.0D;
         }

         this.time = class_3532.method_15350(this.time, 0.0D, 1.0D);
      }
   }

   @EventHandler
   private void onGetFov(GetFovEvent event) {
      event.fov /= this.getScaling();
      if (this.lastFov != event.fov) {
         this.mc.field_1769.method_3292();
      }

      this.lastFov = event.fov;
   }

   public double getScaling() {
      double delta = this.time < 0.5D ? 4.0D * this.time * this.time * this.time : 1.0D - Math.pow(-2.0D * this.time + 2.0D, 3.0D) / 2.0D;
      return class_3532.method_16436(delta, 1.0D, this.value);
   }

   public boolean renderHands() {
      return !this.isActive() || (Boolean)this.renderHands.get();
   }
}
