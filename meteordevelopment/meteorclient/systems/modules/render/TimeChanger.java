package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2761;

public class TimeChanger extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> time;
   long oldTime;

   public TimeChanger() {
      super(Categories.Render, "time-changer", "Makes you able to set a custom time.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.time = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("time")).description("The specified time to be set.")).defaultValue(0.0D).sliderRange(-20000.0D, 20000.0D).build());
   }

   public void onActivate() {
      this.oldTime = this.mc.field_1687.method_8510();
   }

   public void onDeactivate() {
      this.mc.field_1687.method_8435(this.oldTime);
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (event.packet instanceof class_2761) {
         this.oldTime = ((class_2761)event.packet).method_11871();
         event.cancel();
      }

   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      this.mc.field_1687.method_8435(((Double)this.time.get()).longValue());
   }
}
