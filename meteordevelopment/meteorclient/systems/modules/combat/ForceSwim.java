package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BreakIndicators;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;

public class ForceSwim extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Integer> range;
   private final Setting<SortPriority> priority;
   private final Setting<Boolean> pauseEat;
   private final Setting<Integer> bpt;
   private final Setting<Boolean> ignoreItems;
   private final Setting<Boolean> ignoreEntities;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private class_1657 target;

   public ForceSwim() {
      super(Categories.Combat, "force-swim", "Tries to prevent people from standing up while swimming.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("whitelist")).description("Which blocks to use.")).defaultValue(class_2246.field_10540, class_2246.field_22108).build());
      this.range = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("target-range")).description("The range players can be targeted.")).defaultValue(4)).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to select the player to target.")).defaultValue(SortPriority.LowestHealth)).build());
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-eat")).description("Pauses while eating.")).defaultValue(true)).build());
      this.bpt = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("BPT")).description("Blocks placed per tick.")).defaultValue(3)).min(1).max(10).sliderRange(1, 10).build());
      this.ignoreItems = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-items")).description("Ignores item entities when placing blocks.")).defaultValue(false)).build());
      this.ignoreEntities = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-entities")).description("Ignores entities when placing blocks.")).defaultValue(false)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232, 10)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232)).build());
   }

   public void onActivate() {
      this.target = null;
   }

   public void onDeactivate() {
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.target == null || TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get())) {
         this.target = TargetUtils.getPlayerTarget((double)(Integer)this.range.get(), (SortPriority)this.priority.get());
         if (TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get())) {
            return;
         }
      }

      if (this.target != null && this.target.method_5681()) {
         if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
            class_1792 useItem = this.findUseItem();
            if (useItem != null) {
               class_2338 blockAbove = this.target.method_24515().method_10084();
               if (this.mc.field_1687.method_8320(blockAbove).method_26215()) {
                  BreakIndicators breakIndicators = (BreakIndicators)Modules.get().get(BreakIndicators.class);
                  if (breakIndicators != null && breakIndicators.isBlockBeingBroken(blockAbove)) {
                     double breakProgress = ((BreakIndicators.BlockBreak)breakIndicators.breakStartTimes.get(blockAbove)).getBreakProgress(RenderUtils.getCurrentGameTickCalculated());
                     if (breakProgress >= 0.95D) {
                        for(int i = 0; i < (Integer)this.bpt.get(); ++i) {
                           this.placeBlock(blockAbove, useItem);
                        }
                     }
                  } else {
                     for(int i = 0; i < (Integer)this.bpt.get(); ++i) {
                        this.placeBlock(blockAbove, useItem);
                     }
                  }

               }
            }
         }
      }
   }

   private void placeBlock(class_2338 blockPos, class_1792 useItem) {
      if (MeteorClient.BLOCK.beginPlacement(blockPos, this.mc.field_1687.method_8320(blockPos), useItem)) {
         MeteorClient.BLOCK.placeBlock(useItem, blockPos, this.mc.field_1687.method_8320(blockPos));
         MeteorClient.BLOCK.endPlacement();
      }
   }

   private class_1792 findUseItem() {
      FindItemResult result = InvUtils.findInHotbar((itemStack) -> {
         Iterator var2 = ((List)this.blocks.get()).iterator();

         class_2248 block;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            block = (class_2248)var2.next();
         } while(block.method_8389() != itemStack.method_7909());

         return true;
      });
      return !result.found() ? null : this.mc.field_1724.method_31548().method_5438(result.slot()).method_7909();
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         if (this.target != null && this.target.method_5681()) {
            class_2338 blockAbove = this.target.method_24515().method_10084();
            if (BlockUtils.canPlace(blockAbove, true)) {
               event.renderer.box((class_2338)blockAbove, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
            }

         }
      }
   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }
}
