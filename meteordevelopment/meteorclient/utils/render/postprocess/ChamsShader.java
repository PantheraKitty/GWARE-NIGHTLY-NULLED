package meteordevelopment.meteorclient.utils.render.postprocess;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.ResourcePacksReloadedEvent;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_3298;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class ChamsShader extends EntityShader {
   private static final String[] FILE_FORMATS = new String[]{"png", "jpg"};
   private static Texture IMAGE_TEX;
   private static Chams chams;

   public ChamsShader() {
      MeteorClient.EVENT_BUS.subscribe(ChamsShader.class);
   }

   @PostInit
   public static void load() {
      try {
         ByteBuffer data = null;
         String[] var1 = FILE_FORMATS;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            String fileFormat = var1[var3];
            Optional<class_3298> optional = MeteorClient.mc.method_1478().method_14486(MeteorClient.identifier("textures/chams." + fileFormat));
            if (!optional.isEmpty() && ((class_3298)optional.get()).method_14482() != null) {
               data = TextureUtil.readResource(((class_3298)optional.get()).method_14482());
               break;
            }
         }

         if (data == null) {
            return;
         }

         data.rewind();
         MemoryStack stack = MemoryStack.stackPush();

         try {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer image = STBImage.stbi_load_from_memory(data, width, height, comp, 3);
            IMAGE_TEX = new Texture();
            IMAGE_TEX.upload(width.get(0), height.get(0), image, Texture.Format.RGB, Texture.Filter.Nearest, Texture.Filter.Nearest, false);
            STBImage.stbi_image_free(image);
            STBImage.stbi_set_flip_vertically_on_load(false);
         } catch (Throwable var7) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (stack != null) {
            stack.close();
         }
      } catch (IOException var8) {
         var8.printStackTrace();
      }

   }

   @EventHandler
   private static void onResourcePacksReloaded(ResourcePacksReloadedEvent event) {
      load();
   }

   protected void setUniforms() {
      this.shader.set("u_Color", (Color)chams.shaderColor.get());
      if (chams.isShader() && chams.shader.get() == Chams.Shader.Image && IMAGE_TEX != null && IMAGE_TEX.isValid()) {
         IMAGE_TEX.bind(1);
         this.shader.set("u_TextureI", 1);
      }

   }

   protected boolean shouldDraw() {
      if (chams == null) {
         chams = (Chams)Modules.get().get(Chams.class);
      }

      return chams.isShader();
   }

   public boolean shouldDraw(class_1297 entity) {
      if (!this.shouldDraw()) {
         return false;
      } else {
         return ((Set)chams.entities.get()).contains(entity.method_5864()) && (entity != MeteorClient.mc.field_1724 || !(Boolean)chams.ignoreSelfDepth.get());
      }
   }
}
