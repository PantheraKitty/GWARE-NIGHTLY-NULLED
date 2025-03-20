package meteordevelopment.meteorclient.renderer.text;

import java.nio.ByteBuffer;
import meteordevelopment.meteorclient.renderer.DrawMode;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Mesh;
import meteordevelopment.meteorclient.renderer.ShaderMesh;
import meteordevelopment.meteorclient.renderer.Shaders;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_4587;
import org.lwjgl.BufferUtils;

public class CustomTextRenderer implements TextRenderer {
   public static final Color SHADOW_COLOR = new Color(60, 60, 60, 180);
   private final Mesh mesh;
   public final FontFace fontFace;
   private final Font[] fonts;
   private Font font;
   private boolean building;
   private boolean scaleOnly;
   private double fontScale;
   private double scale;

   public CustomTextRenderer(FontFace fontFace) {
      this.mesh = new ShaderMesh(Shaders.TEXT, DrawMode.Triangles, new Mesh.Attrib[]{Mesh.Attrib.Vec2, Mesh.Attrib.Vec2, Mesh.Attrib.Color});
      this.fontScale = 1.0D;
      this.scale = 1.0D;
      this.fontFace = fontFace;
      byte[] bytes = Utils.readBytes(fontFace.toStream());
      ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length).put(bytes).flip();
      this.fonts = new Font[5];

      for(int i = 0; i < this.fonts.length; ++i) {
         this.fonts[i] = new Font(buffer, (int)Math.round(27.0D * ((double)i * 0.5D + 1.0D)));
      }

   }

   public void setAlpha(double a) {
      this.mesh.alpha = a;
   }

   public void begin(double scale, boolean scaleOnly, boolean big) {
      if (this.building) {
         throw new RuntimeException("CustomTextRenderer.begin() called twice");
      } else {
         if (!scaleOnly) {
            this.mesh.begin();
         }

         if (big) {
            this.font = this.fonts[this.fonts.length - 1];
         } else {
            double scaleA = Math.floor(scale * 10.0D) / 10.0D;
            byte scaleI;
            if (scaleA >= 3.0D) {
               scaleI = 5;
            } else if (scaleA >= 2.5D) {
               scaleI = 4;
            } else if (scaleA >= 2.0D) {
               scaleI = 3;
            } else if (scaleA >= 1.5D) {
               scaleI = 2;
            } else {
               scaleI = 1;
            }

            this.font = this.fonts[scaleI - 1];
         }

         this.building = true;
         this.scaleOnly = scaleOnly;
         this.fontScale = (double)this.font.getHeight() / 27.0D;
         this.scale = 1.0D + (scale - this.fontScale) / this.fontScale;
      }
   }

   public double getWidth(String text, int length, boolean shadow) {
      if (text.isEmpty()) {
         return 0.0D;
      } else {
         Font font = this.building ? this.font : this.fonts[0];
         return (font.getWidth(text, length) + (double)(shadow ? 1 : 0)) * this.scale / 1.5D;
      }
   }

   public double getHeight(boolean shadow) {
      Font font = this.building ? this.font : this.fonts[0];
      return (double)(font.getHeight() + 1 + (shadow ? 1 : 0)) * this.scale / 1.5D;
   }

   public double render(String text, double x, double y, Color color, boolean shadow) {
      boolean wasBuilding = this.building;
      if (!wasBuilding) {
         this.begin();
      }

      double width;
      if (shadow) {
         int preShadowA = SHADOW_COLOR.a;
         SHADOW_COLOR.a = (int)((double)color.a / 255.0D * (double)preShadowA);
         width = this.font.render(this.mesh, text, x + this.fontScale * this.scale / 1.5D, y + this.fontScale * this.scale / 1.5D, SHADOW_COLOR, this.scale / 1.5D);
         this.font.render(this.mesh, text, x, y, color, this.scale / 1.5D);
         SHADOW_COLOR.a = preShadowA;
      } else {
         width = this.font.render(this.mesh, text, x, y, color, this.scale / 1.5D);
      }

      if (!wasBuilding) {
         this.end();
      }

      return width;
   }

   public boolean isBuilding() {
      return this.building;
   }

   public void end(class_4587 matrices) {
      if (!this.building) {
         throw new RuntimeException("CustomTextRenderer.end() called without calling begin()");
      } else {
         if (!this.scaleOnly) {
            this.mesh.end();
            GL.bindTexture(this.font.texture.method_4624());
            this.mesh.render(matrices);
         }

         this.building = false;
         this.scale = 1.0D;
      }
   }

   public void destroy() {
      this.mesh.destroy();
   }
}
