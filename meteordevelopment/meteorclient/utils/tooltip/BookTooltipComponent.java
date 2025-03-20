package meteordevelopment.meteorclient.utils.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_5481;
import net.minecraft.class_5684;
import net.minecraft.class_757;

public class BookTooltipComponent implements class_5684, MeteorTooltipData {
   private static final class_2960 TEXTURE_BOOK_BACKGROUND = class_2960.method_60654("textures/gui/book.png");
   private final class_2561 page;

   public BookTooltipComponent(class_2561 page) {
      this.page = page;
   }

   public class_5684 getComponent() {
      return this;
   }

   public int method_32661() {
      return 134;
   }

   public int method_32664(class_327 textRenderer) {
      return 112;
   }

   public void method_32666(class_327 textRenderer, int x, int y, class_332 context) {
      RenderSystem.setShader(class_757::method_34542);
      context.method_25291(TEXTURE_BOOK_BACKGROUND, x, y, 0, 12.0F, 0.0F, 112, 134, 179, 179);
      class_4587 matrices = context.method_51448();
      matrices.method_22903();
      matrices.method_46416((float)(x + 16), (float)(y + 12), 1.0F);
      matrices.method_22905(0.7F, 0.7F, 1.0F);
      int offset = 0;

      for(Iterator var7 = textRenderer.method_1728(this.page, 112).iterator(); var7.hasNext(); offset += 8) {
         class_5481 line = (class_5481)var7.next();
         context.method_51430(textRenderer, line, 0, offset, 0, false);
      }

      matrices.method_22909();
   }
}
