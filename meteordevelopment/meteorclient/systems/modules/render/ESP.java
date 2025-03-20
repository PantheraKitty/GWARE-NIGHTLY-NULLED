package meteordevelopment.meteorclient.systems.modules.render;

import java.util.Iterator;
import java.util.Set;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import org.joml.Vector3d;

public class ESP extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgColors;
   public final Setting<ESP.Mode> mode;
   public final Setting<Integer> outlineWidth;
   public final Setting<Double> glowMultiplier;
   public final Setting<Boolean> ignoreSelf;
   public final Setting<ShapeMode> shapeMode;
   public final Setting<Double> fillOpacity;
   private final Setting<Double> fadeDistance;
   private final Setting<Double> endCrystalFadeDistance;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<SettingColor> playersLineColor;
   private final Setting<SettingColor> playersSideColor;
   private final Setting<SettingColor> friendPlayersLineColor;
   private final Setting<SettingColor> friendPlayersSideColor;
   private final Setting<SettingColor> enemyPlayersLineColor;
   private final Setting<SettingColor> enemyPlayersSideColor;
   private final Setting<SettingColor> animalsLineColor;
   private final Setting<SettingColor> animalsSideColor;
   private final Setting<SettingColor> waterAnimalsLineColor;
   private final Setting<SettingColor> waterAnimalsSideColor;
   private final Setting<SettingColor> monstersLineColor;
   private final Setting<SettingColor> monstersSideColor;
   private final Setting<SettingColor> ambientLineColor;
   private final Setting<SettingColor> ambientSideColor;
   private final Setting<SettingColor> miscLineColor;
   private final Setting<SettingColor> miscSideColor;
   private final Color lineColor;
   private final Color sideColor;
   private final Color baseSideColor;
   private final Color baseLineColor;
   private final Vector3d pos1;
   private final Vector3d pos2;
   private final Vector3d pos;
   private int count;

   public ESP() {
      super(Categories.Render, "esp", "Renders entities through walls.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgColors = this.settings.createGroup("Colors");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Rendering mode.")).defaultValue(ESP.Mode.Shader)).build());
      this.outlineWidth = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("outline-width")).description("The width of the shader outline.")).visible(() -> {
         return this.mode.get() == ESP.Mode.Shader;
      })).defaultValue(2)).range(1, 10).sliderRange(1, 5).build());
      this.glowMultiplier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("glow-multiplier")).description("Multiplier for glow effect")).visible(() -> {
         return this.mode.get() == ESP.Mode.Shader;
      })).decimalPlaces(3).defaultValue(3.5D).min(0.0D).sliderMax(10.0D).build());
      this.ignoreSelf = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-self")).description("Ignores yourself drawing the shader.")).defaultValue(true)).build());
      this.shapeMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).visible(() -> {
         return this.mode.get() != ESP.Mode.Glow;
      })).defaultValue(ShapeMode.Both)).build());
      this.fillOpacity = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fill-opacity")).description("The opacity of the shape fill.")).visible(() -> {
         return this.shapeMode.get() != ShapeMode.Lines && this.mode.get() != ESP.Mode.Glow;
      })).defaultValue(0.3D).range(0.0D, 1.0D).sliderMax(1.0D).build());
      this.fadeDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fade-distance")).description("The distance from an entity where the color begins to fade.")).defaultValue(3.0D).min(0.0D).sliderMax(12.0D).build());
      this.endCrystalFadeDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("end-crystal-fade-distance")).description("The distance from an end crystal where the color begins to fade.")).defaultValue(3.0D).min(0.0D).sliderMax(12.0D).build());
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Select specific entities.")).defaultValue(class_1299.field_6097).build());
      this.playersLineColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("players-line-color")).description("The line color for players.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.playersSideColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("players-side-color")).description("The side color for players.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.friendPlayersLineColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("friend-players-line-color")).description("The line color for players you have added.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.friendPlayersSideColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("friend-players-side-color")).description("The side color for playersyou have added.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.enemyPlayersLineColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("enemy-players-line-color")).description("The line color for players you have enemied.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.enemyPlayersSideColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("enemy-players-side-color")).description("The side color for players you have enemied.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.animalsLineColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("animals-line-color")).description("The line color for animals.")).defaultValue(new SettingColor(25, 255, 25, 255)).build());
      this.animalsSideColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("animals-side-color")).description("The side color for animals.")).defaultValue(new SettingColor(25, 255, 25, 255)).build());
      this.waterAnimalsLineColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("water-animals-line-color")).description("The line color for water animals.")).defaultValue(new SettingColor(25, 25, 255, 255)).build());
      this.waterAnimalsSideColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("water-animals-side-color")).description("The side color for water animals.")).defaultValue(new SettingColor(25, 25, 255, 255)).build());
      this.monstersLineColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("monsters-line-color")).description("The line color for monsters.")).defaultValue(new SettingColor(255, 25, 25, 255)).build());
      this.monstersSideColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("monsters-side-color")).description("The side color for monsters.")).defaultValue(new SettingColor(255, 25, 25, 255)).build());
      this.ambientLineColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ambient-line-color")).description("The line color for ambient entities.")).defaultValue(new SettingColor(25, 25, 25, 255)).build());
      this.ambientSideColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ambient-side-color")).description("The side color for ambient entities.")).defaultValue(new SettingColor(25, 25, 25, 255)).build());
      this.miscLineColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("misc-line-color")).description("The line color for miscellaneous entities.")).defaultValue(new SettingColor(175, 175, 175, 255)).build());
      this.miscSideColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("misc-side-color")).description("The side color for miscellaneous entities.")).defaultValue(new SettingColor(175, 175, 175, 255)).build());
      this.lineColor = new Color();
      this.sideColor = new Color();
      this.baseSideColor = new Color();
      this.baseLineColor = new Color();
      this.pos1 = new Vector3d();
      this.pos2 = new Vector3d();
      this.pos = new Vector3d();
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if (this.mode.get() != ESP.Mode._2D) {
         this.count = 0;
         Iterator var2 = this.mc.field_1687.method_18112().iterator();

         while(true) {
            class_1297 entity;
            do {
               if (!var2.hasNext()) {
                  return;
               }

               entity = (class_1297)var2.next();
            } while(this.shouldSkip(entity));

            if (this.mode.get() == ESP.Mode.Box || this.mode.get() == ESP.Mode.Wireframe) {
               this.drawBoundingBox(event, entity);
            }

            ++this.count;
         }
      }
   }

   private void drawBoundingBox(Render3DEvent event, class_1297 entity) {
      Color entitySideColor = this.getSideColor(entity);
      Color entityLineColor = this.getLineColor(entity);
      double alpha;
      double fadeDist;
      double distance;
      if (entitySideColor != null && entityLineColor != null) {
         alpha = 1.0D;
         if (entity instanceof class_1511) {
            fadeDist = (Double)this.endCrystalFadeDistance.get() * (Double)this.endCrystalFadeDistance.get();
            distance = PlayerUtils.squaredDistanceToCamera(entity);
            if (distance <= fadeDist / 2.0D) {
               alpha = 1.0D;
            } else if (distance >= fadeDist * 2.0D) {
               alpha = 0.0D;
            } else {
               alpha = 1.0D - (distance - fadeDist / 2.0D) / (fadeDist * 1.5D);
            }

            if (alpha <= 0.075D) {
               alpha = 0.0D;
            } else {
               alpha += 0.1D;
            }

            if (alpha > 1.0D) {
               alpha = 1.0D;
            }
         }

         this.sideColor.set(entitySideColor).a((int)((double)this.sideColor.a * (Double)this.fillOpacity.get() * alpha * alpha));
         this.lineColor.set(entityLineColor).a((int)((double)this.lineColor.a * alpha * alpha));
      }

      if (this.mode.get() == ESP.Mode.Box) {
         alpha = class_3532.method_16436((double)event.tickDelta, entity.field_6038, entity.method_23317()) - entity.method_23317();
         fadeDist = class_3532.method_16436((double)event.tickDelta, entity.field_5971, entity.method_23318()) - entity.method_23318();
         distance = class_3532.method_16436((double)event.tickDelta, entity.field_5989, entity.method_23321()) - entity.method_23321();
         class_238 box = entity.method_5829();
         event.renderer.box(alpha + box.field_1323, fadeDist + box.field_1322, distance + box.field_1321, alpha + box.field_1320, fadeDist + box.field_1325, distance + box.field_1324, this.sideColor, this.lineColor, (ShapeMode)this.shapeMode.get(), 0);
      } else {
         WireframeEntityRenderer.render(event, entity, 1.0D, this.sideColor, this.lineColor, (ShapeMode)this.shapeMode.get());
      }

   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      if (this.mode.get() == ESP.Mode._2D) {
         Renderer2D.COLOR.begin();
         this.count = 0;
         Iterator var2 = this.mc.field_1687.method_18112().iterator();

         while(var2.hasNext()) {
            class_1297 entity = (class_1297)var2.next();
            if (!this.shouldSkip(entity)) {
               class_238 box = entity.method_5829();
               double x = class_3532.method_16436((double)event.tickDelta, entity.field_6038, entity.method_23317()) - entity.method_23317();
               double y = class_3532.method_16436((double)event.tickDelta, entity.field_5971, entity.method_23318()) - entity.method_23318();
               double z = class_3532.method_16436((double)event.tickDelta, entity.field_5989, entity.method_23321()) - entity.method_23321();
               this.pos1.set(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
               this.pos2.set(0.0D, 0.0D, 0.0D);
               if (!this.checkCorner(box.field_1323 + x, box.field_1322 + y, box.field_1321 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1320 + x, box.field_1322 + y, box.field_1321 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1323 + x, box.field_1322 + y, box.field_1324 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1320 + x, box.field_1322 + y, box.field_1324 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1323 + x, box.field_1325 + y, box.field_1321 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1320 + x, box.field_1325 + y, box.field_1321 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1323 + x, box.field_1325 + y, box.field_1324 + z, this.pos1, this.pos2) && !this.checkCorner(box.field_1320 + x, box.field_1325 + y, box.field_1324 + z, this.pos1, this.pos2)) {
                  Color entitySideColor = this.getSideColor(entity);
                  Color entityLineColor = this.getLineColor(entity);
                  if (entitySideColor != null && entityLineColor != null) {
                     this.sideColor.set(entitySideColor).a((int)((double)this.sideColor.a * (Double)this.fillOpacity.get()));
                     this.lineColor.set(entityLineColor);
                  }

                  if (this.shapeMode.get() != ShapeMode.Lines && this.sideColor.a > 0) {
                     Renderer2D.COLOR.quad(this.pos1.x, this.pos1.y, this.pos2.x - this.pos1.x, this.pos2.y - this.pos1.y, this.sideColor);
                  }

                  if (this.shapeMode.get() != ShapeMode.Sides) {
                     Renderer2D.COLOR.line(this.pos1.x, this.pos1.y, this.pos1.x, this.pos2.y, this.lineColor);
                     Renderer2D.COLOR.line(this.pos2.x, this.pos1.y, this.pos2.x, this.pos2.y, this.lineColor);
                     Renderer2D.COLOR.line(this.pos1.x, this.pos1.y, this.pos2.x, this.pos1.y, this.lineColor);
                     Renderer2D.COLOR.line(this.pos1.x, this.pos2.y, this.pos2.x, this.pos2.y, this.lineColor);
                  }

                  ++this.count;
               }
            }
         }

         Renderer2D.COLOR.render((class_4587)null);
      }
   }

   private boolean checkCorner(double x, double y, double z, Vector3d min, Vector3d max) {
      this.pos.set(x, y, z);
      if (!NametagUtils.to2D(this.pos, 1.0D)) {
         return true;
      } else {
         if (this.pos.x < min.x) {
            min.x = this.pos.x;
         }

         if (this.pos.y < min.y) {
            min.y = this.pos.y;
         }

         if (this.pos.z < min.z) {
            min.z = this.pos.z;
         }

         if (this.pos.x > max.x) {
            max.x = this.pos.x;
         }

         if (this.pos.y > max.y) {
            max.y = this.pos.y;
         }

         if (this.pos.z > max.z) {
            max.z = this.pos.z;
         }

         return false;
      }
   }

   public boolean shouldSkip(class_1297 entity) {
      if (!((Set)this.entities.get()).contains(entity.method_5864())) {
         return true;
      } else if (entity == this.mc.field_1724 && (Boolean)this.ignoreSelf.get()) {
         return true;
      } else if (entity == this.mc.field_1719 && this.mc.field_1690.method_31044().method_31034()) {
         return true;
      } else {
         return !EntityUtils.isInRenderDistance(entity);
      }
   }

   public Color getLineColor(class_1297 entity) {
      if (!((Set)this.entities.get()).contains(entity.method_5864())) {
         return null;
      } else {
         double alpha = this.getFadeAlpha(entity);
         if (alpha == 0.0D) {
            return null;
         } else {
            Color color = this.getEntityTypeLineColor(entity);
            return this.baseLineColor.set(color.r, color.g, color.b, (int)((double)color.a * alpha));
         }
      }
   }

   public Color getSideColor(class_1297 entity) {
      if (!((Set)this.entities.get()).contains(entity.method_5864())) {
         return null;
      } else {
         double alpha = this.getFadeAlpha(entity);
         if (alpha == 0.0D) {
            return null;
         } else {
            Color color = this.getEntityTypeSideColor(entity);
            return this.baseSideColor.set(color.r, color.g, color.b, (int)((double)color.a * alpha));
         }
      }
   }

   public Color getEntityTypeLineColor(class_1297 entity) {
      if (entity instanceof class_1657) {
         class_1657 player = (class_1657)entity;
         if (Friends.get().isFriend(player)) {
            return (Color)this.friendPlayersLineColor.get();
         } else {
            return Friends.get().isEnemy(player) ? (Color)this.enemyPlayersLineColor.get() : (Color)this.playersLineColor.get();
         }
      } else {
         SettingColor var10000;
         switch(entity.method_5864().method_5891()) {
         case field_6294:
            var10000 = (SettingColor)this.animalsLineColor.get();
            break;
         case field_24460:
         case field_6300:
         case field_30092:
         case field_34447:
            var10000 = (SettingColor)this.waterAnimalsLineColor.get();
            break;
         case field_6302:
            var10000 = (SettingColor)this.monstersLineColor.get();
            break;
         case field_6303:
            var10000 = (SettingColor)this.ambientLineColor.get();
            break;
         default:
            var10000 = (SettingColor)this.miscLineColor.get();
         }

         return var10000;
      }
   }

   public Color getEntityTypeSideColor(class_1297 entity) {
      if (entity instanceof class_1657) {
         class_1657 player = (class_1657)entity;
         if (Friends.get().isFriend(player)) {
            return (Color)this.friendPlayersSideColor.get();
         } else {
            return Friends.get().isEnemy(player) ? (Color)this.enemyPlayersSideColor.get() : (Color)this.playersSideColor.get();
         }
      } else {
         SettingColor var10000;
         switch(entity.method_5864().method_5891()) {
         case field_6294:
            var10000 = (SettingColor)this.animalsSideColor.get();
            break;
         case field_24460:
         case field_6300:
         case field_30092:
         case field_34447:
            var10000 = (SettingColor)this.waterAnimalsSideColor.get();
            break;
         case field_6302:
            var10000 = (SettingColor)this.monstersSideColor.get();
            break;
         case field_6303:
            var10000 = (SettingColor)this.ambientSideColor.get();
            break;
         default:
            var10000 = (SettingColor)this.miscSideColor.get();
         }

         return var10000;
      }
   }

   private double getFadeAlpha(class_1297 entity) {
      double dist = PlayerUtils.squaredDistanceToCamera(entity.method_23317() + (double)(entity.method_17681() / 2.0F), entity.method_23318() + (double)entity.method_18381(entity.method_18376()), entity.method_23321() + (double)(entity.method_17681() / 2.0F));
      double fadeDist = Math.pow((Double)this.fadeDistance.get(), 2.0D);
      double alpha = 1.0D;
      if (dist <= fadeDist * fadeDist) {
         alpha = (double)((float)(Math.sqrt(dist) / fadeDist));
      }

      if (alpha <= 0.075D) {
         alpha = 0.0D;
      }

      return alpha;
   }

   public String getInfoString() {
      return Integer.toString(this.count);
   }

   public boolean isShader() {
      return this.isActive() && this.mode.get() == ESP.Mode.Shader;
   }

   public boolean isGlow() {
      return this.isActive() && this.mode.get() == ESP.Mode.Glow;
   }

   public static enum Mode {
      Box,
      Wireframe,
      _2D,
      Shader,
      Glow;

      public String toString() {
         return this == _2D ? "2D" : super.toString();
      }

      // $FF: synthetic method
      private static ESP.Mode[] $values() {
         return new ESP.Mode[]{Box, Wireframe, _2D, Shader, Glow};
      }
   }
}
