package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2848;
import net.minecraft.class_2848.class_2849;

public class VanillaFakeFly extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<VanillaFakeFly.Mode> mode;
   private boolean wantFirework;

   public VanillaFakeFly() {
      super(Categories.Movement, "vanilla-fakefly", "Fakes your fly.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("How to mode")).defaultValue(VanillaFakeFly.Mode.Fly)).build());
      this.wantFirework = false;
   }

   public void onActivate() {
      this.mc.field_1724.method_23670();
   }

   public void onDeactivate() {
      if (this.isBounce()) {
         this.mc.field_1690.field_1903.method_23481(false);
         Input.setKeyState(this.mc.field_1690.field_1903, false);
         this.mc.field_1690.field_1894.method_23481(false);
         Input.setKeyState(this.mc.field_1690.field_1894, false);
         this.mc.field_1724.method_5728(false);
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.isFlying()) {
         if (this.isBounce()) {
            this.mc.field_1690.field_1903.method_23481(true);
            Input.setKeyState(this.mc.field_1690.field_1903, true);
            this.mc.field_1690.field_1894.method_23481(true);
            Input.setKeyState(this.mc.field_1690.field_1894, true);
            this.mc.field_1724.method_5728(true);
         }

         PlayerUtils.silentSwapEquipElytra();
         this.mc.field_1724.field_3944.method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
         if (this.wantFirework) {
            if (MeteorClient.SWAP.beginSwap(class_1802.field_8639, true)) {
               this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
               MeteorClient.SWAP.endSwap(true);
            }

            this.wantFirework = false;
         }

         PlayerUtils.silentSwapEquipChestplate();
      }
   }

   public boolean isFlying() {
      return this.mc.field_1724.method_24828() && !this.isBounce() ? false : this.isActive();
   }

   public boolean isBounce() {
      return this.mode.get() == VanillaFakeFly.Mode.Bounce;
   }

   public void requestFirework() {
      this.wantFirework = true;
   }

   private static enum Mode {
      Fly,
      Control,
      Bounce;

      // $FF: synthetic method
      private static VanillaFakeFly.Mode[] $values() {
         return new VanillaFakeFly.Mode[]{Fly, Control, Bounce};
      }
   }
}
