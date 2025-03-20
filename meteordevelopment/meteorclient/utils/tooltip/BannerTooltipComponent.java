package meteordevelopment.meteorclient.utils.tooltip;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1088;
import net.minecraft.class_1746;
import net.minecraft.class_1767;
import net.minecraft.class_1799;
import net.minecraft.class_308;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_5602;
import net.minecraft.class_5684;
import net.minecraft.class_630;
import net.minecraft.class_823;
import net.minecraft.class_9307;
import net.minecraft.class_9334;
import net.minecraft.class_4597.class_4598;

public class BannerTooltipComponent implements MeteorTooltipData, class_5684 {
   private final class_1767 color;
   private final class_9307 patterns;
   private final class_630 bannerField;

   public BannerTooltipComponent(class_1799 banner) {
      this.color = ((class_1746)banner.method_7909()).method_7706();
      this.patterns = (class_9307)banner.method_57825(class_9334.field_49619, class_9307.field_49404);
      this.bannerField = MeteorClient.mc.method_31974().method_32072(class_5602.field_27678).method_32086("flag");
   }

   public BannerTooltipComponent(class_1767 color, class_9307 patterns) {
      this.color = color;
      this.patterns = patterns;
      this.bannerField = MeteorClient.mc.method_31974().method_32072(class_5602.field_27678).method_32086("flag");
   }

   public class_5684 getComponent() {
      return this;
   }

   public int method_32661() {
      return 158;
   }

   public int method_32664(class_327 textRenderer) {
      return 80;
   }

   public void method_32666(class_327 textRenderer, int x, int y, class_332 context) {
      class_308.method_24210();
      class_4587 matrices = context.method_51448();
      matrices.method_22903();
      matrices.method_46416((float)(x + 8), (float)(y + 8), 0.0F);
      matrices.method_22903();
      matrices.method_22904(0.5D, 16.0D, 0.0D);
      matrices.method_22905(6.0F, -6.0F, 1.0F);
      matrices.method_22905(2.0F, -2.0F, -2.0F);
      matrices.method_22903();
      matrices.method_22904(2.5D, 8.5D, 0.0D);
      matrices.method_22905(5.0F, 5.0F, 5.0F);
      class_4598 immediate = MeteorClient.mc.method_22940().method_23000();
      this.bannerField.field_3654 = 0.0F;
      this.bannerField.field_3656 = -32.0F;
      class_823.method_29999(matrices, immediate, 15728880, class_4608.field_21444, this.bannerField, class_1088.field_20847, true, this.color, this.patterns);
      matrices.method_22909();
      matrices.method_22909();
      immediate.method_22993();
      matrices.method_22909();
      class_308.method_24211();
   }
}
