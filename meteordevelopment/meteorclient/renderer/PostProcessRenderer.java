package meteordevelopment.meteorclient.renderer;

import meteordevelopment.meteorclient.utils.PreInit;
import net.minecraft.class_4587;

public class PostProcessRenderer {
   private static Mesh mesh;
   private static final class_4587 matrices = new class_4587();

   private PostProcessRenderer() {
   }

   @PreInit
   public static void init() {
      mesh = new Mesh(DrawMode.Triangles, new Mesh.Attrib[]{Mesh.Attrib.Vec2});
      mesh.begin();
      mesh.quad(mesh.vec2(-1.0D, -1.0D).next(), mesh.vec2(-1.0D, 1.0D).next(), mesh.vec2(1.0D, 1.0D).next(), mesh.vec2(1.0D, -1.0D).next());
      mesh.end();
   }

   public static void beginRender() {
      mesh.beginRender(matrices);
   }

   public static void render() {
      mesh.render(matrices);
   }

   public static void endRender() {
      mesh.endRender();
   }
}
