package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.AutoSmelter;
import net.minecraft.class_1661;
import net.minecraft.class_1720;
import net.minecraft.class_2561;
import net.minecraft.class_465;
import net.minecraft.class_489;
import net.minecraft.class_518;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_489.class})
public abstract class AbstractFurnaceScreenMixin<T extends class_1720> extends class_465<T> implements class_518 {
   public AbstractFurnaceScreenMixin(T container, class_1661 playerInventory, class_2561 name) {
      super(container, playerInventory, name);
   }

   @Inject(
      method = {"handledScreenTick"},
      at = {@At("TAIL")}
   )
   private void onTick(CallbackInfo info) {
      if (Modules.get().isActive(AutoSmelter.class)) {
         ((AutoSmelter)Modules.get().get(AutoSmelter.class)).tick((class_1720)this.field_2797);
      }

   }
}
