package meteordevelopment.meteorclient.mixin;

import java.util.Collection;
import java.util.Iterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.RenderBossBarEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_2561;
import net.minecraft.class_337;
import net.minecraft.class_345;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_337.class})
public abstract class BossBarHudMixin {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRender(CallbackInfo info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noBossBar()) {
         info.cancel();
      }

   }

   @Redirect(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Ljava/util/Collection;iterator()Ljava/util/Iterator;"
)
   )
   public Iterator<class_345> onRender(Collection<class_345> collection) {
      RenderBossBarEvent.BossIterator event = (RenderBossBarEvent.BossIterator)MeteorClient.EVENT_BUS.post((Object)RenderBossBarEvent.BossIterator.get(collection.iterator()));
      return event.iterator;
   }

   @Redirect(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/hud/ClientBossBar;getName()Lnet/minecraft/text/Text;"
)
   )
   public class_2561 onAsFormattedString(class_345 clientBossBar) {
      RenderBossBarEvent.BossText event = (RenderBossBarEvent.BossText)MeteorClient.EVENT_BUS.post((Object)RenderBossBarEvent.BossText.get(clientBossBar, clientBossBar.method_5414()));
      return event.name;
   }

   @ModifyConstant(
      method = {"render"},
      constant = {@Constant(
   intValue = 9,
   ordinal = 1
)}
   )
   public int modifySpacingConstant(int j) {
      RenderBossBarEvent.BossSpacing event = (RenderBossBarEvent.BossSpacing)MeteorClient.EVENT_BUS.post((Object)RenderBossBarEvent.BossSpacing.get(j));
      return event.spacing;
   }
}
