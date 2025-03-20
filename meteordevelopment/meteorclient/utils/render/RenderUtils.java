package meteordevelopment.meteorclient.utils.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import net.minecraft.class_2338.class_2339;
import org.joml.Vector3f;

public class RenderUtils {
   public static class_243 center;
   private static final Pool<RenderUtils.RenderBlock> renderBlockPool = new Pool(RenderUtils.RenderBlock::new);
   private static final List<RenderUtils.RenderBlock> renderBlocks = new ArrayList();
   private static final long initTime = System.nanoTime();

   private RenderUtils() {
   }

   @PostInit
   public static void init() {
      MeteorClient.EVENT_BUS.subscribe(RenderUtils.class);
   }

   public static void drawItem(class_332 drawContext, class_1799 itemStack, int x, int y, float scale, boolean overlay, String countOverride) {
      class_4587 matrices = drawContext.method_51448();
      matrices.method_22903();
      matrices.method_22905(scale, scale, 1.0F);
      matrices.method_46416(0.0F, 0.0F, 401.0F);
      int scaledX = (int)((float)x / scale);
      int scaledY = (int)((float)y / scale);
      drawContext.method_51427(itemStack, scaledX, scaledY);
      if (overlay) {
         drawContext.method_51432(MeteorClient.mc.field_1772, itemStack, scaledX, scaledY, countOverride);
      }

      matrices.method_22909();
   }

   public static void drawItem(class_332 drawContext, class_1799 itemStack, int x, int y, float scale, boolean overlay) {
      drawItem(drawContext, itemStack, x, y, scale, overlay, (String)null);
   }

   public static void updateScreenCenter() {
      class_310 mc = class_310.method_1551();
      Vector3f pos = new Vector3f(0.0F, 0.0F, 1.0F);
      if ((Boolean)mc.field_1690.method_42448().method_41753()) {
         class_4587 bobViewMatrices = new class_4587();
         bobView(bobViewMatrices);
         pos.mulPosition(bobViewMatrices.method_23760().method_23761().invert());
      }

      center = (new class_243((double)pos.x, (double)(-pos.y), (double)pos.z)).method_1037(-((float)Math.toRadians((double)mc.field_1773.method_19418().method_19329()))).method_1024(-((float)Math.toRadians((double)mc.field_1773.method_19418().method_19330()))).method_1019(mc.field_1773.method_19418().method_19326());
   }

   private static void bobView(class_4587 matrices) {
      class_1297 cameraEntity = class_310.method_1551().method_1560();
      if (cameraEntity instanceof class_1657) {
         class_1657 playerEntity = (class_1657)cameraEntity;
         float f = MeteorClient.mc.method_60646().method_60637(true);
         float g = playerEntity.field_5973 - playerEntity.field_6039;
         float h = -(playerEntity.field_5973 + g * f);
         float i = class_3532.method_16439(f, playerEntity.field_7505, playerEntity.field_7483);
         matrices.method_22904(-((double)(class_3532.method_15374(h * 3.1415927F) * i) * 0.5D), (double)Math.abs(class_3532.method_15362(h * 3.1415927F) * i), 0.0D);
         matrices.method_22907(class_7833.field_40718.rotationDegrees(class_3532.method_15374(h * 3.1415927F) * i * 3.0F));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(Math.abs(class_3532.method_15362(h * 3.1415927F - 0.2F) * i) * 5.0F));
      }

   }

   public static void renderTickingBlock(class_2338 blockPos, Color sideColor, Color lineColor, ShapeMode shapeMode, int excludeDir, int duration, boolean fade, boolean shrink) {
      Iterator iterator = renderBlocks.iterator();

      while(iterator.hasNext()) {
         RenderUtils.RenderBlock next = (RenderUtils.RenderBlock)iterator.next();
         if (next.pos.equals(blockPos)) {
            iterator.remove();
            renderBlockPool.free(next);
         }
      }

      renderBlocks.add(((RenderUtils.RenderBlock)renderBlockPool.get()).set(blockPos, sideColor, lineColor, shapeMode, excludeDir, duration, fade, shrink));
   }

   @EventHandler
   private static void onTick(TickEvent.Pre event) {
      if (!renderBlocks.isEmpty()) {
         renderBlocks.forEach(RenderUtils.RenderBlock::tick);
         Iterator iterator = renderBlocks.iterator();

         while(iterator.hasNext()) {
            RenderUtils.RenderBlock next = (RenderUtils.RenderBlock)iterator.next();
            if (next.ticks <= 0) {
               iterator.remove();
               renderBlockPool.free(next);
            }
         }

      }
   }

   @EventHandler
   private static void onRender(Render3DEvent event) {
      renderBlocks.forEach((block) -> {
         block.render(event);
      });
   }

   public static double getCurrentGameTickCalculated() {
      return getCurrentGameTickCalculatedNano(System.nanoTime());
   }

   public static double getCurrentGameTickCalculatedNano(long nanoTime) {
      return (double)(nanoTime - initTime) / (double)TimeUnit.MILLISECONDS.toNanos(50L);
   }

   public static class RenderBlock {
      public class_2339 pos = new class_2339();
      public Color sideColor;
      public Color lineColor;
      public ShapeMode shapeMode;
      public int excludeDir;
      public int ticks;
      public int duration;
      public boolean fade;
      public boolean shrink;

      public RenderUtils.RenderBlock set(class_2338 blockPos, Color sideColor, Color lineColor, ShapeMode shapeMode, int excludeDir, int duration, boolean fade, boolean shrink) {
         this.pos.method_10101(blockPos);
         this.sideColor = sideColor;
         this.lineColor = lineColor;
         this.shapeMode = shapeMode;
         this.excludeDir = excludeDir;
         this.fade = fade;
         this.shrink = shrink;
         this.ticks = duration;
         this.duration = duration;
         return this;
      }

      public void tick() {
         --this.ticks;
      }

      public void render(Render3DEvent event) {
         int preSideA = this.sideColor.a;
         int preLineA = this.lineColor.a;
         double x1 = (double)this.pos.method_10263();
         double y1 = (double)this.pos.method_10264();
         double z1 = (double)this.pos.method_10260();
         double x2 = (double)(this.pos.method_10263() + 1);
         double y2 = (double)(this.pos.method_10264() + 1);
         double z2 = (double)(this.pos.method_10260() + 1);
         double d = (double)((float)this.ticks - event.tickDelta) / (double)this.duration;
         if (this.fade) {
            this.sideColor.a = (int)((double)this.sideColor.a * d);
            this.lineColor.a = (int)((double)this.lineColor.a * d);
         }

         if (this.shrink) {
            x1 += d;
            y1 += d;
            z1 += d;
            x2 -= d;
            y2 -= d;
            z2 -= d;
         }

         event.renderer.box(x1, y1, z1, x2, y2, z2, this.sideColor, this.lineColor, this.shapeMode, this.excludeDir);
         this.sideColor.a = preSideA;
         this.lineColor.a = preLineA;
      }
   }
}
