package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.mixininterface.IEntityRenderer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Fullbright;
import meteordevelopment.meteorclient.systems.modules.render.Nametags;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.render.postprocess.PostProcessShaders;
import net.minecraft.class_1297;
import net.minecraft.class_1540;
import net.minecraft.class_1657;
import net.minecraft.class_1944;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4604;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_897.class})
public abstract class EntityRendererMixin<T extends class_1297> implements IEntityRenderer {
   @Shadow
   public abstract class_2960 method_3931(class_1297 var1);

   @Inject(
      method = {"renderLabelIfPresent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderLabel(T entity, class_2561 text, class_4587 matrices, class_4597 vertexConsumers, int light, float tickDelta, CallbackInfo ci) {
      if (PostProcessShaders.rendering) {
         ci.cancel();
      }

      if (((NoRender)Modules.get().get(NoRender.class)).noNametags()) {
         ci.cancel();
      }

      if (entity instanceof class_1657) {
         if (((Nametags)Modules.get().get(Nametags.class)).playerNametags() && (EntityUtils.getGameMode((class_1657)entity) != null || !((Nametags)Modules.get().get(Nametags.class)).excludeBots())) {
            ci.cancel();
         }

      }
   }

   @Inject(
      method = {"shouldRender"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void shouldRender(T entity, class_4604 frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
      if (((NoRender)Modules.get().get(NoRender.class)).noEntity(entity)) {
         cir.cancel();
      }

      if (((NoRender)Modules.get().get(NoRender.class)).noFallingBlocks() && entity instanceof class_1540) {
         cir.cancel();
      }

   }

   @ModifyReturnValue(
      method = {"getSkyLight"},
      at = {@At("RETURN")}
   )
   private int onGetSkyLight(int original) {
      return Math.max(((Fullbright)Modules.get().get(Fullbright.class)).getLuminance(class_1944.field_9284), original);
   }

   @ModifyReturnValue(
      method = {"getBlockLight"},
      at = {@At("RETURN")}
   )
   private int onGetBlockLight(int original) {
      return Math.max(((Fullbright)Modules.get().get(Fullbright.class)).getLuminance(class_1944.field_9282), original);
   }

   public class_2960 getTextureInterface(class_1297 entity) {
      return this.method_3931(entity);
   }
}
