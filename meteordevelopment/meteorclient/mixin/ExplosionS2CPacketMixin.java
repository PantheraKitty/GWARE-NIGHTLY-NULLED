package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IExplosionS2CPacket;
import net.minecraft.class_2664;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_2664.class})
public abstract class ExplosionS2CPacketMixin implements IExplosionS2CPacket {
   @Shadow
   @Final
   @Mutable
   private float field_12176;
   @Shadow
   @Final
   @Mutable
   private float field_12183;
   @Shadow
   @Final
   @Mutable
   private float field_12182;

   public void setVelocityX(float velocity) {
      this.field_12176 = velocity;
   }

   public void setVelocityY(float velocity) {
      this.field_12183 = velocity;
   }

   public void setVelocityZ(float velocity) {
      this.field_12182 = velocity;
   }
}
