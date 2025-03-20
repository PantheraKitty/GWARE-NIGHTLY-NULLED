package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.AbstractBlockAccessor;
import meteordevelopment.meteorclient.mixininterface.IBox;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1541;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2338.class_2339;

public class HoleFiller extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSmart;
   private final SettingGroup sgRender;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Integer> searchRadius;
   private final Setting<Double> placeRange;
   private final Setting<Boolean> doubles;
   private final Setting<Boolean> rotate;
   private final Setting<Integer> placeDelay;
   private final Setting<Integer> blocksPerTick;
   private final Setting<Boolean> smart;
   public final Setting<Keybind> forceFill;
   private final Setting<Boolean> predict;
   private final Setting<Boolean> ignoreSafe;
   private final Setting<Boolean> onlyMoving;
   private final Setting<Double> targetRange;
   private final Setting<Double> feetRange;
   private final Setting<Boolean> swing;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<SettingColor> nextSideColor;
   private final Setting<SettingColor> nextLineColor;
   private final List<class_1657> targets;
   private final List<HoleFiller.Hole> holes;
   private final class_2339 testPos;
   private final class_238 box;
   private int timer;

   public HoleFiller() {
      super(Categories.Combat, "hole-filler", "Fills holes with specified blocks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSmart = this.settings.createGroup("Smart");
      this.sgRender = this.settings.createGroup("Render");
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("Which blocks can be used to fill holes.")).defaultValue(class_2246.field_10540, class_2246.field_22423, class_2246.field_22108, class_2246.field_23152, class_2246.field_10343).build());
      this.searchRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("search-radius")).description("Horizontal radius in which to search for holes.")).defaultValue(5)).min(0).sliderMax(6).build());
      this.placeRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("How far away from the player you can place a block.")).defaultValue(4.5D).min(0.0D).sliderMax(6.0D).build());
      this.doubles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("doubles")).description("Fills double holes.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically rotates towards the holes being filled.")).defaultValue(false)).build());
      this.placeDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("The ticks delay between placement.")).defaultValue(1)).min(0).build());
      this.blocksPerTick = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("blocks-per-tick")).description("How many blocks to place in one tick.")).defaultValue(3)).min(1).build());
      this.smart = this.sgSmart.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smart")).description("Take more factors into account before filling a hole.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgSmart;
      KeybindSetting.Builder var10002 = (KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-fill")).description("Fills all holes around you regardless of target checks.")).defaultValue(Keybind.none());
      Setting var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.forceFill = var10001.add(((KeybindSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgSmart;
      BoolSetting.Builder var1 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict")).description("Predict target movement to account for ping.")).defaultValue(true);
      var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.predict = var10001.add(((BoolSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgSmart;
      var1 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-safe")).description("Ignore players in safe holes.")).defaultValue(true);
      var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.ignoreSafe = var10001.add(((BoolSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgSmart;
      var1 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-moving")).description("Ignore players if they're standing still.")).defaultValue(true);
      var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.onlyMoving = var10001.add(((BoolSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgSmart;
      DoubleSetting.Builder var2 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("How far away to target players.")).defaultValue(7.0D).min(0.0D).sliderMin(1.0D).sliderMax(10.0D);
      var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.targetRange = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgSmart;
      var2 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("feet-range")).description("How far from a hole a player's feet must be to fill it.")).defaultValue(1.5D).min(0.0D).sliderMax(4.0D);
      var10003 = this.smart;
      Objects.requireNonNull(var10003);
      this.feetRange = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).build());
      this.swing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Swing the player's hand when placing.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var3 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both);
      var10003 = this.render;
      Objects.requireNonNull(var10003);
      this.shapeMode = var10001.add(((EnumSetting.Builder)var3.visible(var10003::get)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232, 10)).visible(() -> {
         return (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).sides();
      })).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232)).visible(() -> {
         return (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).lines();
      })).build());
      this.nextSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-side-color")).description("The side color of the next block to be placed.")).defaultValue(new SettingColor(227, 196, 245, 10)).visible(() -> {
         return (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).sides();
      })).build());
      this.nextLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-line-color")).description("The line color of the next block to be placed.")).defaultValue(new SettingColor(227, 196, 245)).visible(() -> {
         return (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).lines();
      })).build());
      this.targets = new ArrayList();
      this.holes = new ArrayList();
      this.testPos = new class_2339();
      this.box = new class_238(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   }

   public void onActivate() {
      this.timer = 0;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if ((Boolean)this.smart.get()) {
         this.setTargets();
      }

      this.holes.clear();
      FindItemResult block = InvUtils.findInHotbar((itemStack) -> {
         return ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()));
      });
      if (block.found()) {
         BlockIterator.register((Integer)this.searchRadius.get(), (Integer)this.searchRadius.get(), (blockPos, blockState) -> {
            if (this.validHole(blockPos)) {
               int bedrock = 0;
               int obsidian = 0;
               class_2350 air = null;
               class_2350[] var6 = class_2350.values();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  class_2350 direction = var6[var8];
                  if (direction != class_2350.field_11036) {
                     class_2680 state = this.mc.field_1687.method_8320(blockPos.method_10093(direction));
                     if (state.method_26204() == class_2246.field_9987) {
                        ++bedrock;
                     } else if (state.method_26204() == class_2246.field_10540) {
                        ++obsidian;
                     } else {
                        if (direction == class_2350.field_11033) {
                           return;
                        }

                        if (this.validHole(blockPos.method_10093(direction)) && air == null) {
                           class_2350[] var11 = class_2350.values();
                           int var12 = var11.length;

                           for(int var13 = 0; var13 < var12; ++var13) {
                              class_2350 dir = var11[var13];
                              if (dir != direction.method_10153() && dir != class_2350.field_11036) {
                                 class_2680 blockState1 = this.mc.field_1687.method_8320(blockPos.method_10093(direction).method_10093(dir));
                                 if (blockState1.method_26204() == class_2246.field_9987) {
                                    ++bedrock;
                                 } else {
                                    if (blockState1.method_26204() != class_2246.field_10540) {
                                       return;
                                    }

                                    ++obsidian;
                                 }
                              }
                           }

                           air = direction;
                        }
                     }

                     if (obsidian + bedrock == 5 && air == null) {
                        this.holes.add(new HoleFiller.Hole(blockPos, (byte)0));
                     } else if (obsidian + bedrock == 8 && (Boolean)this.doubles.get() && air != null) {
                        this.holes.add(new HoleFiller.Hole(blockPos, Dir.get(air)));
                     }
                  }
               }

            }
         });
         BlockIterator.after(() -> {
            if (this.timer <= 0 && !this.holes.isEmpty()) {
               int bpt = 0;
               Iterator var3 = this.holes.iterator();

               while(var3.hasNext()) {
                  HoleFiller.Hole hole = (HoleFiller.Hole)var3.next();
                  if (bpt < (Integer)this.blocksPerTick.get() && BlockUtils.place(hole.blockPos, block, (Boolean)this.rotate.get(), 10, (Boolean)this.swing.get(), true)) {
                     ++bpt;
                  }
               }

               this.timer = (Integer)this.placeDelay.get();
            }
         });
         --this.timer;
      }
   }

   @EventHandler(
      priority = 100
   )
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get() && !this.holes.isEmpty()) {
         Iterator var2 = this.holes.iterator();

         while(var2.hasNext()) {
            HoleFiller.Hole hole = (HoleFiller.Hole)var2.next();
            boolean isNext = false;

            for(int i = 0; i < this.holes.size(); ++i) {
               if (((HoleFiller.Hole)this.holes.get(i)).equals(hole) && i < (Integer)this.blocksPerTick.get()) {
                  isNext = true;
               }
            }

            Color side = isNext ? (Color)this.nextSideColor.get() : (Color)this.sideColor.get();
            Color line = isNext ? (Color)this.nextLineColor.get() : (Color)this.lineColor.get();
            event.renderer.box((class_2338)hole.blockPos, side, line, (ShapeMode)this.shapeMode.get(), hole.exclude);
         }

      }
   }

   private boolean validHole(class_2338 pos) {
      this.testPos.method_10101(pos);
      if (this.mc.field_1724.method_24515().equals(this.testPos)) {
         return false;
      } else if (this.distance(this.mc.field_1724, this.testPos, false) > (Double)this.placeRange.get()) {
         return false;
      } else if (this.mc.field_1687.method_8320(this.testPos).method_26204() == class_2246.field_10343) {
         return false;
      } else if (((AbstractBlockAccessor)this.mc.field_1687.method_8320(this.testPos).method_26204()).isCollidable()) {
         return false;
      } else {
         this.testPos.method_10069(0, 1, 0);
         if (((AbstractBlockAccessor)this.mc.field_1687.method_8320(this.testPos).method_26204()).isCollidable()) {
            return false;
         } else {
            this.testPos.method_10069(0, -1, 0);
            ((IBox)this.box).set(pos);
            if (!this.mc.field_1687.method_8333((class_1297)null, this.box, (entity) -> {
               return entity instanceof class_1657 || entity instanceof class_1541 || entity instanceof class_1511;
            }).isEmpty()) {
               return false;
            } else {
               return (Boolean)this.smart.get() && !((Keybind)this.forceFill.get()).isPressed() ? this.targets.stream().anyMatch((target) -> {
                  return target.method_23318() > (double)this.testPos.method_10264() && this.distance(target, this.testPos, true) < (Double)this.feetRange.get();
               }) : true;
            }
         }
      }
   }

   private void setTargets() {
      this.targets.clear();
      Iterator var1 = this.mc.field_1687.method_18456().iterator();

      while(true) {
         class_1657 player;
         do {
            do {
               do {
                  do {
                     do {
                        do {
                           do {
                              if (!var1.hasNext()) {
                                 return;
                              }

                              player = (class_1657)var1.next();
                           } while(player.method_5858(this.mc.field_1724) > Math.pow((Double)this.targetRange.get(), 2.0D));
                        } while(player.method_7337());
                     } while(player == this.mc.field_1724);
                  } while(player.method_29504());
               } while(!Friends.get().shouldAttack(player));
            } while((Boolean)this.ignoreSafe.get() && this.isSurrounded(player));
         } while((Boolean)this.onlyMoving.get() && (player.method_23317() - player.field_6014 != 0.0D || player.method_23318() - player.field_6036 != 0.0D || player.method_23321() - player.field_5969 != 0.0D));

         this.targets.add(player);
      }
   }

   private boolean isSurrounded(class_1657 target) {
      class_2350[] var2 = class_2350.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         class_2350 dir = var2[var4];
         if (dir != class_2350.field_11036 && dir != class_2350.field_11033) {
            this.testPos.method_10101(target.method_24515().method_10093(dir));
            class_2248 block = this.mc.field_1687.method_8320(this.testPos).method_26204();
            if (block != class_2246.field_10540 && block != class_2246.field_9987 && block != class_2246.field_23152 && block != class_2246.field_22423 && block != class_2246.field_22108) {
               return false;
            }
         }
      }

      return true;
   }

   private double distance(class_1657 player, class_2338 pos, boolean feet) {
      class_243 testVec = player.method_19538();
      if (!feet) {
         testVec.method_1031(0.0D, (double)player.method_18381(this.mc.field_1724.method_18376()), 0.0D);
      } else if ((Boolean)this.predict.get()) {
         testVec.method_1031(player.method_23317() - player.field_6014, player.method_23318() - player.field_6036, player.method_23321() - player.field_5969);
      }

      double i = testVec.field_1352 - ((double)pos.method_10263() + 0.5D);
      double j = testVec.field_1351 - ((double)pos.method_10264() + (feet ? 1.0D : 0.5D));
      double k = testVec.field_1350 - ((double)pos.method_10260() + 0.5D);
      return Math.sqrt(i * i + j * j + k * k);
   }

   private static class Hole {
      private final class_2339 blockPos = new class_2339();
      private final byte exclude;

      public Hole(class_2338 blockPos, byte exclude) {
         this.blockPos.method_10101(blockPos);
         this.exclude = exclude;
      }
   }
}
