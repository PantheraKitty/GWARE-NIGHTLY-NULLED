package meteordevelopment.meteorclient.systems.modules.movement.speed.modes;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.systems.modules.movement.speed.SpeedMode;
import meteordevelopment.meteorclient.systems.modules.movement.speed.SpeedModes;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import org.joml.Vector2d;

public class Strafe extends SpeedMode {
   private long timer = 0L;

   public Strafe() {
      super(SpeedModes.Strafe);
   }

   public void onMove(PlayerMoveEvent event) {
      switch(this.stage) {
      case 0:
         if (PlayerUtils.isMoving()) {
            ++this.stage;
            this.speed = 1.1799999475479126D * this.getDefaultSpeed() - 0.01D;
         }
      case 1:
         if (PlayerUtils.isMoving() && this.mc.field_1724.method_24828()) {
            ((IVec3d)event.movement).setY(this.getHop(0.40123128D));
            this.speed *= (Double)this.settings.ncpSpeed.get();
            ++this.stage;
         }
         break;
      case 2:
         this.speed = this.distance - 0.76D * (this.distance - this.getDefaultSpeed());
         ++this.stage;
         break;
      case 3:
         if (!this.mc.field_1687.method_18026(this.mc.field_1724.method_5829().method_989(0.0D, this.mc.field_1724.method_18798().field_1351, 0.0D)) || this.mc.field_1724.field_5992 && this.stage > 0) {
            this.stage = 0;
         }

         this.speed = this.distance - this.distance / 159.0D;
      }

      this.speed = Math.max(this.speed, this.getDefaultSpeed());
      if ((Boolean)this.settings.ncpSpeedLimit.get()) {
         if (System.currentTimeMillis() - this.timer > 2500L) {
            this.timer = System.currentTimeMillis();
         }

         this.speed = Math.min(this.speed, System.currentTimeMillis() - this.timer > 1250L ? 0.44D : 0.43D);
      }

      Vector2d change = this.transformStrafe(this.speed);
      double velX = change.x;
      double velZ = change.y;
      Anchor anchor = (Anchor)Modules.get().get(Anchor.class);
      if (anchor.isActive() && anchor.controlMovement) {
         velX = anchor.deltaX;
         velZ = anchor.deltaZ;
      }

      ((IVec3d)event.movement).setXZ(velX, velZ);
   }

   private Vector2d transformStrafe(double speed) {
      float forward = this.mc.field_1724.field_3913.field_3905;
      float side = this.mc.field_1724.field_3913.field_3907;
      float yaw = this.mc.field_1724.field_5982 + (this.mc.field_1724.method_36454() - this.mc.field_1724.field_5982) * this.mc.method_60646().method_60637(true);
      if (forward == 0.0F && side == 0.0F) {
         return new Vector2d(0.0D, 0.0D);
      } else {
         if (forward != 0.0F) {
            if (side >= 1.0F) {
               yaw += (float)(forward > 0.0F ? -45 : 45);
               side = 0.0F;
            } else if (side <= -1.0F) {
               yaw += (float)(forward > 0.0F ? 45 : -45);
               side = 0.0F;
            }

            if (forward > 0.0F) {
               forward = 1.0F;
            } else if (forward < 0.0F) {
               forward = -1.0F;
            }
         }

         double mx = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
         double mz = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
         double velX = (double)forward * speed * mx + (double)side * speed * mz;
         double velZ = (double)forward * speed * mz - (double)side * speed * mx;
         return new Vector2d(velX, velZ);
      }
   }

   public void onTick() {
      this.distance = Math.sqrt((this.mc.field_1724.method_23317() - this.mc.field_1724.field_6014) * (this.mc.field_1724.method_23317() - this.mc.field_1724.field_6014) + (this.mc.field_1724.method_23321() - this.mc.field_1724.field_5969) * (this.mc.field_1724.method_23321() - this.mc.field_1724.field_5969));
   }
}
