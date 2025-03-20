package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import net.minecraft.class_2793;

public class PacketSaver extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> grimRubberbandResponse;

   public PacketSaver() {
      super(Categories.Misc, "packet-saver", "Stops the client from sending unnecessary packets. Helps with packet kicks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.grimRubberbandResponse = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-grim-rubberband")).description("Stops the client from responding to Grim rubberband packets")).defaultValue(true)).build());
   }

   @EventHandler(
      priority = 201
   )
   private void onPacketSend(PacketEvent.Send event) {
      if ((Boolean)this.grimRubberbandResponse.get()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2793) {
            class_2793 packet = (class_2793)var3;
            if (packet.method_12086() < 0) {
               event.cancel();
            }
         }
      }

   }
}
