package meteordevelopment.meteorclient.utils.render.postprocess;

import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import net.minecraft.class_1297;

public class EntityOutlineShader extends EntityShader {
   private static ESP esp;

   public EntityOutlineShader() {
      this.init("outline");
   }

   protected boolean shouldDraw() {
      if (esp == null) {
         esp = (ESP)Modules.get().get(ESP.class);
      }

      return esp.isShader();
   }

   public boolean shouldDraw(class_1297 entity) {
      if (!this.shouldDraw()) {
         return false;
      } else {
         return !esp.shouldSkip(entity);
      }
   }

   protected void setUniforms() {
      this.shader.set("u_Width", (Integer)esp.outlineWidth.get());
      this.shader.set("u_FillOpacity", (Double)esp.fillOpacity.get());
      this.shader.set("u_ShapeMode", ((ShapeMode)esp.shapeMode.get()).ordinal());
      this.shader.set("u_GlowMultiplier", (Double)esp.glowMultiplier.get());
   }
}
