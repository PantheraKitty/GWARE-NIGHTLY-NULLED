package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IExplosion;
import net.minecraft.class_1297;
import net.minecraft.class_1927;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_1927.class_4179;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_1927.class})
public abstract class ExplosionMixin implements IExplosion {
   @Shadow
   @Final
   @Mutable
   private class_1937 field_9187;
   @Shadow
   @Final
   @Mutable
   @Nullable
   private class_1297 field_9185;
   @Shadow
   @Final
   @Mutable
   private double field_9195;
   @Shadow
   @Final
   @Mutable
   private double field_9192;
   @Shadow
   @Final
   @Mutable
   private double field_9189;
   @Shadow
   @Final
   @Mutable
   private float field_9190;
   @Shadow
   @Final
   @Mutable
   private boolean field_9186;
   @Shadow
   @Final
   @Mutable
   private class_4179 field_9184;

   public void set(class_243 pos, float power, boolean createFire) {
      this.field_9187 = MeteorClient.mc.field_1687;
      this.field_9185 = null;
      this.field_9195 = pos.field_1352;
      this.field_9192 = pos.field_1351;
      this.field_9189 = pos.field_1350;
      this.field_9190 = power;
      this.field_9186 = createFire;
      this.field_9184 = class_4179.field_18687;
   }
}
