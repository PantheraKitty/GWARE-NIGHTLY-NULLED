package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.concurrent.CompletableFuture;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.ItemUseCrosshairTargetEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.game.ResolutionChangedEvent;
import meteordevelopment.meteorclient.events.game.ResourcePacksReloadedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.mixininterface.IMinecraftClient;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.FastUse;
import meteordevelopment.meteorclient.systems.modules.player.Multitask;
import meteordevelopment.meteorclient.systems.modules.render.UnfocusedCPU;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.CPSUtils;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.network.OnlinePlayers;
import meteordevelopment.orbit.ICancellable;
import meteordevelopment.starscript.Script;
import net.minecraft.class_1041;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_312;
import net.minecraft.class_315;
import net.minecraft.class_3695;
import net.minecraft.class_437;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(
   value = {class_310.class},
   priority = 1001
)
public abstract class MinecraftClientMixin implements IMinecraftClient {
   @Unique
   private boolean doItemUseCalled;
   @Unique
   private boolean rightClick;
   @Unique
   private long lastTime;
   @Unique
   private boolean firstFrame;
   @Shadow
   public class_638 field_1687;
   @Shadow
   @Final
   public class_312 field_1729;
   @Shadow
   @Final
   private class_1041 field_1704;
   @Shadow
   public class_437 field_1755;
   @Shadow
   @Final
   public class_315 field_1690;
   @Shadow
   @Nullable
   public class_636 field_1761;
   @Shadow
   private int field_1752;
   @Shadow
   @Nullable
   public class_746 field_1724;

   @Shadow
   protected abstract void method_1583();

   @Shadow
   public abstract class_3695 method_16011();

