package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2244;
import net.minecraft.class_2338.class_2339;

public class ReverseStep extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> fallSpeed;
   private final Setting<Double> fallDistance;

   public ReverseStep() {
      super(Categories.Movement, "reverse-step", "Allows you to fall down blocks at a greater speed.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.fallSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fall-speed")).description("How fast to fall in blocks per second.")).defaultValue(3.0D).min(0.0D).build());
      this.fallDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fall-distance")).description("The maximum fall distance this setting will activate at.")).defaultValue(3.0D).min(0.0D).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1724.method_24828() && !this.mc.field_1724.method_21754() && !this.mc.field_1724.method_5869() && !this.mc.field_1724.method_5771() && !this.mc.field_1690.field_1903.method_1434() && !this.mc.field_1724.field_5960 && (this.mc.field_1724.field_6250 != 0.0F || this.mc.field_1724.field_6212 != 0.0F)) {
         if (!this.isOnBed() && !this.mc.field_1687.method_18026(this.mc.field_1724.method_5829().method_989(0.0D, (double)((float)(-((Double)this.fallDistance.get() + 0.01D))), 0.0D))) {
            ((IVec3d)this.mc.field_1724.method_18798()).setY(-(Double)this.fallSpeed.get());
         }

      }
   }

   private boolean isOnBed() {
      class_2339 blockPos = this.mc.field_1724.method_24515().method_25503();
      if (this.check(blockPos, 0, 0)) {
         return true;
      } else {
         double xa = this.mc.field_1724.method_23317() - (double)blockPos.method_10263();
         double za = this.mc.field_1724.method_23321() - (double)blockPos.method_10260();
         if (xa >= 0.0D && xa <= 0.3D && this.check(blockPos, -1, 0)) {
            return true;
         } else if (xa >= 0.7D && this.check(blockPos, 1, 0)) {
            return true;
         } else if (za >= 0.0D && za <= 0.3D && this.check(blockPos, 0, -1)) {
            return true;
         } else if (za >= 0.7D && this.check(blockPos, 0, 1)) {
            return true;
         } else if (xa >= 0.0D && xa <= 0.3D && za >= 0.0D && za <= 0.3D && this.check(blockPos, -1, -1)) {
            return true;
         } else if (xa >= 0.0D && xa <= 0.3D && za >= 0.7D && this.check(blockPos, -1, 1)) {
            return true;
         } else if (xa >= 0.7D && za >= 0.0D && za <= 0.3D && this.check(blockPos, 1, -1)) {
            return true;
         } else {
            return xa >= 0.7D && za >= 0.7D && this.check(blockPos, 1, 1);
         }
      }
   }

   private boolean check(class_2339 blockPos, int x, int z) {
      blockPos.method_10100(x, 0, z);
      boolean is = this.mc.field_1687.method_8320(blockPos).method_26204() instanceof class_2244;
      blockPos.method_10100(-x, 0, -z);
      return is;
   }
}
