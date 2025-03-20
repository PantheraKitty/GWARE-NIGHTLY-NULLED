package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IBakedQuad;
import net.minecraft.class_777;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_777.class})
public abstract class BakedQuadMixin implements IBakedQuad {
   @Shadow
   @Final
   protected int[] field_4175;

   public float meteor$getX(int vertexI) {
      return Float.intBitsToFloat(this.field_4175[vertexI * 8]);
   }

   public float meteor$getY(int vertexI) {
      return Float.intBitsToFloat(this.field_4175[vertexI * 8 + 1]);
   }

   public float meteor$getZ(int vertexI) {
      return Float.intBitsToFloat(this.field_4175[vertexI * 8 + 2]);
   }
}
