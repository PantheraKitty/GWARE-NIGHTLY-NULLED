package meteordevelopment.meteorclient.utils.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import net.minecraft.class_1806;
import net.minecraft.class_22;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_5684;
import net.minecraft.class_757;
import net.minecraft.class_9209;
import net.minecraft.class_4597.class_4598;

public class MapTooltipComponent implements class_5684, MeteorTooltipData {
   private static final class_2960 TEXTURE_MAP_BACKGROUND = class_2960.method_60654("textures/map/map_background.png");
   private final int mapId;

   public MapTooltipComponent(int mapId) {
      this.mapId = mapId;
   }

   public int method_32661() {
      double scale = (Double)((BetterTooltips)Modules.get().get(BetterTooltips.class)).mapsScale.get();
      return (int)(144.0D * scale) + 2;
   }

   public int method_32664(class_327 textRenderer) {
      double scale = (Double)((BetterTooltips)Modules.get().get(BetterTooltips.class)).mapsScale.get();
      return (int)(144.0D * scale);
   }

   public class_5684 getComponent() {
      return this;
   }

   public void method_32666(class_327 textRenderer, int x, int y, class_332 context) {
      double scale = (Double)((BetterTooltips)Modules.get().get(BetterTooltips.class)).mapsScale.get();
      class_4587 matrices = context.method_51448();
      matrices.method_22903();
      matrices.method_46416((float)x, (float)y, 0.0F);
      matrices.method_22905((float)scale * 2.0F, (float)scale * 2.0F, 0.0F);
      matrices.method_22905(1.125F, 1.125F, 0.0F);
      RenderSystem.setShader(class_757::method_34542);
      context.method_25291(TEXTURE_MAP_BACKGROUND, 0, 0, 0, 0.0F, 0.0F, 64, 64, 64, 64);
      matrices.method_22909();
      class_4598 consumer = MeteorClient.mc.method_22940().method_23000();
      class_22 mapState = class_1806.method_7997(new class_9209(this.mapId), MeteorClient.mc.field_1687);
      if (mapState != null) {
         matrices.method_22903();
         matrices.method_46416((float)x, (float)y, 0.0F);
         matrices.method_22905((float)scale, (float)scale, 0.0F);
         matrices.method_46416(8.0F, 8.0F, 0.0F);
         MeteorClient.mc.field_1773.method_3194().method_1773(matrices, consumer, new class_9209(this.mapId), mapState, false, 15728880);
         consumer.method_22993();
         matrices.method_22909();
      }
   }
}
