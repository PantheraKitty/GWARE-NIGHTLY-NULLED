package meteordevelopment.meteorclient.systems.modules.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.ProjectileEntitySimulator;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1676;
import net.minecraft.class_1764;
import net.minecraft.class_1771;
import net.minecraft.class_1776;
import net.minecraft.class_1779;
import net.minecraft.class_1787;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1811;
import net.minecraft.class_1823;
import net.minecraft.class_1835;
import net.minecraft.class_1893;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4537;
import net.minecraft.class_7923;
import net.minecraft.class_9239;
import net.minecraft.class_239.class_240;
import org.joml.Vector3d;

public class Trajectories extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<List<class_1792>> items;
   private final Setting<Boolean> otherPlayers;
   private final Setting<Boolean> firedProjectiles;
   private final Setting<Boolean> accurate;
   public final Setting<Integer> simulationSteps;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Boolean> renderPositionBox;
   private final Setting<Double> positionBoxSize;
   private final Setting<SettingColor> positionSideColor;
   private final Setting<SettingColor> positionLineColor;
   private final ProjectileEntitySimulator simulator;
   private final Pool<Vector3d> vec3s;
   private final List<Trajectories.Path> paths;
   private static final double MULTISHOT_OFFSET = Math.toRadians(10.0D);

   public Trajectories() {
      super(Categories.Render, "trajectories", "Predicts the trajectory of throwable items.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.items = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("items")).description("Items to display trajectories for.")).defaultValue(this.getDefaultItems())).filter(this::itemFilter).build());
      this.otherPlayers = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("other-players")).description("Calculates trajectories for other players.")).defaultValue(true)).build());
      this.firedProjectiles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fired-projectiles")).description("Calculates trajectories for already fired projectiles.")).defaultValue(false)).build());
      this.accurate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("accurate")).description("Whether or not to calculate more accurate.")).defaultValue(false)).build());
      this.simulationSteps = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("simulation-steps")).description("How many steps to simulate projectiles. Zero for no limit")).defaultValue(500)).sliderMax(5000).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(255, 150, 0, 35)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(255, 150, 0)).build());
      this.renderPositionBox = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-position-boxes")).description("Renders the actual position the projectile will be at each tick along it's trajectory.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgRender;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("position-box-size")).description("The size of the box drawn at the simulated positions.")).defaultValue(0.02D).sliderRange(0.01D, 0.1D);
      Setting var10003 = this.renderPositionBox;
      Objects.requireNonNull(var10003);
      this.positionBoxSize = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgRender;
      ColorSetting.Builder var1 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("position-side-color")).description("The side color.")).defaultValue(new SettingColor(255, 150, 0, 35));
      var10003 = this.renderPositionBox;
      Objects.requireNonNull(var10003);
      this.positionSideColor = var10001.add(((ColorSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var1 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("position-line-color")).description("The line color.")).defaultValue(new SettingColor(255, 150, 0));
      var10003 = this.renderPositionBox;
      Objects.requireNonNull(var10003);
      this.positionLineColor = var10001.add(((ColorSetting.Builder)var1.visible(var10003::get)).build());
      this.simulator = new ProjectileEntitySimulator();
      this.vec3s = new Pool(Vector3d::new);
      this.paths = new ArrayList();
   }

   private boolean itemFilter(class_1792 item) {
      return item instanceof class_1811 || item instanceof class_1787 || item instanceof class_1835 || item instanceof class_1823 || item instanceof class_1771 || item instanceof class_1776 || item instanceof class_1779 || item instanceof class_4537 || item instanceof class_9239;
   }

   private List<class_1792> getDefaultItems() {
      List<class_1792> items = new ArrayList();
      Iterator var2 = class_7923.field_41178.iterator();

      while(var2.hasNext()) {
         class_1792 item = (class_1792)var2.next();
         if (this.itemFilter(item)) {
            items.add(item);
         }
      }

      return items;
   }

   private Trajectories.Path getEmptyPath() {
      Iterator var1 = this.paths.iterator();

      Trajectories.Path path;
      do {
         if (!var1.hasNext()) {
            Trajectories.Path path = new Trajectories.Path();
            this.paths.add(path);
            return path;
         }

         path = (Trajectories.Path)var1.next();
      } while(!path.points.isEmpty());

      return path;
   }

   private void calculatePath(class_1657 player, float tickDelta) {
      Iterator var3 = this.paths.iterator();

      while(var3.hasNext()) {
         Trajectories.Path path = (Trajectories.Path)var3.next();
         path.clear();
      }

      class_1799 itemStack = player.method_6047();
      if (!((List)this.items.get()).contains(itemStack.method_7909())) {
         itemStack = player.method_6079();
         if (!((List)this.items.get()).contains(itemStack.method_7909())) {
            return;
         }
      }

      if (this.simulator.set(player, itemStack, 0.0D, (Boolean)this.accurate.get(), tickDelta)) {
         this.getEmptyPath().calculate();
         if (itemStack.method_7909() instanceof class_1764 && Utils.hasEnchantment(itemStack, class_1893.field_9108)) {
            if (!this.simulator.set(player, itemStack, MULTISHOT_OFFSET, (Boolean)this.accurate.get(), tickDelta)) {
               return;
            }

            this.getEmptyPath().calculate();
            if (!this.simulator.set(player, itemStack, -MULTISHOT_OFFSET, (Boolean)this.accurate.get(), tickDelta)) {
               return;
            }

            this.getEmptyPath().calculate();
         }

      }
   }

   private void calculateFiredPath(class_1297 entity, double tickDelta) {
      Iterator var4 = this.paths.iterator();

      while(var4.hasNext()) {
         Trajectories.Path path = (Trajectories.Path)var4.next();
         path.clear();
      }

      if (this.simulator.set(entity, (Boolean)this.accurate.get())) {
         this.getEmptyPath().setStart(entity, tickDelta).calculate();
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      float tickDelta = this.mc.field_1687.method_54719().method_54754() ? 1.0F : event.tickDelta;
      Iterator var3 = this.mc.field_1687.method_18456().iterator();

      while(true) {
         class_1657 player;
         Iterator var5;
         Trajectories.Path path;
         do {
            if (!var3.hasNext()) {
               if ((Boolean)this.firedProjectiles.get()) {
                  var3 = this.mc.field_1687.method_18112().iterator();

                  while(true) {
                     class_1297 entity;
                     do {
                        if (!var3.hasNext()) {
                           return;
                        }

                        entity = (class_1297)var3.next();
                     } while(!(entity instanceof class_1676));

                     this.calculateFiredPath(entity, (double)tickDelta);
                     var5 = this.paths.iterator();

                     while(var5.hasNext()) {
                        path = (Trajectories.Path)var5.next();
                        path.render(event);
                     }
                  }
               }

               return;
            }

            player = (class_1657)var3.next();
         } while(!(Boolean)this.otherPlayers.get() && player != this.mc.field_1724);

         this.calculatePath(player, tickDelta);
         var5 = this.paths.iterator();

         while(var5.hasNext()) {
            path = (Trajectories.Path)var5.next();
            path.render(event);
         }
      }
   }

   private class Path {
      private final List<Vector3d> points = new ArrayList();
      private boolean hitQuad;
      private boolean hitQuadHorizontal;
      private double hitQuadX1;
      private double hitQuadY1;
      private double hitQuadZ1;
      private double hitQuadX2;
      private double hitQuadY2;
      private double hitQuadZ2;
      private class_1297 collidingEntity;
      public Vector3d lastPoint;

      public void clear() {
         Iterator var1 = this.points.iterator();

         while(var1.hasNext()) {
            Vector3d point = (Vector3d)var1.next();
            Trajectories.this.vec3s.free(point);
         }

         this.points.clear();
         this.hitQuad = false;
         this.collidingEntity = null;
         this.lastPoint = null;
      }

      public void calculate() {
         this.addPoint();

         for(int i = 0; i < ((Integer)Trajectories.this.simulationSteps.get() > 0 ? (Integer)Trajectories.this.simulationSteps.get() : Integer.MAX_VALUE); ++i) {
            class_239 result = Trajectories.this.simulator.tick();
            if (result != null) {
               this.processHitResult(result);
               break;
            }

            this.addPoint();
         }

      }

      public Trajectories.Path setStart(class_1297 entity, double tickDelta) {
         this.lastPoint = new Vector3d(class_3532.method_16436(tickDelta, entity.field_6038, entity.method_23317()), class_3532.method_16436(tickDelta, entity.field_5971, entity.method_23318()), class_3532.method_16436(tickDelta, entity.field_5989, entity.method_23321()));
         return this;
      }

      private void addPoint() {
         this.points.add(((Vector3d)Trajectories.this.vec3s.get()).set(Trajectories.this.simulator.pos));
      }

      private void processHitResult(class_239 result) {
         if (result.method_17783() == class_240.field_1332) {
            class_3965 r = (class_3965)result;
            this.hitQuad = true;
            this.hitQuadX1 = r.method_17784().field_1352;
            this.hitQuadY1 = r.method_17784().field_1351;
            this.hitQuadZ1 = r.method_17784().field_1350;
            this.hitQuadX2 = r.method_17784().field_1352;
            this.hitQuadY2 = r.method_17784().field_1351;
            this.hitQuadZ2 = r.method_17784().field_1350;
            if (r.method_17780() != class_2350.field_11036 && r.method_17780() != class_2350.field_11033) {
               if (r.method_17780() != class_2350.field_11043 && r.method_17780() != class_2350.field_11035) {
                  this.hitQuadHorizontal = false;
                  this.hitQuadZ1 -= 0.25D;
                  this.hitQuadY1 -= 0.25D;
                  this.hitQuadZ2 += 0.25D;
                  this.hitQuadY2 += 0.25D;
               } else {
                  this.hitQuadHorizontal = false;
                  this.hitQuadX1 -= 0.25D;
                  this.hitQuadY1 -= 0.25D;
                  this.hitQuadX2 += 0.25D;
                  this.hitQuadY2 += 0.25D;
               }
            } else {
               this.hitQuadHorizontal = true;
               this.hitQuadX1 -= 0.25D;
               this.hitQuadZ1 -= 0.25D;
               this.hitQuadX2 += 0.25D;
               this.hitQuadZ2 += 0.25D;
            }

            this.points.add(Utils.set((Vector3d)Trajectories.this.vec3s.get(), result.method_17784()));
         } else if (result.method_17783() == class_240.field_1331) {
            this.collidingEntity = ((class_3966)result).method_17782();
            this.points.add(Utils.set((Vector3d)Trajectories.this.vec3s.get(), result.method_17784()).add(0.0D, (double)(this.collidingEntity.method_17682() / 2.0F), 0.0D));
         }

      }

      public void render(Render3DEvent event) {
         Vector3d point;
         for(Iterator var2 = this.points.iterator(); var2.hasNext(); this.lastPoint = point) {
            point = (Vector3d)var2.next();
            if (this.lastPoint != null) {
               event.renderer.line(this.lastPoint.x, this.lastPoint.y, this.lastPoint.z, point.x, point.y, point.z, (Color)Trajectories.this.lineColor.get());
               if ((Boolean)Trajectories.this.renderPositionBox.get()) {
                  event.renderer.box(point.x - (Double)Trajectories.this.positionBoxSize.get(), point.y - (Double)Trajectories.this.positionBoxSize.get(), point.z - (Double)Trajectories.this.positionBoxSize.get(), point.x + (Double)Trajectories.this.positionBoxSize.get(), point.y + (Double)Trajectories.this.positionBoxSize.get(), point.z + (Double)Trajectories.this.positionBoxSize.get(), (Color)Trajectories.this.positionSideColor.get(), (Color)Trajectories.this.positionLineColor.get(), (ShapeMode)Trajectories.this.shapeMode.get(), 0);
               }
            }
         }

         if (this.hitQuad) {
            if (this.hitQuadHorizontal) {
               event.renderer.sideHorizontal(this.hitQuadX1, this.hitQuadY1, this.hitQuadZ1, this.hitQuadX1 + 0.5D, this.hitQuadZ1 + 0.5D, (Color)Trajectories.this.sideColor.get(), (Color)Trajectories.this.lineColor.get(), (ShapeMode)Trajectories.this.shapeMode.get());
            } else {
               event.renderer.sideVertical(this.hitQuadX1, this.hitQuadY1, this.hitQuadZ1, this.hitQuadX2, this.hitQuadY2, this.hitQuadZ2, (Color)Trajectories.this.sideColor.get(), (Color)Trajectories.this.lineColor.get(), (ShapeMode)Trajectories.this.shapeMode.get());
            }
         }

         if (this.collidingEntity != null) {
            double x = (this.collidingEntity.method_23317() - this.collidingEntity.field_6014) * (double)event.tickDelta;
            double y = (this.collidingEntity.method_23318() - this.collidingEntity.field_6036) * (double)event.tickDelta;
            double z = (this.collidingEntity.method_23321() - this.collidingEntity.field_5969) * (double)event.tickDelta;
            class_238 box = this.collidingEntity.method_5829();
            event.renderer.box(x + box.field_1323, y + box.field_1322, z + box.field_1321, x + box.field_1320, y + box.field_1325, z + box.field_1324, (Color)Trajectories.this.sideColor.get(), (Color)Trajectories.this.lineColor.get(), (ShapeMode)Trajectories.this.shapeMode.get(), 0);
         }

      }
   }
}
