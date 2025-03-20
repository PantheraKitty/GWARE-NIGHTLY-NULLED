package meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightMode;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightModes;

public class Pitch40 extends ElytraFlightMode {
   private boolean pitchingDown = true;
   private int pitch;

   public Pitch40() {
      super(ElytraFlightModes.Pitch40);
   }

   public void onActivate() {
      if (this.mc.field_1724.method_23318() < (Double)this.elytraFly.pitch40upperBounds.get()) {
         this.elytraFly.error("Player must be above upper bounds!", new Object[0]);
         this.elytraFly.toggle();
      }

      this.pitch = 40;
   }

   public void onDeactivate() {
   }

   public void onTick() {
      super.onTick();
      if (this.pitchingDown && this.mc.field_1724.method_23318() <= (Double)this.elytraFly.pitch40lowerBounds.get()) {
         this.pitchingDown = false;
      } else if (!this.pitchingDown && this.mc.field_1724.method_23318() >= (Double)this.elytraFly.pitch40upperBounds.get()) {
         this.pitchingDown = true;
      }

      if (!this.pitchingDown && this.mc.field_1724.method_36455() > -40.0F) {
         this.pitch = (int)((double)this.pitch - (Double)this.elytraFly.pitch40rotationSpeed.get());
         if (this.pitch < -40) {
            this.pitch = -40;
         }
      } else if (this.pitchingDown && this.mc.field_1724.method_36455() < 40.0F) {
         this.pitch = (int)((double)this.pitch + (Double)this.elytraFly.pitch40rotationSpeed.get());
         if (this.pitch > 40) {
            this.pitch = 40;
         }
      }

      this.mc.field_1724.method_36457((float)this.pitch);
   }

   public void autoTakeoff() {
   }

   public void handleHorizontalSpeed(PlayerMoveEvent event) {
      this.velX = event.movement.field_1352;
      this.velZ = event.movement.field_1350;
   }

   public void handleVerticalSpeed(PlayerMoveEvent event) {
   }

   public void handleFallMultiplier() {
   }

   public void handleAutopilot() {
   }
}
