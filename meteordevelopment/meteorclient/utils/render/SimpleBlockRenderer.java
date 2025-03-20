package meteordevelopment.meteorclient.utils.render;

import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IBakedQuad;
import net.minecraft.class_1087;
import net.minecraft.class_1921;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2464;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_5819;
import net.minecraft.class_777;
import net.minecraft.class_827;

public class SimpleBlockRenderer {
   private static final class_4587 MATRICES = new class_4587();
   private static final class_2350[] DIRECTIONS = class_2350.values();
   private static final class_5819 RANDOM = class_5819.method_43047();

   private SimpleBlockRenderer() {
   }

   public static void renderWithBlockEntity(class_2586 blockEntity, float tickDelta, IVertexConsumerProvider vertexConsumerProvider) {
      vertexConsumerProvider.setOffset(blockEntity.method_11016().method_10263(), blockEntity.method_11016().method_10264(), blockEntity.method_11016().method_10260());
      render(blockEntity.method_11016(), blockEntity.method_11010(), vertexConsumerProvider);
      class_827<class_2586> renderer = MeteorClient.mc.method_31975().method_3550(blockEntity);
      if (renderer != null && blockEntity.method_11002() && blockEntity.method_11017().method_20526(blockEntity.method_11010())) {
         renderer.method_3569(blockEntity, tickDelta, MATRICES, vertexConsumerProvider, 15728880, class_4608.field_21444);
      }

      vertexConsumerProvider.setOffset(0, 0, 0);
   }

   public static void render(class_2338 pos, class_2680 state, class_4597 consumerProvider) {
      if (state.method_26217() == class_2464.field_11458) {
         class_4588 consumer = consumerProvider.getBuffer(class_1921.method_23577());
         class_1087 model = MeteorClient.mc.method_1541().method_3349(state);
         class_243 offset = state.method_26226(MeteorClient.mc.field_1687, pos);
         float offsetX = (float)offset.field_1352;
         float offsetY = (float)offset.field_1351;
         float offsetZ = (float)offset.field_1350;
         class_2350[] var9 = DIRECTIONS;
         int var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            class_2350 direction = var9[var11];
            List<class_777> list = model.method_4707(state, direction, RANDOM);
            if (!list.isEmpty()) {
               renderQuads(list, offsetX, offsetY, offsetZ, consumer);
            }
         }

         List<class_777> list = model.method_4707(state, (class_2350)null, RANDOM);
         if (!list.isEmpty()) {
            renderQuads(list, offsetX, offsetY, offsetZ, consumer);
         }

      }
   }

   private static void renderQuads(List<class_777> quads, float offsetX, float offsetY, float offsetZ, class_4588 consumer) {
      Iterator var5 = quads.iterator();

      while(var5.hasNext()) {
         class_777 bakedQuad = (class_777)var5.next();
         IBakedQuad quad = (IBakedQuad)bakedQuad;

         for(int j = 0; j < 4; ++j) {
            float x = quad.meteor$getX(j);
            float y = quad.meteor$getY(j);
            float z = quad.meteor$getZ(j);
            consumer.method_22912(offsetX + x, offsetY + y, offsetZ + z);
         }
      }

   }
}
