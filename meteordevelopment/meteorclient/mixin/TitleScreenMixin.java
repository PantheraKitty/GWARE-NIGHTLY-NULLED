package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.TitleScreenCredits;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_442;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_442.class})
public abstract class TitleScreenMixin extends class_437 {
   public TitleScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"render"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I",
   ordinal = 0
)}
   )
   private void onRenderIdkDude(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (Utils.firstTimeTitleScreen) {
         Utils.firstTimeTitleScreen = false;
         if (!MeteorClient.VERSION.isZero()) {
            MeteorClient.LOG.info("Checking latest version of Meteor Client");
         }
      }

   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void onRender(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if ((Boolean)Config.get().titleScreenCredits.get()) {
         TitleScreenCredits.render(context);
      }

   }

   @Inject(
      method = {"mouseClicked"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
      if ((Boolean)Config.get().titleScreenCredits.get() && button == 0 && TitleScreenCredits.onClicked(mouseX, mouseY)) {
         info.setReturnValue(true);
      }

   }
}
