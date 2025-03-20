package meteordevelopment.meteorclient.utils.render.postprocess;

import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.StorageESP;
import net.minecraft.class_1297;

public class StorageOutlineShader extends PostProcessShader {
   private static StorageESP storageESP;

   public StorageOutlineShader() {
      this.init("outline");
   }

   protected void preDraw() {
      this.framebuffer.method_1230(false);
      this.framebuffer.method_1235(false);
   }

   protected boolean shouldDraw() {
      if (storageESP == null) {
         storageESP = (StorageESP)Modules.get().get(StorageESP.class);
      }

      return storageESP.isShader();
   }

   public boolean shouldDraw(class_1297 entity) {
      return true;
   }

   protected void setUniforms() {
      this.shader.set("u_Width", (Integer)storageESP.outlineWidth.get());
      this.shader.set("u_FillOpacity", (double)(Integer)storageESP.fillOpacity.get() / 255.0D);
      this.shader.set("u_ShapeMode", ((ShapeMode)storageESP.shapeMode.get()).ordinal());
      this.shader.set("u_GlowMultiplier", (Double)storageESP.glowMultiplier.get());
   }
}
