package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.LivingEntityAccessor;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class NoJumpDelay extends Module {
   public NoJumpDelay() {
      super(Categories.Movement, "no-jump-delay", "Makes you spam jump.");
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      ((LivingEntityAccessor)this.mc.field_1724).setJumpCooldown(0);
   }
}
