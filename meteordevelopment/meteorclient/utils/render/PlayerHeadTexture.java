package meteordevelopment.meteorclient.utils.render;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import javax.imageio.ImageIO;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.class_3298;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class PlayerHeadTexture extends Texture {
   private boolean needsRotate;

   public PlayerHeadTexture(String url) {
      BufferedImage skin;
      try {
         skin = ImageIO.read(Http.get(url).sendInputStream());
      } catch (IOException var9) {
         var9.printStackTrace();
         return;
      }

      byte[] head = new byte[192];
      int[] pixel = new int[4];
      int i = 0;

      int x;
      int y;
      int j;
      for(x = 8; x < 16; ++x) {
         for(y = 8; y < 16; ++y) {
            skin.getData().getPixel(x, y, pixel);

            for(j = 0; j < 3; ++j) {
               head[i] = (byte)pixel[j];
               ++i;
            }
         }
      }

      i = 0;

      for(x = 40; x < 48; ++x) {
         for(y = 8; y < 16; ++y) {
            skin.getData().getPixel(x, y, pixel);
            if (pixel[3] != 0) {
               for(j = 0; j < 3; ++j) {
                  head[i] = (byte)pixel[j];
                  ++i;
               }
            } else {
               i += 3;
            }
         }
      }

      this.upload(BufferUtils.createByteBuffer(head.length).put(head));
      this.needsRotate = true;
   }

   public PlayerHeadTexture() {
      try {
         InputStream inputStream = ((class_3298)MeteorClient.mc.method_1478().method_14486(MeteorClient.identifier("textures/steve.png")).get()).method_14482();

         try {
            ByteBuffer data = TextureUtil.readResource(inputStream);
            data.rewind();
            MemoryStack stack = MemoryStack.stackPush();

            try {
               IntBuffer width = stack.mallocInt(1);
               IntBuffer height = stack.mallocInt(1);
               IntBuffer comp = stack.mallocInt(1);
               ByteBuffer image = STBImage.stbi_load_from_memory(data, width, height, comp, 3);
               this.upload(image);
               STBImage.stbi_image_free(image);
            } catch (Throwable var10) {
               if (stack != null) {
                  try {
                     stack.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (stack != null) {
               stack.close();
            }

            MemoryUtil.memFree(data);
         } catch (Throwable var11) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (inputStream != null) {
            inputStream.close();
         }
      } catch (IOException var12) {
         var12.printStackTrace();
      }

   }

   private void upload(ByteBuffer data) {
      Runnable action = () -> {
         this.upload(8, 8, data, Texture.Format.RGB, Texture.Filter.Nearest, Texture.Filter.Nearest, false);
      };
      if (RenderSystem.isOnRenderThread()) {
         action.run();
      } else {
         Objects.requireNonNull(action);
         RenderSystem.recordRenderCall(action::run);
      }

   }

   public boolean needsRotate() {
      return this.needsRotate;
   }
}
