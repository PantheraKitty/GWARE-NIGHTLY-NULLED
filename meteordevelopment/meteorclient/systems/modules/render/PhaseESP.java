package meteordevelopment.meteorclient.systems.modules.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2680;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class PhaseESP extends Module {
   private final SettingGroup sgRender;
   private final Setting<SettingColor> safeBedrockColor;
   private final Setting<SettingColor> unsafeBedrockColor;
   private final Setting<SettingColor> safeOpenHeadBedrockColor;
   private final Setting<SettingColor> safeObsidianColor;
   private final Setting<SettingColor> unsafesafeObsidianColor;
   private final Setting<SettingColor> openHeadColor;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> lineColor;
   private final Pool<PhaseESP.PhaseBlock> phaseBlockPool;
   private final List<PhaseESP.PhaseBlock> phaseBlocks;

   public PhaseESP() {
      super(Categories.Render, "phase-esp", "Shows you where it's safe to phase.");
      this.sgRender = this.settings.createGroup("Render");
      this.safeBedrockColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("safe-bedrock-color")).description("Bedrock that has a safe block below it")).defaultValue(new SettingColor(150, 0, 255, 50)).build());
      this.unsafeBedrockColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("unsafe-bedrock-color")).description("Bedrock that does not have a safe block below it")).defaultValue(new SettingColor(255, 0, 0, 70)).build());
      this.safeOpenHeadBedrockColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("safe-open-head-bedrock-color")).description("Bedrock that has a safe block below it and an open head")).defaultValue(new SettingColor(135, 160, 20, 50)).build());
      this.safeObsidianColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("safe-obsidian-color")).description("Obsidian that has a safe block below it")).defaultValue(new SettingColor(140, 0, 255, 10)).build());
      this.unsafesafeObsidianColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("unsafe-obsidian-color")).description("Obsidian that does not have a safe block below it")).defaultValue(new SettingColor(255, 0, 0, 30)).build());
      this.openHeadColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("open-head-color")).description("A block where the head is open")).defaultValue(new SettingColor(255, 0, 240, 30)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the rendering.")).defaultValue(new SettingColor(255, 255, 255, 20)).visible(() -> {
         return ((ShapeMode)this.shapeMode.get()).lines();
      })).build());
      this.phaseBlockPool = new Pool(() -> {
         return new PhaseESP.PhaseBlock();
      });
      this.phaseBlocks = new ArrayList();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      Iterator var2 = this.phaseBlocks.iterator();

      while(var2.hasNext()) {
         PhaseESP.PhaseBlock hole = (PhaseESP.PhaseBlock)var2.next();
         this.phaseBlockPool.free(hole);
      }

      this.phaseBlocks.clear();
      class_2338 playerPos = this.mc.field_1724.method_24515();
      class_238 boundingBox = this.mc.field_1724.method_5829().method_1009(0.999D, 0.0D, 0.999D);
      double feetY = this.mc.field_1724.method_23318();
      class_238 feetBox = new class_238(boundingBox.field_1323, feetY, boundingBox.field_1321, boundingBox.field_1320, feetY + 0.1D, boundingBox.field_1324);
      boolean isAccorssMultipleBlocks = false;
      if ((int)Math.floor(feetBox.field_1320) - (int)Math.floor(feetBox.field_1323) >= 1 || (int)Math.floor(feetBox.field_1324) - (int)Math.floor(feetBox.field_1321) >= 1) {
         isAccorssMultipleBlocks = true;
      }

      for(int x = (int)Math.floor(feetBox.field_1323); x <= (int)Math.floor(feetBox.field_1320); ++x) {
         for(int z = (int)Math.floor(feetBox.field_1321); z <= (int)Math.floor(feetBox.field_1324); ++z) {
            class_2338 blockPos = new class_2338(x, playerPos.method_10264(), z);
            if (isAccorssMultipleBlocks || playerPos.method_10263() != x || playerPos.method_10260() != z) {
               class_3965 result = this.mc.field_1687.method_17742(new class_3959(this.mc.field_1724.method_19538().method_1031(0.0D, 0.05D, 0.0D), blockPos.method_61082().method_1031(0.0D, 0.05D, 0.0D), class_3960.field_17559, class_242.field_1348, this.mc.field_1724));
               if (result == null || result.method_17783() == class_240.field_1332) {
                  this.checkBlock(blockPos);
               }
            }
         }
      }

   }

   private void checkBlock(class_2338 pos) {
      class_2680 block = this.mc.field_1687.method_8320(pos);
      class_2680 downBlock = this.mc.field_1687.method_8320(pos.method_10093(class_2350.field_11033));
      class_2680 upBlock = this.mc.field_1687.method_8320(pos.method_10093(class_2350.field_11036));
      if (downBlock != null && block != null) {
         boolean obsidian = block.method_27852(class_2246.field_10540) || block.method_27852(class_2246.field_22423);
         boolean bedrock = block.method_27852(class_2246.field_9987);
         boolean obsidianDown = downBlock.method_27852(class_2246.field_10540) || downBlock.method_27852(class_2246.field_22423);
         boolean bedrockDown = downBlock.method_27852(class_2246.field_9987);
         boolean airUp = upBlock.method_26215();
         boolean bedrockUp = upBlock.method_27852(class_2246.field_9987);
         boolean obsidianUp = upBlock.method_27852(class_2246.field_10540) || upBlock.method_27852(class_2246.field_22423);
         if (bedrock) {
            if (bedrockDown) {
               if (bedrockUp) {
                  this.phaseBlocks.add(((PhaseESP.PhaseBlock)this.phaseBlockPool.get()).set(pos, PhaseESP.PhaseBlock.Type.SafeBedrock));
               } else {
                  this.phaseBlocks.add(((PhaseESP.PhaseBlock)this.phaseBlockPool.get()).set(pos, PhaseESP.PhaseBlock.Type.SafeBedrockOpenHead));
               }
            } else {
               this.phaseBlocks.add(((PhaseESP.PhaseBlock)this.phaseBlockPool.get()).set(pos, PhaseESP.PhaseBlock.Type.UnsafeBedrock));
            }
         } else if (obsidian) {
            if (!obsidianDown && !bedrockDown) {
               this.phaseBlocks.add(((PhaseESP.PhaseBlock)this.phaseBlockPool.get()).set(pos, PhaseESP.PhaseBlock.Type.UnsafeObsidian));
            } else if (airUp) {
               this.phaseBlocks.add(((PhaseESP.PhaseBlock)this.phaseBlockPool.get()).set(pos, PhaseESP.PhaseBlock.Type.OpenHead));
            } else {
               this.phaseBlocks.add(((PhaseESP.PhaseBlock)this.phaseBlockPool.get()).set(pos, PhaseESP.PhaseBlock.Type.SafeObsidian));
            }
         } else if (obsidianUp) {
            this.phaseBlocks.add(((PhaseESP.PhaseBlock)this.phaseBlockPool.get()).set(pos, PhaseESP.PhaseBlock.Type.UnsafeObsidian));
         } else if (airUp && (obsidianDown || bedrockDown)) {
            this.phaseBlocks.add(((PhaseESP.PhaseBlock)this.phaseBlockPool.get()).set(pos, PhaseESP.PhaseBlock.Type.OpenHead));
         }

      }
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      Iterator var2 = this.phaseBlocks.iterator();

      while(var2.hasNext()) {
         PhaseESP.PhaseBlock phaseBlock = (PhaseESP.PhaseBlock)var2.next();
         phaseBlock.render(event.renderer);
      }

   }

   private class PhaseBlock {
      public class_2339 blockPos = new class_2339();
      public PhaseESP.PhaseBlock.Type type;

      public PhaseBlock() {
      }

      public PhaseESP.PhaseBlock set(class_2338 blockPos, PhaseESP.PhaseBlock.Type type) {
         this.blockPos.method_10101(blockPos);
         this.type = type;
         return this;
      }

      public void render(Renderer3D renderer) {
         int x1 = this.blockPos.method_10263();
         int y1 = this.blockPos.method_10264();
         int z1 = this.blockPos.method_10260();
         int x2 = this.blockPos.method_10263() + 1;
         int z2 = this.blockPos.method_10260() + 1;
         SettingColor var10000;
         switch(this.type.ordinal()) {
         case 0:
            var10000 = (SettingColor)PhaseESP.this.safeBedrockColor.get();
            break;
         case 1:
            var10000 = (SettingColor)PhaseESP.this.safeObsidianColor.get();
            break;
         case 2:
            var10000 = (SettingColor)PhaseESP.this.unsafeBedrockColor.get();
            break;
         case 3:
            var10000 = (SettingColor)PhaseESP.this.unsafesafeObsidianColor.get();
            break;
         case 4:
            var10000 = (SettingColor)PhaseESP.this.safeOpenHeadBedrockColor.get();
            break;
         case 5:
            var10000 = (SettingColor)PhaseESP.this.openHeadColor.get();
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
         }

         Color color = var10000;
         renderer.sideHorizontal((double)x1, (double)y1, (double)z1, (double)x2, (double)z2, color, (Color)PhaseESP.this.lineColor.get(), (ShapeMode)PhaseESP.this.shapeMode.get());
      }

      public static enum Type {
         SafeBedrock,
         SafeObsidian,
         UnsafeBedrock,
         UnsafeObsidian,
         SafeBedrockOpenHead,
         OpenHead;

         // $FF: synthetic method
         private static PhaseESP.PhaseBlock.Type[] $values() {
            return new PhaseESP.PhaseBlock.Type[]{SafeBedrock, SafeObsidian, UnsafeBedrock, UnsafeObsidian, SafeBedrockOpenHead, OpenHead};
         }
      }
   }
}
