package meteordevelopment.meteorclient.renderer.text;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import net.minecraft.class_327.class_6415;
import net.minecraft.class_4597.class_4598;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class VanillaTextRenderer implements TextRenderer {
   public static final VanillaTextRenderer INSTANCE = new VanillaTextRenderer();
   private final class_9799 buffer = new class_9799(2048);
   private final class_4598 immediate;
   private final class_4587 matrices;
   private final Matrix4f emptyMatrix;
   public double scale;
   public boolean scaleIndividually;
   private boolean building;
   private double alpha;

   private VanillaTextRenderer() {
      this.immediate = class_4597.method_22991(this.buffer);
      this.matrices = new class_4587();
      this.emptyMatrix = new Matrix4f();
      this.scale = 2.0D;
      this.alpha = 1.0D;
   }

   public void setAlpha(double a) {
      this.alpha = a;
   }

   public double getWidth(String text, int length, boolean shadow) {
      if (text.isEmpty()) {
         return 0.0D;
      } else {
         if (length != text.length()) {
            text = text.substring(0, length);
         }

         return (double)(MeteorClient.mc.field_1772.method_1727(text) + (shadow ? 1 : 0)) * this.scale;
      }
   }

   public double getHeight(boolean shadow) {
      Objects.requireNonNull(MeteorClient.mc.field_1772);
      return (double)(9 + (shadow ? 1 : 0)) * this.scale;
   }

   public void begin(double scale, boolean scaleOnly, boolean big) {
      if (this.building) {
         throw new RuntimeException("VanillaTextRenderer.begin() called twice");
      } else {
         this.scale = scale * 2.0D;
         this.building = true;
      }
   }

   public double render(String text, double x, double y, Color color, boolean shadow) {
      boolean wasBuilding = this.building;
      if (!wasBuilding) {
         this.begin();
      }

      x += 0.5D * this.scale;
      y += 0.5D * this.scale;
      int preA = color.a;
      color.a = (int)((double)(color.a / 255) * this.alpha * 255.0D);
      Matrix4f matrix = this.emptyMatrix;
      if (this.scaleIndividually) {
         this.matrices.method_22903();
         this.matrices.method_22905((float)this.scale, (float)this.scale, 1.0F);
         matrix = this.matrices.method_23760().method_23761();
      }

      double x2 = (double)MeteorClient.mc.field_1772.method_27521(text, (float)(x / this.scale), (float)(y / this.scale), color.getPacked(), shadow, matrix, this.immediate, class_6415.field_33993, 0, 15728880);
      if (this.scaleIndividually) {
         this.matrices.method_22909();
      }

      color.a = preA;
      if (!wasBuilding) {
         this.end();
      }

      return (x2 - 1.0D) * this.scale;
   }

   public boolean isBuilding() {
      return this.building;
   }

   public void end(class_4587 matrices) {
      if (!this.building) {
         throw new RuntimeException("VanillaTextRenderer.end() called without calling begin()");
      } else {
         Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
         RenderSystem.disableDepthTest();
         matrixStack.pushMatrix();
         if (matrices != null) {
            matrixStack.mul(matrices.method_23760().method_23761());
         }

         if (!this.scaleIndividually) {
            matrixStack.scale((float)this.scale, (float)this.scale, 1.0F);
         }

         RenderSystem.applyModelViewMatrix();
         this.immediate.method_22993();
         matrixStack.popMatrix();
         RenderSystem.enableDepthTest();
         RenderSystem.applyModelViewMatrix();
         this.scale = 2.0D;
         this.building = false;
      }
   }
}
