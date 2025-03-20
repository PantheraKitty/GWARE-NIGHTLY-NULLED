package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.InventoryTweaks;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.systems.modules.render.ItemHighlight;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_3872;
import net.minecraft.class_3936;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_9334;
import net.minecraft.class_3872.class_3931;
import net.minecraft.class_4185.class_7840;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_465.class})
public abstract class HandledScreenMixin<T extends class_1703> extends class_437 implements class_3936<T> {
   @Shadow
   protected class_1735 field_2787;
   @Shadow
   protected int field_2776;
   @Shadow
   protected int field_2800;
   @Shadow
   private boolean field_2783;
   @Unique
   private static final class_1799[] ITEMS = new class_1799[27];

   @Shadow
   @Nullable
   protected abstract class_1735 method_2386(double var1, double var3);

   @Shadow
   public abstract T method_17577();

   @Shadow
   protected abstract void method_2383(class_1735 var1, int var2, int var3, class_1713 var4);

   @Shadow
   public abstract void method_25419();

   public HandledScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"init"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo info) {
      InventoryTweaks invTweaks = (InventoryTweaks)Modules.get().get(InventoryTweaks.class);
      if (invTweaks.isActive() && invTweaks.showButtons() && invTweaks.canSteal(this.method_17577())) {
         this.method_37063((new class_7840(class_2561.method_43470("Steal"), (button) -> {
            invTweaks.steal(this.method_17577());
         })).method_46433(this.field_2776, this.field_2800 - 22).method_46437(40, 20).method_46431());
         this.method_37063((new class_7840(class_2561.method_43470("Dump"), (button) -> {
            invTweaks.dump(this.method_17577());
         })).method_46433(this.field_2776 + 42, this.field_2800 - 22).method_46437(40, 20).method_46431());
      }

   }

   @Inject(
      method = {"mouseDragged"},
      at = {@At("TAIL")}
   )
   private void onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> info) {
      if (button == 0 && !this.field_2783 && ((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).mouseDragItemMove()) {
         class_1735 slot = this.method_2386(mouseX, mouseY);
         if (slot != null && slot.method_7681() && method_25442()) {
            this.method_2383(slot, slot.field_7874, button, class_1713.field_7794);
         }

      }
   }

   @Inject(
      method = {"mouseClicked"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      BetterTooltips toolips = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      if (button == 2 && this.field_2787 != null && !this.field_2787.method_7677().method_7960() && MeteorClient.mc.field_1724.field_7512.method_34255().method_7960() && toolips.middleClickOpen()) {
         class_1799 itemStack = this.field_2787.method_7677();
         if (!Utils.hasItems(itemStack) && itemStack.method_7909() != class_1802.field_8466) {
            if (itemStack.method_57824(class_9334.field_49606) != null || itemStack.method_57824(class_9334.field_49653) != null) {
               this.method_25419();
               MeteorClient.mc.method_1507(new class_3872(class_3931.method_17562(itemStack)));
               cir.setReturnValue(true);
            }
         } else {
            cir.setReturnValue(Utils.openContainer(this.field_2787.method_7677(), ITEMS, false));
         }
      }

   }

   @Inject(
      method = {"drawSlot"},
      at = {@At("HEAD")}
   )
   private void onDrawSlot(class_332 context, class_1735 slot, CallbackInfo ci) {
      int color = ((ItemHighlight)Modules.get().get(ItemHighlight.class)).getColor(slot.method_7677());
      if (color != -1) {
         context.method_25294(slot.field_7873, slot.field_7872, slot.field_7873 + 16, slot.field_7872 + 16, color);
      }

   }
}
