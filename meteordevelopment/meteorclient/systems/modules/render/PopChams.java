package meteordevelopment.meteorclient.systems.modules.render;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2663;

public class PopChams extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> onlyOne;
   private final Setting<Double> renderTime;
   private final Setting<Double> yModifier;
   private final Setting<Double> scaleModifier;
   private final Setting<Boolean> fadeOut;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final List<PopChams.GhostPlayer> ghosts;

   public PopChams() {
      super(Categories.Render, "pop-chams", "Renders a ghost where players pop totem.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.onlyOne = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-one")).description("Only allow one ghost per player.")).defaultValue(false)).build());
      this.renderTime = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("render-time")).description("How long the ghost is rendered in seconds.")).defaultValue(1.0D).min(0.1D).sliderMax(6.0D).build());
      this.yModifier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("y-modifier")).description("How much should the Y position of the ghost change per second.")).defaultValue(0.75D).sliderRange(-4.0D, 4.0D).build());
      this.scaleModifier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale-modifier")).description("How much should the scale of the ghost change per second.")).defaultValue(-0.25D).sliderRange(-4.0D, 4.0D).build());
      this.fadeOut = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fade-out")).description("Fades out the color.")).defaultValue(true)).build());
      this.shapeMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(255, 255, 255, 25)).build());
      this.lineColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(255, 255, 255, 60)).build());
      this.ghosts = new ArrayList();
   }

   public void onDeactivate() {
      synchronized(this.ghosts) {
         this.ghosts.clear();
      }
   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2663) {
         class_2663 p = (class_2663)var3;
         if (p.method_11470() == 35) {
            class_1297 entity = p.method_11469(this.mc.field_1687);
            if (entity instanceof class_1657) {
               class_1657 player = (class_1657)entity;
               if (entity != this.mc.field_1724) {
                  synchronized(this.ghosts) {
                     if ((Boolean)this.onlyOne.get()) {
                        this.ghosts.removeIf((ghostPlayer) -> {
                           return ghostPlayer.uuid.equals(entity.method_5667());
                        });
                     }

                     this.ghosts.add(new PopChams.GhostPlayer(player));
                     return;
                  }
               }
            }

         }
      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      synchronized(this.ghosts) {
         this.ghosts.removeIf((ghostPlayer) -> {
            return ghostPlayer.render(event);
         });
      }
   }

   private class GhostPlayer {
      private final UUID uuid;
      private double timer;
      private double scale = 1.0D;
      private class_1657 player;
      private List<WireframeEntityRenderer.RenderablePart> parts;
      private class_243 pos;

      public GhostPlayer(class_1657 player) {
         this.uuid = player.method_5667();
         this.player = player;
         this.pos = new class_243(0.0D, 0.0D, 0.0D);
      }

      public boolean render(Render3DEvent event) {
         if (this.parts == null) {
            this.parts = WireframeEntityRenderer.cloneEntityForRendering(event, this.player, this.pos);
         }

         this.timer += event.frameTime;
         if (this.timer > (Double)PopChams.this.renderTime.get()) {
            return true;
         } else {
            ((IVec3d)this.pos).setY(this.pos.field_1351 + (Double)PopChams.this.yModifier.get() * event.frameTime);
            this.scale += (Double)PopChams.this.scaleModifier.get() * event.frameTime;
            int preSideA = ((SettingColor)PopChams.this.sideColor.get()).a;
            int preLineA = ((SettingColor)PopChams.this.lineColor.get()).a;
            if ((Boolean)PopChams.this.fadeOut.get()) {
               SettingColor var10000 = (SettingColor)PopChams.this.sideColor.get();
               var10000.a = (int)((double)var10000.a * (1.0D - this.timer / (Double)PopChams.this.renderTime.get()));
               var10000 = (SettingColor)PopChams.this.lineColor.get();
               var10000.a = (int)((double)var10000.a * (1.0D - this.timer / (Double)PopChams.this.renderTime.get()));
            }

            WireframeEntityRenderer.render(event, this.pos, this.parts, this.scale, (Color)PopChams.this.sideColor.get(), (Color)PopChams.this.lineColor.get(), (ShapeMode)PopChams.this.shapeMode.get());
            ((SettingColor)PopChams.this.sideColor.get()).a = preSideA;
            ((SettingColor)PopChams.this.lineColor.get()).a = preLineA;
            return false;
         }
      }
   }
}
