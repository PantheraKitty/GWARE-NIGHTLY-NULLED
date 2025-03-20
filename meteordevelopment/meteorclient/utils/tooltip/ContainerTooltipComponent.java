package meteordevelopment.meteorclient.utils.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5684;
import net.minecraft.class_757;

public class ContainerTooltipComponent implements class_5684, MeteorTooltipData {
   private static final class_2960 TEXTURE_CONTAINER_BACKGROUND = MeteorClient.identifier("textures/container.png");
   private final class_1799[] items;
   private final Color color;

   public ContainerTooltipComponent(class_1799[] items, Color color) {
      this.items = items;
      this.color = color;
   }

   public class_5684 getComponent() {
      return this;
   }

   public int method_32661() {
      return 67;
   }

   public int method_32664(class_327 textRenderer) {
      return 176;
   }

   public void method_32666(class_327 textRenderer, int x, int y, class_332 context) {
      RenderSystem.setShader(class_757::method_34542);
      RenderSystem.setShaderColor((float)this.color.r / 255.0F, (float)this.color.g / 255.0F, (float)this.color.b / 255.0F, (float)this.color.a / 255.0F);
      context.method_25291(TEXTURE_CONTAINER_BACKGROUND, x, y, 0, 0.0F, 0.0F, 176, 67, 176, 67);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int row = 0;
      int i = 0;
      class_1799[] var7 = this.items;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         class_1799 itemStack = var7[var9];
         RenderUtils.drawItem(context, itemStack, x + 8 + i * 18, y + 7 + row * 18, 1.0F, true);
         ++i;
         if (i >= 9) {
            i = 0;
            ++row;
         }
      }

   }
}
