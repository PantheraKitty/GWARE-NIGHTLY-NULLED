package meteordevelopment.meteorclient.systems.modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1744;
import net.minecraft.class_1802;

public class BowSpam extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> charge;
   private final Setting<Boolean> onlyWhenHoldingRightClick;
   private boolean wasBow;
   private boolean wasHoldingRightClick;

   public BowSpam() {
      super(Categories.Combat, "bow-spam", "Spams arrows.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.charge = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("charge")).description("How long to charge the bow before releasing in ticks.")).defaultValue(5)).range(5, 20).sliderRange(5, 20).build());
      this.onlyWhenHoldingRightClick = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("when-holding-right-click")).description("Works only when holding right click.")).defaultValue(false)).build());
      this.wasBow = false;
      this.wasHoldingRightClick = false;
   }

   public void onActivate() {
      this.wasBow = false;
      this.wasHoldingRightClick = false;
   }

   public void onDeactivate() {
      this.setPressed(false);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1724.method_31549().field_7477 || InvUtils.find((itemStack) -> {
         return itemStack.method_7909() instanceof class_1744;
      }).found()) {
         if ((Boolean)this.onlyWhenHoldingRightClick.get() && !this.mc.field_1690.field_1904.method_1434()) {
            if (this.wasHoldingRightClick) {
               this.setPressed(false);
               this.wasHoldingRightClick = false;
            }
         } else {
            boolean isBow = this.mc.field_1724.method_6047().method_7909() == class_1802.field_8102;
            if (!isBow && this.wasBow) {
               this.setPressed(false);
            }

            this.wasBow = isBow;
            if (!isBow) {
               return;
            }

            if (this.mc.field_1724.method_6048() >= (Integer)this.charge.get()) {
               this.mc.field_1761.method_2897(this.mc.field_1724);
            } else {
               this.setPressed(true);
            }

            this.wasHoldingRightClick = this.mc.field_1690.field_1904.method_1434();
         }

      }
   }

   private void setPressed(boolean pressed) {
      this.mc.field_1690.field_1904.method_23481(pressed);
   }
}
