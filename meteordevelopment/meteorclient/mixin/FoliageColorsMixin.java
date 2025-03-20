package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1926;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_1926.class})
public abstract class FoliageColorsMixin {
   @ModifyReturnValue(
      method = {"getBirchColor"},
      at = {@At("RETURN")}
   )
   private static int onGetBirchColor(int original) {
      return getModifiedColor(original);
   }

   @ModifyReturnValue(
      method = {"getSpruceColor"},
      at = {@At("RETURN")}
   )
   private static int onGetSpruceColor(int original) {
      return getModifiedColor(original);
   }

   @ModifyReturnValue(
      method = {"getMangroveColor"},
      at = {@At("RETURN")}
   )
   private static int onGetMangroveColor(int original) {
      return getModifiedColor(original);
   }

   @Unique
   private static int getModifiedColor(int original) {
      if (Modules.get() == null) {
         return original;
      } else {
         Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
         return ambience.isActive() && (Boolean)ambience.customFoliageColor.get() ? ((SettingColor)ambience.foliageColor.get()).getPacked() : original;
      }
   }
}
