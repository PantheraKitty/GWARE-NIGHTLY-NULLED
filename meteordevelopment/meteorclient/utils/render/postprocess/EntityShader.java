package meteordevelopment.meteorclient.utils.render.postprocess;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.WorldRendererAccessor;
import net.minecraft.class_276;
import net.minecraft.class_761;

public abstract class EntityShader extends PostProcessShader {
   private class_276 prevBuffer;

   protected void preDraw() {
      class_761 worldRenderer = MeteorClient.mc.field_1769;
      WorldRendererAccessor wra = (WorldRendererAccessor)worldRenderer;
      this.prevBuffer = worldRenderer.method_22990();
      wra.setEntityOutlinesFramebuffer(this.framebuffer);
   }

   protected void postDraw() {
      if (this.prevBuffer != null) {
         class_761 worldRenderer = MeteorClient.mc.field_1769;
         WorldRendererAccessor wra = (WorldRendererAccessor)worldRenderer;
         wra.setEntityOutlinesFramebuffer(this.prevBuffer);
         this.prevBuffer = null;
      }
   }

   public void endRender() {
      this.endRender(() -> {
         this.vertexConsumerProvider.method_23285();
      });
   }
}
