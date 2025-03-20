package meteordevelopment.meteorclient.systems.hud;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.CustomFontChangedEvent;
import meteordevelopment.meteorclient.renderer.DrawMode;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Mesh;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.ShaderMesh;
import meteordevelopment.meteorclient.renderer.Shaders;
import meteordevelopment.meteorclient.renderer.text.CustomTextRenderer;
import meteordevelopment.meteorclient.renderer.text.Font;
import meteordevelopment.meteorclient.renderer.text.VanillaTextRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import org.lwjgl.BufferUtils;

public class HudRenderer {
   public static final HudRenderer INSTANCE = new HudRenderer();
   private static final double SCALE_TO_HEIGHT = 0.05555555555555555D;
   private final Hud hud = Hud.get();
   private final List<Runnable> postTasks = new ArrayList();
   private final Int2ObjectMap<HudRenderer.FontHolder> fontsInUse = new Int2ObjectOpenHashMap();
   private final LoadingCache<Integer, HudRenderer.FontHolder> fontCache = CacheBuilder.newBuilder().maximumSize(4L).expireAfterAccess(Duration.ofMinutes(10L)).removalListener((notification) -> {
      if (notification.wasEvicted()) {
         ((HudRenderer.FontHolder)notification.getValue()).destroy();
      }

   }).build(CacheLoader.from(HudRenderer::loadFont));
   public class_332 drawContext;
   public double delta;

   private HudRenderer() {
      MeteorClient.EVENT_BUS.subscribe((Object)this);
   }

   public void begin(class_332 drawContext) {
      Renderer2D.COLOR.begin();
      this.drawContext = drawContext;
      this.delta = Utils.frameTime;
      if (!this.hud.hasCustomFont()) {
         VanillaTextRenderer.INSTANCE.scaleIndividually = true;
         VanillaTextRenderer.INSTANCE.begin();
      }

   }

   public void end() {
      Renderer2D.COLOR.render(new class_4587());
      HudRenderer.FontHolder fontHolder;
      if (this.hud.hasCustomFont()) {
         for(ObjectIterator it = this.fontsInUse.values().iterator(); it.hasNext(); fontHolder.visited = false) {
            fontHolder = (HudRenderer.FontHolder)it.next();
            if (fontHolder.visited) {
               GL.bindTexture(fontHolder.font.texture.method_4624());
               fontHolder.getMesh().render((class_4587)null);
            } else {
               it.remove();
               this.fontCache.put(fontHolder.font.getHeight(), fontHolder);
            }
         }
      } else {
         VanillaTextRenderer.INSTANCE.end();
         VanillaTextRenderer.INSTANCE.scaleIndividually = false;
      }

      Iterator var3 = this.postTasks.iterator();

      while(var3.hasNext()) {
         Runnable task = (Runnable)var3.next();
         task.run();
      }

      this.postTasks.clear();
      this.drawContext = null;
   }

   public void line(double x1, double y1, double x2, double y2, Color color) {
      Renderer2D.COLOR.line(x1, y1, x2, y2, color);
   }

   public void quad(double x, double y, double width, double height, Color color) {
      Renderer2D.COLOR.quad(x, y, width, height, color);
   }

   public void quad(double x, double y, double width, double height, Color cTopLeft, Color cTopRight, Color cBottomRight, Color cBottomLeft) {
      Renderer2D.COLOR.quad(x, y, width, height, cTopLeft, cTopRight, cBottomRight, cBottomLeft);
   }

