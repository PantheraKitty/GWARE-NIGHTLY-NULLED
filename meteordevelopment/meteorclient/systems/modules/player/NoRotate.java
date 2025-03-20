package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.PlayerPositionLookS2CPacketAccessor;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import net.minecraft.class_2708;
import net.minecraft.class_2709;

public class NoRotate extends Module {
   public NoRotate() {
      super(Categories.Player, "no-rotate", "Attempts to block rotations sent from server to client.");
   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2708) {
         class_2708 packet = (class_2708)var3;
         if (packet.method_11733().contains(class_2709.field_12401)) {
            ((PlayerPositionLookS2CPacketAccessor)packet).setYaw(0.0F);
         } else {
            ((PlayerPositionLookS2CPacketAccessor)packet).setYaw(this.mc.field_1724.method_36454());
         }

         if (packet.method_11733().contains(class_2709.field_12397)) {
            ((PlayerPositionLookS2CPacketAccessor)packet).setPitch(0.0F);
         } else {
            ((PlayerPositionLookS2CPacketAccessor)packet).setPitch(this.mc.field_1724.method_36455());
         }
      }

   }
}
