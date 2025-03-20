package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_557;
import net.minecraft.class_828;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_828.class})
public abstract class EnchantingTableBlockEntityRendererMixin {
   @WrapWithCondition(
      method = {"render(Lnet/minecraft/block/entity/EnchantingTableBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/entity/model/BookModel;renderBook(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
)}
   )
   private boolean onRenderBookModelRenderProxy(class_557 instance, class_4587 matrices, class_4588 vertices, int light, int overlay, int i) {
      return !((NoRender)Modules.get().get(NoRender.class)).noEnchTableBook();
   }
}
