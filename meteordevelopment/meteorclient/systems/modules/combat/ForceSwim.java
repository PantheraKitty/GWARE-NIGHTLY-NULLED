package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
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
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2350.class_2353;

public class ForceSwim extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Integer> range;
   private final Setting<SortPriority> priority;
   private final Setting<Boolean> pauseEat;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private class_1657 target;

   public ForceSwim() {
      super(Categories.Combat, "force-swim", "Tries to prevent people from standing up while swiming");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("whitelist")).description("Which blocks to use.")).defaultValue(class_2246.field_10540, class_2246.field_22108).build());
      this.range = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("target-range")).description("The range players can be targeted.")).defaultValue(4)).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to select the player to target.")).defaultValue(SortPriority.LowestHealth)).build());
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-eat")).description("Pauses while eating.")).defaultValue(true)).build());
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

      if (this.target != null && this.target.method_20448()) {
         if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
            class_1792 useItem = this.findUseItem();
            if (useItem != null) {
               List<class_2338> placePoses = this.getBlockPoses();
               if (MeteorClient.BLOCK.beginPlacement(placePoses, useItem)) {
                  placePoses.forEach((blockPos) -> {
                     boolean isCrystalBlock = false;
                     Iterator var3 = class_2353.field_11062.iterator();

                     while(var3.hasNext()) {
                        class_2350 dir = (class_2350)var3.next();
                        if (blockPos.equals(this.target.method_24515().method_10093(dir))) {
                           isCrystalBlock = true;
                        }
                     }

                     if (!isCrystalBlock) {
                        MeteorClient.BLOCK.placeBlock(class_1802.field_8281, blockPos);
                     }
                  });
                  MeteorClient.BLOCK.endPlacement();
               }
            }
         }
      }
   }

   private class_1792 findUseItem() {
      FindItemResult result = InvUtils.findInHotbar((itemStack) -> {
         Iterator var2 = ((List)this.blocks.get()).iterator();

         class_2248 blocks;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            blocks = (class_2248)var2.next();
         } while(blocks.method_8389() != itemStack.method_7909());

         return true;
      });
      return !result.found() ? null : this.mc.field_1724.method_31548().method_5438(result.slot()).method_7909();
   }

   private List<class_2338> getBlockPoses() {
      List<class_2338> list = new ArrayList();
      class_238 boundingBox = this.target.method_5829().method_1009(0.7D, 0.0D, 0.7D);
      double feetY = this.target.method_23318();
      class_238 feetBox = new class_238(boundingBox.field_1323, feetY, boundingBox.field_1321, boundingBox.field_1320, feetY + 0.1D, boundingBox.field_1324);
      Iterator var6 = class_2338.method_10094((int)Math.floor(feetBox.field_1323), (int)Math.floor(feetBox.field_1322), (int)Math.floor(feetBox.field_1321), (int)Math.floor(feetBox.field_1320), (int)Math.floor(feetBox.field_1325), (int)Math.floor(feetBox.field_1324)).iterator();

      while(var6.hasNext()) {
         class_2338 pos = (class_2338)var6.next();
         list.add(pos.method_10069(0, 1, 0));
      }

      return list;
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         if (this.target != null && this.target.method_20448()) {
            List<class_2338> poses = this.getBlockPoses();
            Iterator var3 = poses.iterator();

            while(var3.hasNext()) {
               class_2338 pos = (class_2338)var3.next();
               boolean isCrystalBlock = false;
               Iterator var6 = class_2353.field_11062.iterator();

               while(var6.hasNext()) {
                  class_2350 dir = (class_2350)var6.next();
                  if (pos.equals(this.target.method_24515().method_10093(dir))) {
                     isCrystalBlock = true;
                  }
               }

               if (!isCrystalBlock && BlockUtils.canPlace(pos, true)) {
                  event.renderer.box((class_2338)pos, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
               }
            }

         }
      }
   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }
}
