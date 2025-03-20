package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.BlockBreakingCooldownEvent;
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
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.SilentMine;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2846;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_2846.class_2847;

public class Nuker extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgWhitelist;
   private final SettingGroup sgRender;
   private final Setting<Nuker.Shape> shape;
   private final Setting<Nuker.Mode> mode;
   private final Setting<Double> range;
   private final Setting<Integer> range_up;
   private final Setting<Integer> range_down;
   private final Setting<Integer> range_left;
   private final Setting<Integer> range_right;
   private final Setting<Integer> range_forward;
   private final Setting<Integer> range_back;
   private final Setting<Boolean> silentMine;
   private final Setting<Integer> delay;
   private final Setting<Integer> maxBlocksPerTick;
   private final Setting<Nuker.SortMode> sortMode;
   private final Setting<Boolean> swingHand;
   private final Setting<Boolean> packetMine;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> belowAirScaffold;
   private final Setting<Nuker.ListMode> listMode;
   private final Setting<List<class_2248>> blacklist;
   private final Setting<List<class_2248>> whitelist;
   private final Setting<Boolean> enableRenderBounding;
   private final Setting<ShapeMode> shapeModeBox;
   private final Setting<SettingColor> sideColorBox;
   private final Setting<SettingColor> lineColorBox;
   private final Setting<Boolean> enableRenderBreaking;
   private final Setting<ShapeMode> shapeModeBreak;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final List<class_2338> blocks;
   private boolean firstBlock;
   private final class_2339 lastBlockPos;
   private int timer;
   private int noBlockTimer;
   private final class_2339 pos1;
   private final class_2339 pos2;
   int maxh;
   int maxv;

   public Nuker() {
      super(Categories.World, "nuker", "Breaks blocks around you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgWhitelist = this.settings.createGroup("Whitelist");
      this.sgRender = this.settings.createGroup("Render");
      this.shape = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape")).description("The shape of nuking algorithm.")).defaultValue(Nuker.Shape.Sphere)).build());
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The way the blocks are broken.")).defaultValue(Nuker.Mode.Flatten)).build());
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The break range.")).defaultValue(4.0D).min(0.0D).visible(() -> {
         return this.shape.get() != Nuker.Shape.Cube;
      })).build());
      this.range_up = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("up")).description("The break range.")).defaultValue(1)).min(0).visible(() -> {
         return this.shape.get() == Nuker.Shape.Cube;
      })).build());
      this.range_down = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("down")).description("The break range.")).defaultValue(1)).min(0).visible(() -> {
         return this.shape.get() == Nuker.Shape.Cube;
      })).build());
      this.range_left = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("left")).description("The break range.")).defaultValue(1)).min(0).visible(() -> {
         return this.shape.get() == Nuker.Shape.Cube;
      })).build());
      this.range_right = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("right")).description("The break range.")).defaultValue(1)).min(0).visible(() -> {
         return this.shape.get() == Nuker.Shape.Cube;
      })).build());
      this.range_forward = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("forward")).description("The break range.")).defaultValue(1)).min(0).visible(() -> {
         return this.shape.get() == Nuker.Shape.Cube;
      })).build());
      this.range_back = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("back")).description("The break range.")).defaultValue(1)).min(0).visible(() -> {
         return this.shape.get() == Nuker.Shape.Cube;
      })).build());
      this.silentMine = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("silent-mine")).description("Uses SilentMine to break/double-break")).defaultValue(true)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("Delay in ticks between breaking blocks.")).defaultValue(0)).visible(() -> {
         return !(Boolean)this.silentMine.get();
      })).build());
      this.maxBlocksPerTick = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-blocks-per-tick")).description("Maximum blocks to try to break per tick. Useful when insta mining.")).defaultValue(1)).min(1).sliderRange(1, 6).visible(() -> {
         return !(Boolean)this.silentMine.get();
      })).build());
      this.sortMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("sort-mode")).description("The blocks you want to mine first.")).defaultValue(Nuker.SortMode.Closest)).build());
      this.swingHand = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing-hand")).description("Swing hand client side.")).defaultValue(true)).build());
      this.packetMine = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("packet-mine")).description("Attempt to instamine everything at once.")).defaultValue(false)).visible(() -> {
         return !(Boolean)this.silentMine.get();
      })).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates server-side to the block being mined.")).defaultValue(true)).visible(() -> {
         return !(Boolean)this.silentMine.get();
      })).build());
      this.belowAirScaffold = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("below-air-scaffold")).description("Scaffolds one block below you, to prevent you from failling. Useful for clearing large areas vertically.")).defaultValue(false)).build());
      this.listMode = this.sgWhitelist.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("list-mode")).description("Selection mode.")).defaultValue(Nuker.ListMode.Blacklist)).build());
      this.blacklist = this.sgWhitelist.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blacklist")).description("The blocks you don't want to mine.")).visible(() -> {
         return this.listMode.get() == Nuker.ListMode.Blacklist;
      })).build());
      this.whitelist = this.sgWhitelist.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("whitelist")).description("The blocks you want to mine.")).visible(() -> {
         return this.listMode.get() == Nuker.ListMode.Whitelist;
      })).build());
      this.enableRenderBounding = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("bounding-box")).description("Enable rendering bounding box for Cube and Uniform Cube.")).defaultValue(true)).build());
      this.shapeModeBox = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("nuke-box-mode")).description("How the shape for the bounding box is rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColorBox = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the bounding box.")).defaultValue(new SettingColor(16, 106, 144, 100)).build());
      this.lineColorBox = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the bounding box.")).defaultValue(new SettingColor(16, 106, 144, 255)).build());
      this.enableRenderBreaking = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("broken-blocks")).description("Enable rendering bounding box for Cube and Uniform Cube.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("nuke-block-mode")).description("How the shapes for broken blocks are rendered.")).defaultValue(ShapeMode.Both);
      Setting var10003 = this.enableRenderBreaking;
      Objects.requireNonNull(var10003);
      this.shapeModeBreak = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgRender;
      ColorSetting.Builder var1 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(255, 0, 0, 80));
      var10003 = this.enableRenderBreaking;
      Objects.requireNonNull(var10003);
      this.sideColor = var10001.add(((ColorSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var1 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(255, 0, 0, 255));
      var10003 = this.enableRenderBreaking;
      Objects.requireNonNull(var10003);
      this.lineColor = var10001.add(((ColorSetting.Builder)var1.visible(var10003::get)).build());
      this.blocks = new ArrayList();
      this.lastBlockPos = new class_2339();
      this.pos1 = new class_2339();
      this.pos2 = new class_2339();
      this.maxh = 0;
      this.maxv = 0;
   }

   public void onActivate() {
      this.firstBlock = true;
      this.timer = 0;
      this.noBlockTimer = 0;
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.enableRenderBounding.get() && this.shape.get() != Nuker.Shape.Sphere && this.mode.get() != Nuker.Mode.Smash) {
         int minX = Math.min(this.pos1.method_10263(), this.pos2.method_10263());
         int minY = Math.min(this.pos1.method_10264(), this.pos2.method_10264());
         int minZ = Math.min(this.pos1.method_10260(), this.pos2.method_10260());
         int maxX = Math.max(this.pos1.method_10263(), this.pos2.method_10263());
         int maxY = Math.max(this.pos1.method_10264(), this.pos2.method_10264());
         int maxZ = Math.max(this.pos1.method_10260(), this.pos2.method_10260());
         event.renderer.box((double)minX, (double)minY, (double)minZ, (double)maxX, (double)maxY, (double)maxZ, (Color)this.sideColorBox.get(), (Color)this.lineColorBox.get(), (ShapeMode)this.shapeModeBox.get(), 0);
      }

   }

   @EventHandler
   private void onTickPre(TickEvent.Pre event) {
      if (this.timer > 0) {
         --this.timer;
      } else {
         double pX = this.mc.field_1724.method_23317();
         double pY = this.mc.field_1724.method_23318();
         double pZ = this.mc.field_1724.method_23321();
         double rangeSq = Math.pow((Double)this.range.get(), 2.0D);
         if (this.shape.get() == Nuker.Shape.UniformCube) {
            this.range.set((double)Math.round((Double)this.range.get()));
         }

         int r = (int)Math.round((Double)this.range.get());
         double pX_;
         if (this.shape.get() == Nuker.Shape.UniformCube) {
            pX_ = pX + 1.0D;
            this.pos1.method_10102(pX_ - (double)r, pY - (double)r + 1.0D, pZ - (double)r + 1.0D);
            this.pos2.method_10102(pX_ + (double)r - 1.0D, pY + (double)r, pZ + (double)r);
         } else {
            int direction = Math.round(this.mc.field_1724.method_5802().field_1342 % 360.0F / 90.0F);
            direction = Math.floorMod(direction, 4);
            this.pos1.method_10102(pX - (double)(Integer)this.range_forward.get(), Math.ceil(pY) - (double)(Integer)this.range_down.get(), pZ - (double)(Integer)this.range_right.get());
            this.pos2.method_10102(pX + (double)(Integer)this.range_back.get() + 1.0D, Math.ceil(pY + (double)(Integer)this.range_up.get() + 1.0D), pZ + (double)(Integer)this.range_left.get() + 1.0D);
            double pZ_;
            switch(direction) {
            case 0:
               pZ_ = pZ + 1.0D;
               pX_ = pX + 1.0D;
               this.pos1.method_10102(pX_ - (double)((Integer)this.range_right.get() + 1), Math.ceil(pY) - (double)(Integer)this.range_down.get(), pZ_ - (double)((Integer)this.range_back.get() + 1));
               this.pos2.method_10102(pX_ + (double)(Integer)this.range_left.get(), Math.ceil(pY + (double)(Integer)this.range_up.get() + 1.0D), pZ_ + (double)(Integer)this.range_forward.get());
            case 1:
            default:
               break;
            case 2:
               pX_ = pX + 1.0D;
               pZ_ = pZ + 1.0D;
               this.pos1.method_10102(pX_ - (double)((Integer)this.range_left.get() + 1), Math.ceil(pY) - (double)(Integer)this.range_down.get(), pZ_ - (double)((Integer)this.range_forward.get() + 1));
               this.pos2.method_10102(pX_ + (double)(Integer)this.range_right.get(), Math.ceil(pY + (double)(Integer)this.range_up.get() + 1.0D), pZ_ + (double)(Integer)this.range_back.get());
               break;
            case 3:
               pX_ = pX + 1.0D;
               this.pos1.method_10102(pX_ - (double)((Integer)this.range_back.get() + 1), Math.ceil(pY) - (double)(Integer)this.range_down.get(), pZ - (double)(Integer)this.range_left.get());
               this.pos2.method_10102(pX_ + (double)(Integer)this.range_forward.get(), Math.ceil(pY + (double)(Integer)this.range_up.get() + 1.0D), pZ + (double)(Integer)this.range_right.get() + 1.0D);
            }

            this.maxh = 1 + Math.max(Math.max(Math.max((Integer)this.range_back.get(), (Integer)this.range_right.get()), (Integer)this.range_forward.get()), (Integer)this.range_left.get());
            this.maxv = 1 + Math.max((Integer)this.range_up.get(), (Integer)this.range_down.get());
         }

         if (this.mode.get() == Nuker.Mode.Flatten) {
            this.pos1.method_33098((int)Math.floor(pY));
         }

         class_238 box = new class_238(this.pos1.method_46558(), this.pos2.method_46558());
         BlockIterator.register(Math.max((int)Math.ceil((Double)this.range.get() + 1.0D), this.maxh), Math.max((int)Math.ceil((Double)this.range.get()), this.maxv), (blockPos, blockState) -> {
            switch(((Nuker.Shape)this.shape.get()).ordinal()) {
            case 0:
               if (!box.method_1006(class_243.method_24953(blockPos))) {
                  return;
               }
               break;
            case 1:
               if ((double)chebyshevDist(this.mc.field_1724.method_24515().method_10263(), this.mc.field_1724.method_24515().method_10264(), this.mc.field_1724.method_24515().method_10260(), blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260()) >= (Double)this.range.get()) {
                  return;
               }
               break;
            case 2:
               if (Utils.squaredDistance(pX, pY, pZ, (double)blockPos.method_10263() + 0.5D, (double)blockPos.method_10264() + 0.5D, (double)blockPos.method_10260() + 0.5D) > rangeSq) {
                  return;
               }
            }

            if (BlockUtils.canBreak(blockPos, blockState)) {
               if (this.mode.get() != Nuker.Mode.Flatten || !((double)blockPos.method_10264() < Math.floor(this.mc.field_1724.method_23318()))) {
                  if (this.mode.get() != Nuker.Mode.Smash || blockState.method_26214(this.mc.field_1687, blockPos) == 0.0F) {
                     if (this.listMode.get() != Nuker.ListMode.Whitelist || ((List)this.whitelist.get()).contains(blockState.method_26204())) {
                        if (this.listMode.get() != Nuker.ListMode.Blacklist || !((List)this.blacklist.get()).contains(blockState.method_26204())) {
                           this.blocks.add(blockPos.method_10062());
                        }
                     }
                  }
               }
            }
         });
         BlockIterator.after(() -> {
            if (this.sortMode.get() == Nuker.SortMode.TopDown) {
               this.blocks.sort(Comparator.comparingDouble((value) -> {
                  return (double)(-value.method_10264());
               }));
            } else if (this.sortMode.get() != Nuker.SortMode.None) {
               this.blocks.sort(Comparator.comparingDouble((value) -> {
                  return Utils.squaredDistance(pX, pY, pZ, (double)value.method_10263() + 0.5D, (double)value.method_10264() + 0.5D, (double)value.method_10260() + 0.5D) * (double)(this.sortMode.get() == Nuker.SortMode.Closest ? 1 : -1);
               }));
            }

            if ((Boolean)this.silentMine.get()) {
               SilentMine silentMine = (SilentMine)Modules.get().get(SilentMine.class);
               if (!silentMine.hasDelayedDestroy() && (!silentMine.hasRebreakBlock() || silentMine.canRebreakRebreakBlock())) {
                  int count = 0;

                  for(Iterator var13 = this.blocks.iterator(); var13.hasNext(); ++count) {
                     class_2338 block = (class_2338)var13.next();
                     if (count >= 2) {
                        break;
                     }

                     silentMine.silentBreakBlock(block, class_2350.field_11036, 5.0D);
                  }

                  this.blocks.clear();
               }
            } else if (this.blocks.isEmpty()) {
               if (this.noBlockTimer++ >= (Integer)this.delay.get()) {
                  this.firstBlock = true;
               }

            } else {
               this.noBlockTimer = 0;
               if (!this.firstBlock && !this.lastBlockPos.equals(this.blocks.getFirst())) {
                  this.timer = (Integer)this.delay.get();
                  this.firstBlock = false;
                  this.lastBlockPos.method_10101((class_2382)this.blocks.getFirst());
                  if (this.timer > 0) {
                     return;
                  }
               }

               int countx = 0;
               Iterator var8 = this.blocks.iterator();

               while(var8.hasNext()) {
                  class_2338 blockx = (class_2338)var8.next();
                  if (countx >= (Integer)this.maxBlocksPerTick.get()) {
                     break;
                  }

                  boolean canInstaMine = BlockUtils.canInstaBreak(blockx);
                  if ((Boolean)this.rotate.get()) {
                     Rotations.rotate(Rotations.getYaw(blockx), Rotations.getPitch(blockx), () -> {
                        this.breakBlock(blockx);
                     });
                  } else {
                     this.breakBlock(blockx);
                  }

                  if ((Boolean)this.enableRenderBreaking.get()) {
                     RenderUtils.renderTickingBlock(blockx, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeModeBreak.get(), 0, 8, true, false);
                  }

                  this.lastBlockPos.method_10101(blockx);
                  ++countx;
                  if (!canInstaMine && !(Boolean)this.packetMine.get()) {
                     break;
                  }
               }

               this.firstBlock = false;
               this.blocks.clear();
            }
         });
         if ((Boolean)this.belowAirScaffold.get()) {
            List<class_2338> placePoses = new ArrayList();
            placePoses.add(this.mc.field_1724.method_24515().method_10087(3));
            if (!this.mc.field_1724.method_6115() && MeteorClient.BLOCK.beginPlacement(placePoses, class_1802.field_8281)) {
               placePoses.forEach((blockPos) -> {
                  MeteorClient.BLOCK.placeBlock(class_1802.field_8281, blockPos);
               });
               MeteorClient.BLOCK.endPlacement();
            }
         }

      }
   }

   private void breakBlock(class_2338 blockPos) {
      if ((Boolean)this.packetMine.get()) {
         this.mc.method_1562().method_52787(new class_2846(class_2847.field_12968, blockPos, BlockUtils.getDirection(blockPos)));
         this.mc.field_1724.method_6104(class_1268.field_5808);
         this.mc.method_1562().method_52787(new class_2846(class_2847.field_12973, blockPos, BlockUtils.getDirection(blockPos)));
      } else {
         BlockUtils.breakBlock(blockPos, (Boolean)this.swingHand.get());
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onBlockBreakingCooldown(BlockBreakingCooldownEvent event) {
      event.cooldown = 0;
   }

   public static int chebyshevDist(int x1, int y1, int z1, int x2, int y2, int z2) {
      int dX = Math.abs(x2 - x1);
      int dY = Math.abs(y2 - y1);
      int dZ = Math.abs(z2 - z1);
      return Math.max(Math.max(dX, dY), dZ);
   }

   public static enum Shape {
      Cube,
      UniformCube,
      Sphere;

      // $FF: synthetic method
      private static Nuker.Shape[] $values() {
         return new Nuker.Shape[]{Cube, UniformCube, Sphere};
      }
   }

   public static enum Mode {
      All,
      Flatten,
      Smash;

      // $FF: synthetic method
      private static Nuker.Mode[] $values() {
         return new Nuker.Mode[]{All, Flatten, Smash};
      }
   }

   public static enum SortMode {
      None,
      Closest,
      Furthest,
      TopDown;

      // $FF: synthetic method
      private static Nuker.SortMode[] $values() {
         return new Nuker.SortMode[]{None, Closest, Furthest, TopDown};
      }
   }

   public static enum ListMode {
      Whitelist,
      Blacklist;

      // $FF: synthetic method
      private static Nuker.ListMode[] $values() {
         return new Nuker.ListMode[]{Whitelist, Blacklist};
      }
   }
}
