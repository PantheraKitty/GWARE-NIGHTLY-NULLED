package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1304;
import net.minecraft.class_1713;
import net.minecraft.class_1802;
import net.minecraft.class_2848;
import net.minecraft.class_2848.class_2849;

public class GrimDisabler extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<GrimDisabler.HorizontalDisablerMode> horizontalDisblerMode;
   private boolean fallFlyingBoostState;

   public GrimDisabler() {
      super(Categories.Movement, "grim-disabler", "Disables the Grim anti-cheat. Allows use of modules such as Speed and ClickTp");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.horizontalDisblerMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("horizontal-disabler-mode")).description("Determines mode of disabler for horizontal movement")).defaultValue(GrimDisabler.HorizontalDisablerMode.YawOverflow)).build());
      this.fallFlyingBoostState = false;
   }

   @EventHandler
   public void onPreMove(SendMovementPacketsEvent.Pre event) {
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
   }

   public boolean isInElytraFlyState() {
      return this.isActive() && this.fallFlyingBoostState;
   }

   public boolean shouldSetYawOverflowRotation() {
      return this.isActive() && this.horizontalDisblerMode.get() == GrimDisabler.HorizontalDisablerMode.YawOverflow;
   }

   private void stopFallFlying() {
      if (this.mc.field_1724.method_6118(class_1304.field_6174).method_7909().equals(class_1802.field_8833)) {
         this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, 6, 0, class_1713.field_7790, this.mc.field_1724);
         this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, 6, 0, class_1713.field_7790, this.mc.field_1724);
      }
   }

   private void startFallFlying() {
      if (this.mc.field_1724.method_6118(class_1304.field_6174).method_7909().equals(class_1802.field_8833)) {
         this.mc.field_1724.field_3944.method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
      }
   }

   public String getInfoString() {
      return this.horizontalDisblerMode.get() == GrimDisabler.HorizontalDisablerMode.None ? "" : String.format("%s", ((GrimDisabler.HorizontalDisablerMode)this.horizontalDisblerMode.get()).toString());
   }

   public static enum HorizontalDisablerMode {
      None,
      YawOverflow;

      // $FF: synthetic method
      private static GrimDisabler.HorizontalDisablerMode[] $values() {
         return new GrimDisabler.HorizontalDisablerMode[]{None, YawOverflow};
      }
   }
}
