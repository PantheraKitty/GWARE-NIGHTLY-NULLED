package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1747;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_3611;
import net.minecraft.class_3612;
import net.minecraft.class_2338.class_2339;

public class LiquidFiller extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgWhitelist;
   private final Setting<LiquidFiller.PlaceIn> placeInLiquids;
   private final Setting<LiquidFiller.Shape> shape;
   private final Setting<Double> range;
   private final Setting<Integer> delay;
   private final Setting<Integer> maxBlocksPerTick;
   private final Setting<LiquidFiller.SortMode> sortMode;
   private final Setting<Boolean> rotate;
   private final Setting<LiquidFiller.ListMode> listMode;
   private final Setting<List<class_2248>> whitelist;
   private final Setting<List<class_2248>> blacklist;
   private final List<class_2339> blocks;
   private int timer;

   public LiquidFiller() {
      super(Categories.World, "liquid-filler", "Places blocks inside of liquid source blocks within range of you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgWhitelist = this.settings.createGroup("Whitelist");
      this.placeInLiquids = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("place-in")).description("What type of liquids to place in.")).defaultValue(LiquidFiller.PlaceIn.Both)).build());
      this.shape = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape")).description("The shape of placing algorithm.")).defaultValue(LiquidFiller.Shape.Sphere)).build());
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The place range.")).defaultValue(4.0D).min(0.0D).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("Delay between actions in ticks.")).defaultValue(0)).min(0).build());
      this.maxBlocksPerTick = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-blocks-per-tick")).description("Maximum blocks to try to place per tick.")).defaultValue(1)).min(1).sliderRange(1, 10).build());
      this.sortMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("sort-mode")).description("The blocks you want to place first.")).defaultValue(LiquidFiller.SortMode.Closest)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically rotates towards the space targeted for filling.")).defaultValue(true)).build());
      this.listMode = this.sgWhitelist.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("list-mode")).description("Selection mode.")).defaultValue(LiquidFiller.ListMode.Whitelist)).build());
      this.whitelist = this.sgWhitelist.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("whitelist")).description("The allowed blocks that it will use to fill up the liquid.")).defaultValue(class_2246.field_10566, class_2246.field_10445, class_2246.field_10340, class_2246.field_10515, class_2246.field_10508, class_2246.field_10474, class_2246.field_10115).visible(() -> {
         return this.listMode.get() == LiquidFiller.ListMode.Whitelist;
      })).build());
      this.blacklist = this.sgWhitelist.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blacklist")).description("The denied blocks that it not will use to fill up the liquid.")).visible(() -> {
         return this.listMode.get() == LiquidFiller.ListMode.Blacklist;
      })).build());
      this.blocks = new ArrayList();
   }

   public void onActivate() {
      this.timer = 0;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.timer < (Integer)this.delay.get()) {
         ++this.timer;
      } else {
         this.timer = 0;
         double pX = this.mc.field_1724.method_23317();
         double pY = this.mc.field_1724.method_23318();
         double pZ = this.mc.field_1724.method_23321();
         double rangeSq = Math.pow((Double)this.range.get(), 2.0D);
         if (this.shape.get() == LiquidFiller.Shape.UniformCube) {
            this.range.set((double)Math.round((Double)this.range.get()));
         }

         FindItemResult item;
         if (this.listMode.get() == LiquidFiller.ListMode.Whitelist) {
            item = InvUtils.findInHotbar((itemStack) -> {
               return itemStack.method_7909() instanceof class_1747 && ((List)this.whitelist.get()).contains(class_2248.method_9503(itemStack.method_7909()));
            });
         } else {
            item = InvUtils.findInHotbar((itemStack) -> {
               return itemStack.method_7909() instanceof class_1747 && !((List)this.blacklist.get()).contains(class_2248.method_9503(itemStack.method_7909()));
            });
         }

         if (item.found()) {
            BlockIterator.register((int)Math.ceil((Double)this.range.get() + 1.0D), (int)Math.ceil((Double)this.range.get()), (blockPos, blockState) -> {
               boolean toofarSphere = Utils.squaredDistance(pX, pY, pZ, (double)blockPos.method_10263() + 0.5D, (double)blockPos.method_10264() + 0.5D, (double)blockPos.method_10260() + 0.5D) > rangeSq;
               boolean toofarUniformCube = maxDist(Math.floor(pX), Math.floor(pY), Math.floor(pZ), (double)blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260()) >= (Double)this.range.get();
               if ((!toofarSphere || this.shape.get() != LiquidFiller.Shape.Sphere) && (!toofarUniformCube || this.shape.get() != LiquidFiller.Shape.UniformCube)) {
                  class_3611 fluid = blockState.method_26227().method_15772();
                  if ((this.placeInLiquids.get() != LiquidFiller.PlaceIn.Both || fluid == class_3612.field_15910 || fluid == class_3612.field_15908) && (this.placeInLiquids.get() != LiquidFiller.PlaceIn.Water || fluid == class_3612.field_15910) && (this.placeInLiquids.get() != LiquidFiller.PlaceIn.Lava || fluid == class_3612.field_15908)) {
                     if (BlockUtils.canPlace(blockPos)) {
                        this.blocks.add(blockPos.method_25503());
                     }
                  }
               }
            });
            BlockIterator.after(() -> {
               if (this.sortMode.get() != LiquidFiller.SortMode.TopDown && this.sortMode.get() != LiquidFiller.SortMode.BottomUp) {
                  if (this.sortMode.get() != LiquidFiller.SortMode.None) {
                     this.blocks.sort(Comparator.comparingDouble((value) -> {
                        return Utils.squaredDistance(pX, pY, pZ, (double)value.method_10263() + 0.5D, (double)value.method_10264() + 0.5D, (double)value.method_10260() + 0.5D) * (double)(this.sortMode.get() == LiquidFiller.SortMode.Closest ? 1 : -1);
                     }));
                  }
               } else {
                  this.blocks.sort(Comparator.comparingDouble((value) -> {
                     return (double)(value.method_10264() * (this.sortMode.get() == LiquidFiller.SortMode.BottomUp ? 1 : -1));
                  }));
               }

               int count = 0;

               for(Iterator var9 = this.blocks.iterator(); var9.hasNext(); ++count) {
                  class_2338 pos = (class_2338)var9.next();
                  if (count >= (Integer)this.maxBlocksPerTick.get()) {
                     break;
                  }

                  BlockUtils.place(pos, item, (Boolean)this.rotate.get(), 0, true);
               }

               this.blocks.clear();
            });
         }
      }
   }

   private static double maxDist(double x1, double y1, double z1, double x2, double y2, double z2) {
      double dX = Math.ceil(Math.abs(x2 - x1));
      double dY = Math.ceil(Math.abs(y2 - y1));
      double dZ = Math.ceil(Math.abs(z2 - z1));
      return Math.max(Math.max(dX, dY), dZ);
   }

   public static enum PlaceIn {
      Both,
      Water,
      Lava;

      // $FF: synthetic method
      private static LiquidFiller.PlaceIn[] $values() {
         return new LiquidFiller.PlaceIn[]{Both, Water, Lava};
      }
   }

   public static enum Shape {
      Sphere,
      UniformCube;

      // $FF: synthetic method
      private static LiquidFiller.Shape[] $values() {
         return new LiquidFiller.Shape[]{Sphere, UniformCube};
      }
   }

   public static enum SortMode {
      None,
      Closest,
      Furthest,
      TopDown,
      BottomUp;

      // $FF: synthetic method
      private static LiquidFiller.SortMode[] $values() {
         return new LiquidFiller.SortMode[]{None, Closest, Furthest, TopDown, BottomUp};
      }
   }

   public static enum ListMode {
      Whitelist,
      Blacklist;

      // $FF: synthetic method
      private static LiquidFiller.ListMode[] $values() {
         return new LiquidFiller.ListMode[]{Whitelist, Blacklist};
      }
   }
}
