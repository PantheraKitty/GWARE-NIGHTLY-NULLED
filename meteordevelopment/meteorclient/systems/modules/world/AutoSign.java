package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.AbstractSignEditScreenAccessor;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2625;
import net.minecraft.class_2877;
import net.minecraft.class_7743;

public class AutoSign extends Module {
   private String[] text;

   public AutoSign() {
      super(Categories.World, "auto-sign", "Automatically writes signs. The first sign's text will be used.");
   }

   public void onDeactivate() {
      this.text = null;
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if (event.packet instanceof class_2877) {
         this.text = ((class_2877)event.packet).method_12508();
      }
   }

   @EventHandler
   private void onOpenScreen(OpenScreenEvent event) {
      if (event.screen instanceof class_7743 && this.text != null) {
         class_2625 sign = ((AbstractSignEditScreenAccessor)event.screen).getSign();
         this.mc.field_1724.field_3944.method_52787(new class_2877(sign.method_11016(), true, this.text[0], this.text[1], this.text[2], this.text[3]));
         event.cancel();
      }
   }
}
