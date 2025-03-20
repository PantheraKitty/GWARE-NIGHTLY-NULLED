package meteordevelopment.meteorclient.utils.render.postprocess;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.PostProcessRenderer;
import meteordevelopment.meteorclient.renderer.Shader;
import net.minecraft.class_1297;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_4618;
import net.minecraft.class_6367;
import org.lwjgl.glfw.GLFW;

public abstract class PostProcessShader {
   public class_4618 vertexConsumerProvider;
   public class_276 framebuffer;
   protected Shader shader;

   public void init(String frag) {
      this.vertexConsumerProvider = new class_4618(MeteorClient.mc.method_22940().method_23000());
      this.framebuffer = new class_6367(MeteorClient.mc.method_22683().method_4489(), MeteorClient.mc.method_22683().method_4506(), false, class_310.field_1703);
      this.shader = new Shader("post-process/base.vert", "post-process/" + frag + ".frag");
   }

   protected abstract boolean shouldDraw();

   public abstract boolean shouldDraw(class_1297 var1);

   protected void preDraw() {
   }

   protected void postDraw() {
   }

   protected abstract void setUniforms();

   public void beginRender() {
      if (this.shouldDraw()) {
         this.framebuffer.method_1230(class_310.field_1703);
         MeteorClient.mc.method_1522().method_1235(false);
      }
   }

   public void endRender(Runnable draw) {
      if (this.shouldDraw()) {
         this.preDraw();
         draw.run();
         this.postDraw();
         MeteorClient.mc.method_1522().method_1235(false);
         GL.bindTexture(this.framebuffer.method_30277(), 0);
         this.shader.bind();
         this.shader.set("u_Size", (double)MeteorClient.mc.method_22683().method_4489(), (double)MeteorClient.mc.method_22683().method_4506());
         this.shader.set("u_Texture", 0);
         this.shader.set("u_Time", GLFW.glfwGetTime());
         this.setUniforms();
         PostProcessRenderer.render();
      }
   }

   public void onResized(int width, int height) {
      if (this.framebuffer != null) {
         this.framebuffer.method_1234(width, height, class_310.field_1703);
      }
   }
}
