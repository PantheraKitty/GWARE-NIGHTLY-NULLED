package meteordevelopment.meteorclient.mixin;

import it.unimi.dsi.fastutil.Pair;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoReconnect;
import net.minecraft.class_2561;
import net.minecraft.class_412;
import net.minecraft.class_4185;
import net.minecraft.class_419;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_639;
import net.minecraft.class_642;
import net.minecraft.class_8667;
import net.minecraft.class_9112;
import net.minecraft.class_4185.class_7840;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({class_419.class})
public abstract class DisconnectedScreenMixin extends class_437 {
   @Shadow
   @Final
   private class_8667 field_44552;
   @Unique
   private class_4185 reconnectBtn;
   @Unique
   private double time;

   protected DisconnectedScreenMixin(class_2561 title) {
      super(title);
      this.time = (Double)((AutoReconnect)Modules.get().get(AutoReconnect.class)).time.get() * 20.0D;
   }

   @Inject(
      method = {"init"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;refreshPositions()V",
   shift = Shift.BEFORE
)},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void addButtons(CallbackInfo ci, class_4185 buttonWidget) {
      AutoReconnect autoReconnect = (AutoReconnect)Modules.get().get(AutoReconnect.class);
      if (autoReconnect.lastServerConnection != null) {
         this.reconnectBtn = (new class_7840(class_2561.method_43470(this.getText()), (button) -> {
            this.tryConnecting();
         })).method_46431();
         this.field_44552.method_52736(this.reconnectBtn);
         this.field_44552.method_52736((new class_7840(class_2561.method_43470("Toggle Auto Reconnect"), (button) -> {
            autoReconnect.toggle();
            this.reconnectBtn.method_25355(class_2561.method_43470(this.getText()));
            this.time = (Double)autoReconnect.time.get() * 20.0D;
         })).method_46431());
      }

   }

   public void method_25393() {
      AutoReconnect autoReconnect = (AutoReconnect)Modules.get().get(AutoReconnect.class);
      if (autoReconnect.isActive() && autoReconnect.lastServerConnection != null) {
         if (this.time <= 0.0D) {
            this.tryConnecting();
         } else {
            --this.time;
            if (this.reconnectBtn != null) {
               this.reconnectBtn.method_25355(class_2561.method_43470(this.getText()));
            }
         }

      }
   }

   @Unique
   private String getText() {
      String reconnectText = "Reconnect";
      if (Modules.get().isActive(AutoReconnect.class)) {
         reconnectText = reconnectText + " " + String.format("(%.1f)", this.time / 20.0D);
      }

      return reconnectText;
   }

   @Unique
   private void tryConnecting() {
      Pair<class_639, class_642> lastServer = ((AutoReconnect)Modules.get().get(AutoReconnect.class)).lastServerConnection;
      class_412.method_36877(new class_442(), MeteorClient.mc, (class_639)lastServer.left(), (class_642)lastServer.right(), false, (class_9112)null);
   }
}
