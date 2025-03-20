package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import net.minecraft.class_1923;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4076;
import net.minecraft.class_862;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_862.class})
public abstract class ChunkBorderDebugRendererMixin {
   @Shadow
   @Final
   private class_310 field_4516;

   @ModifyExpressionValue(
      method = {"render"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;getChunkPos()Lnet/minecraft/util/math/ChunkPos;"
)}
   )
   private class_1923 render$getChunkPos(class_1923 chunkPos) {
      Freecam freecam = (Freecam)Modules.get().get(Freecam.class);
      if (!freecam.isActive()) {
         return chunkPos;
      } else {
         float delta = this.field_4516.method_60646().method_60637(true);
         return new class_1923(class_4076.method_18675(class_3532.method_15357(freecam.getX(delta))), class_4076.method_18675(class_3532.method_15357(freecam.getZ(delta))));
      }
   }
}
