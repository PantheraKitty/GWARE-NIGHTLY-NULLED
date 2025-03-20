package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_239;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_2350.class_2351;
import net.minecraft.class_239.class_240;

public class ClickTP extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> maxDistance;

   public ClickTP() {
      super(Categories.Movement, "click-tp", "Teleports you to the block you click on.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.maxDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-distance")).description("The maximum distance you can teleport.")).defaultValue(5.0D).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (!this.mc.field_1724.method_6115()) {
         if (this.mc.field_1690.field_1904.method_1434()) {
            class_239 hitResult = this.mc.field_1724.method_5745((Double)this.maxDistance.get(), 0.05F, false);
            if (hitResult.method_17783() == class_240.field_1331 && this.mc.field_1724.method_7287(((class_3966)hitResult).method_17782(), class_1268.field_5808) != class_1269.field_5811) {
               return;
            }

            if (hitResult.method_17783() == class_240.field_1332) {
               class_2338 pos = ((class_3965)hitResult).method_17777();
               class_2350 side = ((class_3965)hitResult).method_17780();
               if (this.mc.field_1687.method_8320(pos).method_55781(this.mc.field_1687, this.mc.field_1724, (class_3965)hitResult) != class_1269.field_5811) {
                  return;
               }

               class_2680 state = this.mc.field_1687.method_8320(pos);
               class_265 shape = state.method_26220(this.mc.field_1687, pos);
               if (shape.method_1110()) {
                  shape = state.method_26218(this.mc.field_1687, pos);
               }

               double height = shape.method_1110() ? 1.0D : shape.method_1105(class_2351.field_11052);
               this.mc.field_1724.method_5814((double)pos.method_10263() + 0.5D + (double)side.method_10148(), (double)pos.method_10264() + height, (double)pos.method_10260() + 0.5D + (double)side.method_10165());
            }
         }

      }
   }
}
