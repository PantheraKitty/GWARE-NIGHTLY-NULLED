package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.List;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.Slippy;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.minecraft.class_1922;
import net.minecraft.class_1935;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_4970;
import net.minecraft.class_4970.class_2251;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_2248.class})
public abstract class BlockMixin extends class_4970 implements class_1935 {
   public BlockMixin(class_2251 settings) {
      super(settings);
   }

   @ModifyReturnValue(
      method = {"shouldDrawSide"},
      at = {@At("RETURN")}
   )
   private static boolean onShouldDrawSide(boolean original, class_2680 state, class_1922 world, class_2338 pos, class_2350 side, class_2338 blockPos) {
      Xray xray = (Xray)Modules.get().get(Xray.class);
      return xray.isActive() ? xray.modifyDrawSide(state, world, pos, side, original) : original;
   }

   @ModifyReturnValue(
      method = {"getSlipperiness"},
      at = {@At("RETURN")}
   )
   public float getSlipperiness(float original) {
      if (Modules.get() == null) {
         return original;
      } else {
         Slippy slippy = (Slippy)Modules.get().get(Slippy.class);
         class_2248 block = (class_2248)this;
         if (slippy.isActive()) {
            if (slippy.listMode.get() == Slippy.ListMode.Whitelist) {
               if (!((List)slippy.allowedBlocks.get()).contains(block)) {
                  return block == class_2246.field_10030 && ((NoSlow)Modules.get().get(NoSlow.class)).slimeBlock() ? 0.6F : original;
               }
            } else if (((List)slippy.ignoredBlocks.get()).contains(block)) {
               return block == class_2246.field_10030 && ((NoSlow)Modules.get().get(NoSlow.class)).slimeBlock() ? 0.6F : original;
            }

            return ((Double)slippy.friction.get()).floatValue();
         } else {
            return block == class_2246.field_10030 && ((NoSlow)Modules.get().get(NoSlow.class)).slimeBlock() ? 0.6F : original;
         }
      }
   }
}
