package meteordevelopment.meteorclient.mixin;

import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2505;
import net.minecraft.class_2507;
import net.minecraft.class_2519;
import net.minecraft.class_2561;
import net.minecraft.class_437;
import net.minecraft.class_473;
import net.minecraft.class_4185.class_7840;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_473.class})
public abstract class BookEditScreenMixin extends class_437 {
   @Shadow
   @Final
   private List<String> field_17116;
   @Shadow
   private int field_2840;
   @Shadow
   private boolean field_2837;

   public BookEditScreenMixin(class_2561 title) {
      super(title);
   }

   @Shadow
   protected abstract void method_2413();

   @Inject(
      method = {"init"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo info) {
      this.method_37063((new class_7840(class_2561.method_43470("Copy"), (button) -> {
         class_2499 listTag = new class_2499();
         Stream var10000 = this.field_17116.stream().map(class_2519::method_23256);
         Objects.requireNonNull(listTag);
         var10000.forEach(listTag::add);
         class_2487 tag = new class_2487();
         tag.method_10566("pages", listTag);
         tag.method_10569("currentPage", this.field_2840);
         FastByteArrayOutputStream bytes = new FastByteArrayOutputStream();
         DataOutputStream out = new DataOutputStream(bytes);

         try {
            class_2507.method_55324(tag, out);
         } catch (IOException var8) {
            var8.printStackTrace();
         }

         try {
            GLFW.glfwSetClipboardString(MeteorClient.mc.method_22683().method_4490(), Base64.getEncoder().encodeToString(bytes.array));
         } catch (OutOfMemoryError var7) {
            GLFW.glfwSetClipboardString(MeteorClient.mc.method_22683().method_4490(), var7.toString());
         }

      })).method_46433(4, 4).method_46437(120, 20).method_46431());
      this.method_37063((new class_7840(class_2561.method_43470("Paste"), (button) -> {
         String clipboard = GLFW.glfwGetClipboardString(MeteorClient.mc.method_22683().method_4490());
         if (clipboard != null) {
            byte[] bytes;
            try {
               bytes = Base64.getDecoder().decode(clipboard);
            } catch (IllegalArgumentException var8) {
               return;
            }

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

            try {
               class_2487 tag = class_2507.method_10629(in, class_2505.method_53898());
               class_2499 listTag = tag.method_10554("pages", 8).method_10612();
               this.field_17116.clear();

               for(int i = 0; i < listTag.size(); ++i) {
                  this.field_17116.add(listTag.method_10608(i));
               }

               if (this.field_17116.isEmpty()) {
                  this.field_17116.add("");
               }

               this.field_2840 = tag.method_10550("currentPage");
               this.field_2837 = true;
               this.method_2413();
            } catch (IOException var9) {
               var9.printStackTrace();
            }

         }
      })).method_46433(4, 26).method_46437(120, 20).method_46431());
   }
}
