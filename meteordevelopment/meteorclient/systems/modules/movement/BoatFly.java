package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.entity.BoatMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_243;
import net.minecraft.class_2692;

public class BoatFly extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> speed;
   private final Setting<Double> verticalSpeed;
   private final Setting<Double> fallSpeed;
   private final Setting<Boolean> cancelServerPackets;

   public BoatFly() {
      super(Categories.Movement, "boat-fly", "Transforms your boat into a plane.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.speed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("speed")).description("Horizontal speed in blocks per second.")).defaultValue(10.0D).min(0.0D).sliderMax(50.0D).build());
      this.verticalSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vertical-speed")).description("Vertical speed in blocks per second.")).defaultValue(6.0D).min(0.0D).sliderMax(20.0D).build());
      this.fallSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fall-speed")).description("How fast you fall in blocks per second.")).defaultValue(0.1D).min(0.0D).build());
      this.cancelServerPackets = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("cancel-server-packets")).description("Cancels incoming boat move packets.")).defaultValue(false)).build());
   }

   @EventHandler
   private void onBoatMove(BoatMoveEvent event) {
      if (event.boat.method_5642() == this.mc.field_1724) {
         event.boat.method_36456(this.mc.field_1724.method_36454());
         class_243 vel = PlayerUtils.getHorizontalVelocity((Double)this.speed.get());
         double velX = vel.method_10216();
         double velY = 0.0D;
         double velZ = vel.method_10215();
         if (this.mc.field_1690.field_1903.method_1434()) {
            velY += (Double)this.verticalSpeed.get() / 20.0D;
         }

         if (this.mc.field_1690.field_1867.method_1434()) {
            velY -= (Double)this.verticalSpeed.get() / 20.0D;
         } else {
            velY -= (Double)this.fallSpeed.get() / 20.0D;
         }

         ((IVec3d)event.boat.method_18798()).set(velX, velY, velZ);
      }
   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      if (event.packet instanceof class_2692 && (Boolean)this.cancelServerPackets.get()) {
         event.cancel();
      }

   }
}
