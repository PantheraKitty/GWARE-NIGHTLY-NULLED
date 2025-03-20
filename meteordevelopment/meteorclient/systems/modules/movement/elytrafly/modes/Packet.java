package meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightMode;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightModes;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_2828;
import net.minecraft.class_2848;
import net.minecraft.class_2828.class_5911;
import net.minecraft.class_2848.class_2849;

public class Packet extends ElytraFlightMode {
   private final class_243 vec3d = new class_243(0.0D, 0.0D, 0.0D);

   public Packet() {
      super(ElytraFlightModes.Packet);
   }

   public void onDeactivate() {
      this.mc.field_1724.method_31549().field_7479 = false;
      this.mc.field_1724.method_31549().field_7478 = false;
   }

   public void onTick() {
      super.onTick();
      if (this.mc.field_1724.method_31548().method_7372(2).method_7909() == class_1802.field_8833 && !((double)this.mc.field_1724.field_6017 <= 0.2D) && !this.mc.field_1690.field_1832.method_1434()) {
         if (this.mc.field_1690.field_1894.method_1434()) {
            this.vec3d.method_1031(0.0D, 0.0D, (Double)this.elytraFly.horizontalSpeed.get());
            this.vec3d.method_1024(-((float)Math.toRadians((double)this.mc.field_1724.method_36454())));
         } else if (this.mc.field_1690.field_1881.method_1434()) {
            this.vec3d.method_1031(0.0D, 0.0D, (Double)this.elytraFly.horizontalSpeed.get());
            this.vec3d.method_1024((float)Math.toRadians((double)this.mc.field_1724.method_36454()));
         }

         if (this.mc.field_1690.field_1903.method_1434()) {
            this.vec3d.method_1031(0.0D, (Double)this.elytraFly.verticalSpeed.get(), 0.0D);
         } else if (!this.mc.field_1690.field_1903.method_1434()) {
            this.vec3d.method_1031(0.0D, -(Double)this.elytraFly.verticalSpeed.get(), 0.0D);
         }

         this.mc.field_1724.method_18799(this.vec3d);
         this.mc.field_1724.field_3944.method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
         this.mc.field_1724.field_3944.method_52787(new class_5911(true));
      }
   }

   public void onPacketSend(PacketEvent.Send event) {
      if (event.packet instanceof class_2828) {
         this.mc.field_1724.field_3944.method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
      }

   }

   public void onPlayerMove() {
      this.mc.field_1724.method_31549().field_7479 = true;
      this.mc.field_1724.method_31549().method_7248(((Double)this.elytraFly.horizontalSpeed.get()).floatValue() / 20.0F);
   }
}
