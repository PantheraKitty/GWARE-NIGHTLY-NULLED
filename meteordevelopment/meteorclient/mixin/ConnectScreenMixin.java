package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.ServerConnectBeginEvent;
import net.minecraft.class_310;
import net.minecraft.class_412;
import net.minecraft.class_639;
import net.minecraft.class_642;
import net.minecraft.class_9112;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_412.class})
public abstract class ConnectScreenMixin {
   @Inject(
      method = {"connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;Lnet/minecraft/client/network/CookieStorage;)V"},
      at = {@At("HEAD")}
   )
   private void tryConnectEvent(class_310 client, class_639 address, class_642 info, class_9112 cookieStorage, CallbackInfo ci) {
      MeteorClient.EVENT_BUS.post((Object)ServerConnectBeginEvent.get(address, info));
   }
}
