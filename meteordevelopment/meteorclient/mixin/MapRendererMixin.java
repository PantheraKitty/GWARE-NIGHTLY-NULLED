package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.utils.misc.EmptyIterator;
import net.minecraft.class_20;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_330.class_331;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_331.class})
public abstract class MapRendererMixin {
   @ModifyExpressionValue(
      method = {"draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ZI)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/item/map/MapState;getDecorations()Ljava/lang/Iterable;"
)}
   )
   private Iterable<class_20> getIconsProxy(Iterable<class_20> original) {
      return ((NoRender)Modules.get().get(NoRender.class)).noMapMarkers() ? EmptyIterator::new : original;
   }

   @Inject(
      method = {"draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ZI)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDraw(class_4587 matrices, class_4597 vertexConsumers, boolean hidePlayerIcons, int light, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noMapContents()) {
         ci.cancel();
      }

   }
}
