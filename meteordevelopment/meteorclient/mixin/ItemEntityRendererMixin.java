package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.RenderItemEntityEvent;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_1542;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5819;
import net.minecraft.class_916;
import net.minecraft.class_918;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_916.class})
public abstract class ItemEntityRendererMixin {
   @Shadow
   @Final
   private class_5819 field_4725;
   @Shadow
   @Final
   private class_918 field_4726;

   @Inject(
      method = {"render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void render(class_1542 itemEntity, float f, float g, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, CallbackInfo ci) {
      RenderItemEntityEvent event = (RenderItemEntityEvent)MeteorClient.EVENT_BUS.post((ICancellable)RenderItemEntityEvent.get(itemEntity, f, g, matrixStack, vertexConsumerProvider, i, this.field_4725, this.field_4726));
      if (event.isCancelled()) {
         ci.cancel();
      }

   }
}
