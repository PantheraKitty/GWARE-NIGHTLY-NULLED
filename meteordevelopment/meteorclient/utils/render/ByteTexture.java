package meteordevelopment.meteorclient.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.nio.ByteBuffer;
import net.minecraft.class_1044;
import net.minecraft.class_3300;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30C;

public class ByteTexture extends class_1044 {
   public ByteTexture(int width, int height, byte[] data, ByteTexture.Format format, ByteTexture.Filter filterMin, ByteTexture.Filter filterMag) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.upload(width, height, data, format, filterMin, filterMag);
         });
      } else {
         this.upload(width, height, data, format, filterMin, filterMag);
      }

   }

   public ByteTexture(int width, int height, ByteBuffer buffer, ByteTexture.Format format, ByteTexture.Filter filterMin, ByteTexture.Filter filterMag) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.upload(width, height, buffer, format, filterMin, filterMag);
         });
      } else {
         this.upload(width, height, buffer, format, filterMin, filterMag);
      }

   }

   private void upload(int width, int height, byte[] data, ByteTexture.Format format, ByteTexture.Filter filterMin, ByteTexture.Filter filterMag) {
      ByteBuffer buffer = BufferUtils.createByteBuffer(data.length).put(data);
      this.upload(width, height, buffer, format, filterMin, filterMag);
   }

   private void upload(int width, int height, ByteBuffer buffer, ByteTexture.Format format, ByteTexture.Filter filterMin, ByteTexture.Filter filterMag) {
      this.method_23207();
      GL30C.glPixelStorei(3312, 0);
      GL30C.glPixelStorei(3313, 0);
      GL30C.glPixelStorei(3314, 0);
      GL30C.glPixelStorei(32878, 0);
      GL30C.glPixelStorei(3315, 0);
      GL30C.glPixelStorei(3316, 0);
      GL30C.glPixelStorei(32877, 0);
      GL30C.glPixelStorei(3317, 4);
      GL30C.glTexParameteri(3553, 10242, 10497);
      GL30C.glTexParameteri(3553, 10243, 10497);
      GL30C.glTexParameteri(3553, 10241, filterMin.toOpenGL());
      GL30C.glTexParameteri(3553, 10240, filterMag.toOpenGL());
      buffer.rewind();
      GL30C.glTexImage2D(3553, 0, format.toOpenGL(), width, height, 0, format.toOpenGL(), 5121, buffer);
   }

   public void method_4625(class_3300 manager) throws IOException {
   }

   public static enum Format {
      A,
      RGB,
      RGBA;

      public int toOpenGL() {
         short var10000;
         switch(this.ordinal()) {
         case 0:
            var10000 = 6403;
            break;
         case 1:
            var10000 = 6407;
            break;
         case 2:
            var10000 = 6408;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static ByteTexture.Format[] $values() {
         return new ByteTexture.Format[]{A, RGB, RGBA};
      }
   }

   public static enum Filter {
      Nearest,
      Linear;

      public int toOpenGL() {
         return this == Nearest ? 9728 : 9729;
      }

      // $FF: synthetic method
      private static ByteTexture.Filter[] $values() {
         return new ByteTexture.Filter[]{Nearest, Linear};
      }
   }
}
