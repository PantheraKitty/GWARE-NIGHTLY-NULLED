package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.MovementFix;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2560;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_2846.class_2847;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2560.class})
public abstract class CobwebBlockMixin {
   @Inject(
      method = {"onEntityCollision"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onEntityCollision(class_2680 state, class_1937 world, class_2338 pos, class_1297 entity, CallbackInfo info) {
      if (entity == MeteorClient.mc.field_1724) {
         NoSlow noSlow = (NoSlow)Modules.get().get(NoSlow.class);
         if (noSlow.cobweb()) {
            info.cancel();
         }

         if (noSlow.cobwebGrim()) {
            info.cancel();
            int s1 = MeteorClient.mc.field_1687.method_41925().method_41937().method_41942();
            MeteorClient.mc.method_1562().method_52787(new class_2846(class_2847.field_12973, pos, class_2350.field_11036, s1));
            MovementFix.inWebs = true;
         }
      }

   }
}