   @Shadow
   public abstract boolean method_1569();

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo info) {
      MeteorClient.INSTANCE.onInitializeClient();
      this.firstFrame = true;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"tick"}
   )
   private void onPreTick(CallbackInfo info) {
      OnlinePlayers.update();
      this.doItemUseCalled = false;
      this.method_16011().method_15396("meteor-client_pre_update");
      MeteorClient.EVENT_BUS.post((Object)TickEvent.Pre.get());
      this.method_16011().method_15407();
      if (this.rightClick && !this.doItemUseCalled && this.field_1761 != null) {
         this.method_1583();
      }

      this.rightClick = false;
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"tick"}
   )
   private void onTick(CallbackInfo info) {
      this.method_16011().method_15396("meteor-client_post_update");
      MeteorClient.EVENT_BUS.post((Object)TickEvent.Post.get());
      this.method_16011().method_15407();
   }

   @Inject(
      method = {"doAttack"},
      at = {@At("HEAD")}
   )
   private void onAttack(CallbackInfoReturnable<Boolean> cir) {
      CPSUtils.onAttack();
   }

   @Inject(
      method = {"doItemUse"},
      at = {@At("HEAD")}
   )
   private void onDoItemUse(CallbackInfo info) {
      this.doItemUseCalled = true;
   }

   @Inject(
      method = {"disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V"},
      at = {@At("HEAD")}
   )
   private void onDisconnect(class_437 screen, boolean transferring, CallbackInfo info) {
      if (this.field_1687 != null) {
         MeteorClient.EVENT_BUS.post((Object)GameLeftEvent.get());
      }

   }

   @Inject(
      method = {"setScreen"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSetScreen(class_437 screen, CallbackInfo info) {
      if (screen instanceof WidgetScreen) {
         screen.method_16014(this.field_1729.method_1603() * this.field_1704.method_4495(), this.field_1729.method_1604() * this.field_1704.method_4495());
      }

      OpenScreenEvent event = OpenScreenEvent.get(screen);
      MeteorClient.EVENT_BUS.post((ICancellable)event);
      if (event.isCancelled()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"doItemUse"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/item/ItemStack;isItemEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"
)},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void onDoItemUseHand(CallbackInfo ci, class_1268[] var1, int var2, int var3, class_1268 hand, class_1799 itemStack) {
      FastUse fastUse = (FastUse)Modules.get().get(FastUse.class);
      if (fastUse.isActive()) {
         this.field_1752 = fastUse.getItemUseCooldown(itemStack);
      }

   }

   @ModifyExpressionValue(
      method = {"doItemUse"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;",
   ordinal = 1
)}
   )
   private class_239 doItemUseMinecraftClientCrosshairTargetProxy(class_239 original) {
      return ((ItemUseCrosshairTargetEvent)MeteorClient.EVENT_BUS.post((Object)ItemUseCrosshairTargetEvent.get(original))).target;
   }

   @ModifyReturnValue(
      method = {"reloadResources(ZLnet/minecraft/client/MinecraftClient$LoadingContext;)Ljava/util/concurrent/CompletableFuture;"},
      at = {@At("RETURN")}
   )
   private CompletableFuture<Void> onReloadResourcesNewCompletableFuture(CompletableFuture<Void> original) {
      return original.thenRun(() -> {
         MeteorClient.EVENT_BUS.post((Object)ResourcePacksReloadedEvent.get());
      });
   }

   @ModifyArg(
      method = {"updateWindowTitle"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/Window;setTitle(Ljava/lang/String;)V"
)
   )
   private String setTitle(String original) {
      if (Config.get() != null && (Boolean)Config.get().customWindowTitle.get()) {
         String customTitle = (String)Config.get().customWindowTitleText.get();
         Script script = MeteorStarscript.compile(customTitle);
         if (script != null) {
            String title = MeteorStarscript.run(script);
            if (title != null) {
               customTitle = title;
            }
         }

         return customTitle;
      } else {
         return original;
      }
   }

   @Inject(
      method = {"onResolutionChanged"},
      at = {@At("TAIL")}
   )
   private void onResolutionChanged(CallbackInfo info) {
      MeteorClient.EVENT_BUS.post((Object)ResolutionChangedEvent.get());
   }

   @Inject(
      method = {"getFramerateLimit"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetFramerateLimit(CallbackInfoReturnable<Integer> info) {
      if (Modules.get().isActive(UnfocusedCPU.class) && !this.method_1569()) {
         info.setReturnValue(Math.min((Integer)((UnfocusedCPU)Modules.get().get(UnfocusedCPU.class)).fps.get(), (Integer)this.field_1690.method_42524().method_41753()));
      }

   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void onRender(CallbackInfo info) {
      long time = System.currentTimeMillis();
      if (this.firstFrame) {
         this.lastTime = time;
         this.firstFrame = false;
      }

      Utils.frameTime = (double)(time - this.lastTime) / 1000.0D;
      this.lastTime = time;
   }

   @ModifyExpressionValue(
      method = {"doItemUse"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"
)}
   )
   private boolean doItemUseModifyIsBreakingBlock(boolean original) {
      return !Modules.get().isActive(Multitask.class) && original;
   }

   @ModifyExpressionValue(
      method = {"handleBlockBreaking"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
)}
   )
   private boolean handleBlockBreakingModifyIsUsingItem(boolean original) {
      return !Modules.get().isActive(Multitask.class) && original;
   }

   @ModifyExpressionValue(
      method = {"handleInputEvents"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z",
   ordinal = 0
)}
   )
   private boolean handleInputEventsModifyIsUsingItem(boolean original) {
      return !((Multitask)Modules.get().get(Multitask.class)).attackingEntities() && original;
   }

   @Inject(
      method = {"handleInputEvents"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z",
   ordinal = 0,
   shift = Shift.BEFORE
)}
   )
   private void handleInputEventsInjectStopUsingItem(CallbackInfo info) {
      if (((Multitask)Modules.get().get(Multitask.class)).attackingEntities() && this.field_1724.method_6115()) {
         if (!this.field_1690.field_1904.method_1434()) {
            this.field_1761.method_2897(this.field_1724);
         }

         while(true) {
            if (this.field_1690.field_1904.method_1436()) {
               continue;
            }
         }
      }

   }

   public void meteor_client$rightClick() {
      this.rightClick = true;
   }
}