   public void triangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
      Renderer2D.COLOR.triangle(x1, y1, x2, y2, x3, y3, color);
   }

   public void texture(class_2960 id, double x, double y, double width, double height, Color color) {
      GL.bindTexture(id);
      Renderer2D.TEXTURE.begin();
      Renderer2D.TEXTURE.texQuad(x, y, width, height, color);
      Renderer2D.TEXTURE.render((class_4587)null);
   }

   public double text(String text, double x, double y, Color color, boolean shadow, double scale) {
      if (scale == -1.0D) {
         scale = this.hud.getTextScale();
      }

      if (!this.hud.hasCustomFont()) {
         VanillaTextRenderer.INSTANCE.scale = scale * 2.0D;
         return VanillaTextRenderer.INSTANCE.render(text, x, y, color, shadow);
      } else {
         HudRenderer.FontHolder fontHolder = this.getFontHolder(scale, true);
         Font font = fontHolder.font;
         Mesh mesh = fontHolder.getMesh();
         double width;
         if (shadow) {
            int preShadowA = CustomTextRenderer.SHADOW_COLOR.a;
            CustomTextRenderer.SHADOW_COLOR.a = (int)((double)color.a / 255.0D * (double)preShadowA);
            width = font.render(mesh, text, x + 1.0D, y + 1.0D, CustomTextRenderer.SHADOW_COLOR, scale);
            font.render(mesh, text, x, y, color, scale);
            CustomTextRenderer.SHADOW_COLOR.a = preShadowA;
         } else {
            width = font.render(mesh, text, x, y, color, scale);
         }

         return width;
      }
   }

   public double text(String text, double x, double y, Color color, boolean shadow) {
      return this.text(text, x, y, color, shadow, -1.0D);
   }

   public double textWidth(String text, boolean shadow, double scale) {
      if (text.isEmpty()) {
         return 0.0D;
      } else if (this.hud.hasCustomFont()) {
         double width = this.getFont(scale).getWidth(text, text.length());
         return (width + (double)(shadow ? 1 : 0)) * (scale == -1.0D ? this.hud.getTextScale() : scale) + (double)(shadow ? 1 : 0);
      } else {
         VanillaTextRenderer.INSTANCE.scale = (scale == -1.0D ? this.hud.getTextScale() : scale) * 2.0D;
         return VanillaTextRenderer.INSTANCE.getWidth(text, shadow);
      }
   }

   public double textWidth(String text, boolean shadow) {
      return this.textWidth(text, shadow, -1.0D);
   }

   public double textWidth(String text, double scale) {
      return this.textWidth(text, false, scale);
   }

   public double textWidth(String text) {
      return this.textWidth(text, false, -1.0D);
   }

   public double textHeight(boolean shadow, double scale) {
      if (this.hud.hasCustomFont()) {
         double height = (double)(this.getFont(scale).getHeight() + 1);
         return (height + (double)(shadow ? 1 : 0)) * (scale == -1.0D ? this.hud.getTextScale() : scale);
      } else {
         VanillaTextRenderer.INSTANCE.scale = (scale == -1.0D ? this.hud.getTextScale() : scale) * 2.0D;
         return VanillaTextRenderer.INSTANCE.getHeight(shadow);
      }
   }

   public double textHeight(boolean shadow) {
      return this.textHeight(shadow, -1.0D);
   }

   public double textHeight() {
      return this.textHeight(false, -1.0D);
   }

   public void post(Runnable task) {
      this.postTasks.add(task);
   }

   public void item(class_1799 itemStack, int x, int y, float scale, boolean overlay, String countOverlay) {
      RenderUtils.drawItem(this.drawContext, itemStack, x, y, scale, overlay, countOverlay);
   }

   public void item(class_1799 itemStack, int x, int y, float scale, boolean overlay) {
      RenderUtils.drawItem(this.drawContext, itemStack, x, y, scale, overlay);
   }

   private HudRenderer.FontHolder getFontHolder(double scale, boolean render) {
      if (scale == -1.0D) {
         scale = this.hud.getTextScale();
      }

      int height = (int)Math.round(scale / 0.05555555555555555D);
      HudRenderer.FontHolder fontHolder = (HudRenderer.FontHolder)this.fontsInUse.get(height);
      if (fontHolder != null) {
         if (render) {
            fontHolder.visited = true;
         }

         return fontHolder;
      } else if (render) {
         fontHolder = (HudRenderer.FontHolder)this.fontCache.getIfPresent(height);
         if (fontHolder == null) {
            fontHolder = loadFont(height);
         } else {
            this.fontCache.invalidate(height);
         }

         this.fontsInUse.put(height, fontHolder);
         fontHolder.visited = true;
         return fontHolder;
      } else {
         return (HudRenderer.FontHolder)this.fontCache.getUnchecked(height);
      }
   }

   private Font getFont(double scale) {
      return this.getFontHolder(scale, false).font;
   }

   @EventHandler
   private void onCustomFontChanged(CustomFontChangedEvent event) {
      ObjectIterator var2 = this.fontsInUse.values().iterator();

      HudRenderer.FontHolder fontHolder;
      while(var2.hasNext()) {
         fontHolder = (HudRenderer.FontHolder)var2.next();
         fontHolder.destroy();
      }

      Iterator var4 = this.fontCache.asMap().values().iterator();

      while(var4.hasNext()) {
         fontHolder = (HudRenderer.FontHolder)var4.next();
         fontHolder.destroy();
      }

      this.fontsInUse.clear();
      this.fontCache.invalidateAll();
   }

   private static HudRenderer.FontHolder loadFont(int height) {
      byte[] data = Utils.readBytes(Fonts.RENDERER.fontFace.toStream());
      ByteBuffer buffer = BufferUtils.createByteBuffer(data.length).put(data).flip();
      return new HudRenderer.FontHolder(new Font(buffer, height));
   }

   private static class FontHolder {
      public final Font font;
      public boolean visited;
      private Mesh mesh;

      public FontHolder(Font font) {
         this.font = font;
      }

      public Mesh getMesh() {
         if (this.mesh == null) {
            this.mesh = new ShaderMesh(Shaders.TEXT, DrawMode.Triangles, new Mesh.Attrib[]{Mesh.Attrib.Vec2, Mesh.Attrib.Vec2, Mesh.Attrib.Color});
         }

         if (!this.mesh.isBuilding()) {
            this.mesh.begin();
         }

         return this.mesh;
      }

      public void destroy() {
         this.font.texture.method_4528();
         if (this.mesh != null) {
            this.mesh.destroy();
         }

      }
   }
}
