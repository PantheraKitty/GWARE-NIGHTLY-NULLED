package meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightMode;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightModes;
import net.minecraft.class_1294;
import net.minecraft.class_1304;
import net.minecraft.class_1770;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_2708;
import net.minecraft.class_2848;
import net.minecraft.class_746;
import net.minecraft.class_2848.class_2849;

public class Slide extends ElytraFlightMode {
   boolean rubberbanded = false;
   int tickDelay;

   public Slide() {
      super(ElytraFlightModes.Slide);
      this.tickDelay = (Integer)this.elytraFly.restartDelay.get();
   }

   public void onTick() {
      super.onTick();
      if (this.mc.field_1690.field_1903.method_1434() && !this.mc.field_1724.method_6128()) {
         this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
      }

      if (checkConditions(this.mc.field_1724) && this.mc.field_1724.method_24828()) {
         double yaw = Math.toRadians((double)this.mc.field_1724.method_36454());
         double speedFactor = Math.max(0.1D, Math.min(1.0D, (100.0D * (Double)this.elytraFly.slideAccel.get() / 20.0D - this.mc.field_1724.method_18798().method_1033()) / (100.0D * (Double)this.elytraFly.slideAccel.get() / 20.0D)));
         class_243 dir = new class_243(-Math.sin(yaw), 0.0D, Math.cos(yaw));
         this.mc.field_1724.method_60491(dir.method_1021((Double)this.elytraFly.slideMaxSpeed.get() / 2000.0D / speedFactor));
         if (this.rubberbanded && (Boolean)this.elytraFly.restart.get()) {
            if (this.tickDelay > 0) {
               --this.tickDelay;
            } else {
               this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
               this.rubberbanded = false;
               this.tickDelay = (Integer)this.elytraFly.restartDelay.get();
            }
         }
      }

   }

   public void onPreTick() {
      super.onPreTick();
   }

   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.packet instanceof class_2708) {
         this.rubberbanded = true;
         this.mc.field_1724.method_23670();
      }

   }

   public void onPacketSend(PacketEvent.Send event) {
      if (event.packet instanceof class_2848 && ((class_2848)event.packet).method_12365().equals(class_2849.field_12982) && !(Boolean)this.elytraFly.sprint.get()) {
         this.mc.field_1724.method_5728(true);
      }

   }

   public static boolean recastElytra(class_746 player) {
      if (checkConditions(player) && ignoreGround(player)) {
         player.field_3944.method_52787(new class_2848(player, class_2849.field_12982));
         return true;
      } else {
         return false;
      }
   }

   public static boolean checkConditions(class_746 player) {
      class_1799 itemStack = player.method_6118(class_1304.field_6174);
      return !player.method_5765() && !player.method_6101() && itemStack.method_31574(class_1802.field_8833) && class_1770.method_7804(itemStack);
   }

   private static boolean ignoreGround(class_746 player) {
      if (!player.method_5799() && !player.method_6059(class_1294.field_5902)) {
         class_1799 itemStack = player.method_6118(class_1304.field_6174);
         if (itemStack.method_31574(class_1802.field_8833) && class_1770.method_7804(itemStack)) {
            player.method_23669();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void onActivate() {
   }

   public void onDeactivate() {
      this.rubberbanded = false;
   }
}
