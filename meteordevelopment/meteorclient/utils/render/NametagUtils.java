package meteordevelopment.meteorclient.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Zoom;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
import org.joml.Vector4f;

public class NametagUtils {
   private static final Vector4f vec4 = new Vector4f();
   private static final Vector4f mmMat4 = new Vector4f();
   private static final Vector4f pmMat4 = new Vector4f();
   private static final Vector3d camera = new Vector3d();
   private static final Vector3d cameraNegated = new Vector3d();
   private static final Matrix4f model = new Matrix4f();
   private static final Matrix4f projection = new Matrix4f();
   private static double windowScale;
   public static double scale;

   private NametagUtils() {
   }

   public static void onRender(Matrix4f modelView) {
      model.set(modelView);
      projection.set(RenderSystem.getProjectionMatrix());
      Utils.set(camera, MeteorClient.mc.field_1773.method_19418().method_19326());
      cameraNegated.set(camera);
      cameraNegated.negate();
      windowScale = (double)MeteorClient.mc.method_22683().method_4476(1, false);
   }

   public static boolean to2D(Vector3d pos, double scale) {
      return to2D(pos, scale, true);
   }

   public static boolean to2D(Vector3d pos, double scale, boolean distanceScaling) {
      return to2D(pos, scale, distanceScaling, false);
   }

   public static boolean to2D(Vector3d pos, double scale, boolean distanceScaling, boolean allowBehind) {
      Zoom zoom = (Zoom)Modules.get().get(Zoom.class);
      NametagUtils.scale = scale * zoom.getScaling();
      if (distanceScaling) {
         NametagUtils.scale *= getScale(pos);
      }

      vec4.set(cameraNegated.x + pos.x, cameraNegated.y + pos.y, cameraNegated.z + pos.z, 1.0D);
      vec4.mul(model, mmMat4);
      mmMat4.mul(projection, pmMat4);
      boolean behind = pmMat4.w <= 0.0F;
      if (behind && !allowBehind) {
         return false;
      } else {
         toScreen(pmMat4);
         double x = (double)(pmMat4.x * (float)MeteorClient.mc.method_22683().method_4489());
         double y = (double)(pmMat4.y * (float)MeteorClient.mc.method_22683().method_4506());
         if (behind) {
            x = (double)MeteorClient.mc.method_22683().method_4489() - x;
            y = (double)MeteorClient.mc.method_22683().method_4506() - y;
         }

         if (!Double.isInfinite(x) && !Double.isInfinite(y)) {
            pos.set(x / windowScale, (double)MeteorClient.mc.method_22683().method_4506() - y / windowScale, allowBehind ? (double)pmMat4.w : (double)pmMat4.z);
            return true;
         } else {
            return false;
         }
      }
   }

   public static void begin(Vector3d pos) {
      Matrix4fStack matrices = RenderSystem.getModelViewStack();
      begin(matrices, pos);
   }

   public static void begin(Vector3d pos, class_332 drawContext) {
      begin(pos);
      class_4587 matrices = drawContext.method_51448();
      matrices.method_22903();
      matrices.method_46416((float)pos.x, (float)pos.y, 0.0F);
      matrices.method_22905((float)scale, (float)scale, 1.0F);
   }

   private static void begin(Matrix4fStack matrices, Vector3d pos) {
      matrices.pushMatrix();
      matrices.translate((float)pos.x, (float)pos.y, 0.0F);
      matrices.scale((float)scale, (float)scale, 1.0F);
   }

   public static void end() {
      RenderSystem.getModelViewStack().popMatrix();
   }

   public static void end(class_332 drawContext) {
      end();
      drawContext.method_51448().method_22909();
   }

   private static double getScale(Vector3d pos) {
      double dist = camera.distance(pos);
      return class_3532.method_15350(1.0D - dist * 0.01D, 0.5D, 2.147483647E9D);
   }

   private static void toScreen(Vector4f vec) {
      float newW = 1.0F / vec.w * 0.5F;
      vec.x = vec.x * newW + 0.5F;
      vec.y = vec.y * newW + 0.5F;
      vec.z = vec.z * newW + 0.5F;
      vec.w = newW;
   }
}
