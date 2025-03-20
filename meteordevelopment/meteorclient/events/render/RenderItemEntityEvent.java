package meteordevelopment.meteorclient.events.render;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_1542;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5819;
import net.minecraft.class_918;

public class RenderItemEntityEvent extends Cancellable {
   private static final RenderItemEntityEvent INSTANCE = new RenderItemEntityEvent();
   public class_1542 itemEntity;
   public float f;
   public float tickDelta;
   public class_4587 matrixStack;
   public class_4597 vertexConsumerProvider;
   public int light;
   public class_5819 random;
   public class_918 itemRenderer;

   public static RenderItemEntityEvent get(class_1542 itemEntity, float f, float tickDelta, class_4587 matrixStack, class_4597 vertexConsumerProvider, int light, class_5819 random, class_918 itemRenderer) {
      INSTANCE.setCancelled(false);
      INSTANCE.itemEntity = itemEntity;
      INSTANCE.f = f;
      INSTANCE.tickDelta = tickDelta;
      INSTANCE.matrixStack = matrixStack;
      INSTANCE.vertexConsumerProvider = vertexConsumerProvider;
      INSTANCE.light = light;
      INSTANCE.random = random;
      INSTANCE.itemRenderer = itemRenderer;
      return INSTANCE;
   }
}
