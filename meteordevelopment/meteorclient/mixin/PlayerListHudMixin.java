package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTab;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_355;
import net.minecraft.class_640;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_355.class})
public abstract class PlayerListHudMixin {
   @Shadow
   protected abstract List<class_640> method_48213();

   @Inject(
      method = {"collectPlayerEntries"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void modifyPlayerEntries(CallbackInfoReturnable<List<class_640>> cir) {
      List<class_640> originalList = (List)cir.getReturnValue();
      BetterTab betterTab = (BetterTab)Modules.get().get(BetterTab.class);
      Stream var10000 = originalList.stream();
      Objects.requireNonNull(betterTab);
      List<class_640> modifiedList = var10000.filter(betterTab::shouldShowPlayer).toList();
      cir.setReturnValue(modifiedList);
   }

   @ModifyConstant(
      constant = {@Constant(
   longValue = 80L
)},
      method = {"collectPlayerEntries"}
   )
   private long modifyCount(long count) {
      BetterTab module = (BetterTab)Modules.get().get(BetterTab.class);
      return module.isActive() ? (long)(Integer)module.tabSize.get() : count;
   }

   @Inject(
      method = {"getPlayerName"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getPlayerName(class_640 playerListEntry, CallbackInfoReturnable<class_2561> info) {
      BetterTab betterTab = (BetterTab)Modules.get().get(BetterTab.class);
      if (betterTab.isActive()) {
         info.setReturnValue(betterTab.getPlayerName(playerListEntry));
      }

   }

   @ModifyArg(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Ljava/lang/Math;min(II)I"
),
      index = 0
   )
   private int modifyWidth(int width) {
      BetterTab module = (BetterTab)Modules.get().get(BetterTab.class);
      return module.isActive() && (Boolean)module.accurateLatency.get() ? width + 30 : width;
   }

   @Inject(
      method = {"render"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/lang/Math;min(II)I",
   shift = Shift.BEFORE
)}
   )
   private void modifyHeight(CallbackInfo ci, @Local(ordinal = 5) LocalIntRef o, @Local(ordinal = 6) LocalIntRef p) {
      BetterTab module = (BetterTab)Modules.get().get(BetterTab.class);
      if (module.isActive()) {
         int newP = 1;

         int newO;
         for(int totalPlayers = newO = this.method_48213().size(); newO > (Integer)module.tabHeight.get(); newO = (totalPlayers + newP - 1) / newP) {
            ++newP;
         }

         o.set(newO);
         p.set(newP);
      }
   }

   @Inject(
      method = {"renderLatencyIcon"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderLatencyIcon(class_332 context, int width, int x, int y, class_640 entry, CallbackInfo ci) {
      BetterTab betterTab = (BetterTab)Modules.get().get(BetterTab.class);
      if (betterTab.isActive() && (Boolean)betterTab.accurateLatency.get()) {
         class_310 mc = class_310.method_1551();
         class_327 textRenderer = mc.field_1772;
         int latency = class_3532.method_15340(entry.method_2959(), 0, 9999);
         int color = latency < 150 ? '\ue970' : (latency < 300 ? 15192096 : 14107192);
         String text = latency + "ms";
         context.method_25303(textRenderer, text, x + width - textRenderer.method_1727(text), y, color);
         ci.cancel();
      }

   }
}
