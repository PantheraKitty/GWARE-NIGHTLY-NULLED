package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IHorseBaseEntity;
import net.minecraft.class_1496;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_1496.class})
public abstract class HorseBaseEntityMixin implements IHorseBaseEntity {
   @Shadow
   protected abstract void method_6769(int var1, boolean var2);

   public void setSaddled(boolean saddled) {
      this.method_6769(4, saddled);
   }
}
