package meteordevelopment.meteorclient.systems.modules.movement.elytrafly;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_2848;
import net.minecraft.class_310;
import net.minecraft.class_2848.class_2849;

public class ElytraFlightMode {
   protected final class_310 mc = class_310.method_1551();
   protected final ElytraFly elytraFly = (ElytraFly)Modules.get().get(ElytraFly.class);
   private final ElytraFlightModes type;
   protected boolean lastJumpPressed;
   protected boolean incrementJumpTimer;
   protected boolean lastForwardPressed;
   protected int jumpTimer;
   protected double velX;
   protected double velY;
   protected double velZ;
   protected double ticksLeft;
   protected class_243 forward;
   protected class_243 right;
   protected double acceleration;

   public ElytraFlightMode(ElytraFlightModes type) {
      this.type = type;
   }

   public void onTick() {
      if ((Boolean)this.elytraFly.autoReplenish.get()) {
         FindItemResult fireworks = InvUtils.find(class_1802.field_8639);
         if (fireworks.found() && !fireworks.isHotbar()) {
            InvUtils.move().from(fireworks.slot()).toHotbar((Integer)this.elytraFly.replenishSlot.get() - 1);
         }
      }

      if ((Boolean)this.elytraFly.replace.get()) {
         class_1799 chestStack = this.mc.field_1724.method_31548().method_7372(2);
         if (chestStack.method_7909() == class_1802.field_8833 && chestStack.method_7936() - chestStack.method_7919() <= (Integer)this.elytraFly.replaceDurability.get()) {
            FindItemResult elytra = InvUtils.find((stack) -> {
               return stack.method_7936() - stack.method_7919() > (Integer)this.elytraFly.replaceDurability.get() && stack.method_7909() == class_1802.field_8833;
            });
            InvUtils.move().from(elytra.slot()).toArmor(2);
         }
      }

   }

   public void onPreTick() {
   }

   public void onPacketSend(PacketEvent.Send event) {
   }

   public void onPacketReceive(PacketEvent.Receive event) {
   }

   public void onPlayerMove() {
   }

   public void onActivate() {
      this.lastJumpPressed = false;
      this.jumpTimer = 0;
      this.ticksLeft = 0.0D;
      this.acceleration = 0.0D;
   }

   public void onDeactivate() {
   }

   public void autoTakeoff() {
      if (this.incrementJumpTimer) {
         ++this.jumpTimer;
      }

      boolean jumpPressed = this.mc.field_1690.field_1903.method_1434();
      if ((Boolean)this.elytraFly.autoTakeOff.get() && jumpPressed) {
         if (!this.lastJumpPressed && !this.mc.field_1724.method_6128()) {
            this.jumpTimer = 0;
            this.incrementJumpTimer = true;
         }

         if (this.jumpTimer >= 8) {
            this.jumpTimer = 0;
            this.incrementJumpTimer = false;
            this.mc.field_1724.method_6100(false);
            this.mc.field_1724.method_5728(true);
            this.mc.field_1724.method_6043();
            this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
         }
      }

      this.lastJumpPressed = jumpPressed;
   }

   public void handleAutopilot() {
      if (this.mc.field_1724.method_6128()) {
         if ((Boolean)this.elytraFly.autoPilot.get() && this.mc.field_1724.method_23318() > (Double)this.elytraFly.autoPilotMinimumHeight.get() && this.elytraFly.flightMode.get() != ElytraFlightModes.Bounce) {
            this.mc.field_1690.field_1894.method_23481(true);
            this.lastForwardPressed = true;
         }

         if ((Boolean)this.elytraFly.useFireworks.get()) {
            if (this.ticksLeft <= 0.0D) {
               this.ticksLeft = (Double)this.elytraFly.autoPilotFireworkDelay.get() * 20.0D;
               FindItemResult itemResult = InvUtils.findInHotbar(class_1802.field_8639);
               if (!itemResult.found()) {
                  return;
               }

               if (itemResult.isOffhand()) {
                  this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5810);
                  this.mc.field_1724.method_6104(class_1268.field_5810);
               } else {
                  InvUtils.swap(itemResult.slot(), true);
                  this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
                  this.mc.field_1724.method_6104(class_1268.field_5808);
                  InvUtils.swapBack();
               }
            }

            --this.ticksLeft;
         }

      }
   }

   public void handleHorizontalSpeed(PlayerMoveEvent event) {
      boolean a = false;
      boolean b = false;
      if (this.mc.field_1690.field_1894.method_1434()) {
         this.velX += this.forward.field_1352 * this.getSpeed() * 10.0D;
         this.velZ += this.forward.field_1350 * this.getSpeed() * 10.0D;
         a = true;
      } else if (this.mc.field_1690.field_1881.method_1434()) {
         this.velX -= this.forward.field_1352 * this.getSpeed() * 10.0D;
         this.velZ -= this.forward.field_1350 * this.getSpeed() * 10.0D;
         a = true;
      }

      if (this.mc.field_1690.field_1849.method_1434()) {
         this.velX += this.right.field_1352 * this.getSpeed() * 10.0D;
         this.velZ += this.right.field_1350 * this.getSpeed() * 10.0D;
         b = true;
      } else if (this.mc.field_1690.field_1913.method_1434()) {
         this.velX -= this.right.field_1352 * this.getSpeed() * 10.0D;
         this.velZ -= this.right.field_1350 * this.getSpeed() * 10.0D;
         b = true;
      }

      if (a && b) {
         double diagonal = 1.0D / Math.sqrt(2.0D);
         this.velX *= diagonal;
         this.velZ *= diagonal;
      }

   }

   public void handleVerticalSpeed(PlayerMoveEvent event) {
      if (this.mc.field_1690.field_1903.method_1434()) {
         this.velY += 0.5D * (Double)this.elytraFly.verticalSpeed.get();
      } else if (this.mc.field_1690.field_1832.method_1434()) {
         this.velY -= 0.5D * (Double)this.elytraFly.verticalSpeed.get();
      }

   }

   public void handleFallMultiplier() {
      if (this.velY < 0.0D) {
         this.velY *= (Double)this.elytraFly.fallMultiplier.get();
      } else if (this.velY > 0.0D) {
         this.velY = 0.0D;
      }

   }

   public void handleAcceleration() {
      if ((Boolean)this.elytraFly.acceleration.get()) {
         if (!PlayerUtils.isMoving()) {
            this.acceleration = 0.0D;
         }

         this.acceleration = Math.min(this.acceleration + (Double)this.elytraFly.accelerationMin.get() + (Double)this.elytraFly.accelerationStep.get() * 0.1D, (Double)this.elytraFly.horizontalSpeed.get());
      } else {
         this.acceleration = 0.0D;
      }

   }

   public void zeroAcceleration() {
      this.acceleration = 0.0D;
   }

   protected double getSpeed() {
      return (Boolean)this.elytraFly.acceleration.get() ? this.acceleration : (Double)this.elytraFly.horizontalSpeed.get();
   }

   public String getHudString() {
      return this.type.name();
   }
}
