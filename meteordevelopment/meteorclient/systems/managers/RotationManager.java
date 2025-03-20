package meteordevelopment.meteorclient.systems.managers;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.LookAtEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerJumpEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerTravelEvent;
import meteordevelopment.meteorclient.events.entity.player.RotateEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.entity.player.UpdatePlayerVelocity;
import meteordevelopment.meteorclient.events.input.KeyboardInputEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.config.AntiCheatConfig;
import meteordevelopment.meteorclient.systems.modules.movement.MovementFix;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2708;
import net.minecraft.class_2709;
import net.minecraft.class_2828;
import net.minecraft.class_3532;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2828.class_2831;

public class RotationManager {
   public float nextYaw;
   public float nextPitch;
   public float rotationYaw = 0.0F;
   public float rotationPitch = 0.0F;
   public float lastYaw = 0.0F;
   public float lastPitch = 0.0F;
   private static float renderPitch;
   private static float renderYawOffset;
   private static float prevPitch;
   private static float prevRenderYawOffset;
   private static float prevRotationYawHead;
   private static float rotationYawHead;
   public static boolean sendDisablerPacket = false;
   public static float lastActualYaw = 0.0F;
   private int ticksExisted;
   public static class_243 targetVec = null;
   public static boolean lastGround;
   public double lastX = 0.0D;
   public double lastY = 0.0D;
   public double lastZ = 0.0D;
   private boolean shouldFulfilRequest = false;
   private static final RotationManager.RotationRequest request = new RotationManager.RotationRequest();
   private final AntiCheatConfig antiCheatConfig = AntiCheatConfig.get();

   public RotationManager() {
      MeteorClient.EVENT_BUS.subscribe((Object)this);
   }

   public void snapAt(class_243 target) {
      float[] angle = this.getRotation(target);
      if ((Boolean)this.antiCheatConfig.grimSnapRotation.get()) {
         MeteorClient.mc.method_1562().method_52787(new class_2830(this.lastX, this.lastY, this.lastZ, angle[0], angle[1], lastGround));
      } else {
         MeteorClient.mc.method_1562().method_52787(new class_2831(angle[0], angle[1], lastGround));
      }

   }

   public void snapAt(float yaw, float pitch) {
      if ((Boolean)this.antiCheatConfig.grimSnapRotation.get()) {
         MeteorClient.mc.method_1562().method_52787(new class_2830(this.lastX, this.lastY, this.lastZ, yaw, pitch, lastGround));
      } else {
         MeteorClient.mc.method_1562().method_52787(new class_2831(yaw, pitch, lastGround));
      }

   }

   public void requestRotation(class_243 target, double priority) {
      float[] angle = this.getRotation(target);
      this.requestRotation(angle[0], angle[1], priority, (Runnable)null);
   }

   public void requestRotation(class_243 target, double priority, Runnable callback) {
      float[] angle = this.getRotation(target);
      this.requestRotation(angle[0], angle[1], priority, callback);
   }

   public void requestRotation(float yaw, float pitch, double priority) {
      this.requestRotation(yaw, pitch, priority, (Runnable)null);
   }

   public void requestRotation(float yaw, float pitch, double priority, Runnable callback) {
      if (!(request.priority > priority) || request.fulfilled) {
         request.fulfilled = false;
         request.yaw = yaw;
         request.pitch = pitch;
         request.priority = priority;
         request.callback = callback;
      }
   }

   public float[] getRotation(class_243 eyesPos, class_243 vec) {
      double diffX = vec.field_1352 - eyesPos.field_1352;
      double diffY = vec.field_1351 - eyesPos.field_1351;
      double diffZ = vec.field_1350 - eyesPos.field_1350;
      double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
      float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
      float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
      return new float[]{class_3532.method_15393(yaw), class_3532.method_15393(pitch)};
   }

   public float[] getRotation(class_243 vec) {
      class_243 eyesPos = MeteorClient.mc.field_1724.method_33571();
      return this.getRotation(eyesPos, vec);
   }

   public boolean lookingAt(class_238 box) {
      return this.lookingAt(this.lastYaw, this.lastPitch, box);
   }

   public boolean lookingAt(float yaw, float pitch, class_238 box) {
      return this.raytraceCheck(MeteorClient.mc.field_1724.method_33571(), (double)yaw, (double)pitch, box);
   }

   @EventHandler(
      priority = -200
   )
   public void onLastRotation(RotateEvent event) {
      LookAtEvent lookAtEvent = new LookAtEvent();
      MeteorClient.EVENT_BUS.post((Object)lookAtEvent);
      this.shouldFulfilRequest = false;
      if (request != null && !request.fulfilled && request.priority > (double)lookAtEvent.priority) {
         event.setYaw(request.yaw);
         event.setPitch(request.pitch);
         this.shouldFulfilRequest = true;
      } else {
         if (lookAtEvent.getRotation()) {
            event.setYaw(lookAtEvent.getYaw());
            event.setPitch(lookAtEvent.getPitch());
         } else if (lookAtEvent.getTarget() != null) {
            float[] newAngle = this.getRotation(lookAtEvent.getTarget());
            event.setYaw(newAngle[0]);
            event.setPitch(newAngle[1]);
         }

      }
   }

   @EventHandler(
      priority = -999
   )
   public void onPacketSend(PacketEvent.Send event) {
      if (MeteorClient.mc.field_1724 != null && !event.isCancelled()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2828) {
            class_2828 packet = (class_2828)var3;
            if (packet.method_36172()) {
               this.lastYaw = packet.method_12271(this.lastYaw);
               if (sendDisablerPacket) {
                  sendDisablerPacket = false;
                  this.lastYaw = lastActualYaw;
               }

               this.lastPitch = packet.method_12270(this.lastPitch);
               this.setRenderRotation(this.lastYaw, this.lastPitch, false);
            }

            if (packet.method_36171()) {
               this.lastX = packet.method_12269(this.lastX);
               this.lastY = packet.method_12268(this.lastY);
               this.lastZ = packet.method_12274(this.lastZ);
            }

            lastGround = packet.method_12273();
         }

      }
   }

   @EventHandler(
      priority = 100
   )
   public void onReceivePacket(PacketEvent.Receive event) {
      if (MeteorClient.mc.field_1724 != null) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2708) {
            class_2708 packet = (class_2708)var3;
            if (packet.method_11733().contains(class_2709.field_12397)) {
               this.lastYaw += packet.method_11736();
            } else {
               this.lastYaw = packet.method_11736();
            }

            if (packet.method_11733().contains(class_2709.field_12401)) {
               this.lastPitch += packet.method_11739();
            } else {
               this.lastPitch = packet.method_11739();
            }

            if (packet.method_11733().contains(class_2709.field_12400)) {
               this.lastX += packet.method_11734();
            } else {
               this.lastX = packet.method_11734();
            }

            if (packet.method_11733().contains(class_2709.field_12398)) {
               this.lastY += packet.method_11735();
            } else {
               this.lastY = packet.method_11735();
            }

            if (packet.method_11733().contains(class_2709.field_12403)) {
               this.lastZ += packet.method_11738();
            } else {
               this.lastZ = packet.method_11738();
            }

            this.setRenderRotation(this.lastYaw, this.lastPitch, true);
         }

      }
   }

   @EventHandler
   public void onUpdateWalkingPost(SendMovementPacketsEvent.Post event) {
      this.setRenderRotation(this.lastYaw, this.lastPitch, false);
   }

   @EventHandler
   public void onMovementPacket(SendMovementPacketsEvent.Rotation event) {
      if ((Boolean)this.antiCheatConfig.tickSync.get()) {
         if (this.shouldFulfilRequest && !request.fulfilled) {
            request.fulfilled = true;
            this.shouldFulfilRequest = false;
         }

         if (MovementFix.MOVE_FIX.isActive()) {
            event.yaw = this.nextYaw;
            event.pitch = this.nextPitch;
         } else {
            RotateEvent rotateEvent = new RotateEvent(event.yaw, event.pitch);
            MeteorClient.EVENT_BUS.post((Object)rotateEvent);
            event.yaw = rotateEvent.getYaw();
            event.pitch = rotateEvent.getPitch();
         }

         if ((Boolean)this.antiCheatConfig.grimSync.get()) {
            event.forceFull = true;
         }

         if ((Boolean)this.antiCheatConfig.grimRotation.get()) {
            event.forceFullOnRotate = true;
         }

      }
   }

   @EventHandler(
      priority = -200
   )
   public void onUpdatePlayerVelocity(UpdatePlayerVelocity event) {
      if (MovementFix.MOVE_FIX.isActive() && MovementFix.MOVE_FIX.updateMode.get() != MovementFix.UpdateMode.Mouse) {
         this.moveFixRotation();
      }

   }

   @EventHandler(
      priority = -200
   )
   public void onPreJump(PlayerJumpEvent.Pre event) {
      if (MovementFix.MOVE_FIX.isActive() && MovementFix.MOVE_FIX.updateMode.get() != MovementFix.UpdateMode.Mouse) {
         this.moveFixRotation();
      }

   }

   @EventHandler(
      priority = -200
   )
   public void onTravel(PlayerTravelEvent.Pre event) {
      if (MovementFix.MOVE_FIX.isActive() && MovementFix.MOVE_FIX.updateMode.get() != MovementFix.UpdateMode.Mouse) {
         this.moveFixRotation();
      }

   }

   @EventHandler(
      priority = 200
   )
   public void onKeyInput(KeyboardInputEvent event) {
      if (MovementFix.MOVE_FIX.isActive() && MovementFix.MOVE_FIX.updateMode.get() != MovementFix.UpdateMode.Mouse) {
         this.moveFixRotation();
      }

   }

   private void moveFixRotation() {
      if (MovementFix.setRot) {
         MeteorClient.mc.field_1724.method_36456(MovementFix.prevYaw);
         MeteorClient.mc.field_1724.method_36457(MovementFix.prevPitch);
      }

      RotateEvent rotateEvent = new RotateEvent(MeteorClient.mc.field_1724.method_36454(), MeteorClient.mc.field_1724.method_36455());
      MeteorClient.EVENT_BUS.post((Object)rotateEvent);
      this.nextYaw = rotateEvent.getYaw();
      this.nextPitch = rotateEvent.getPitch();
      MovementFix.fixYaw = this.nextYaw;
      MovementFix.fixPitch = this.nextPitch;
      if (MovementFix.setRot) {
         MeteorClient.mc.field_1724.method_36456(MovementFix.fixYaw);
         MeteorClient.mc.field_1724.method_36457(MovementFix.fixPitch);
      }

   }

   public boolean raytraceCheck(class_243 pos, double yaw, double pitch, class_238 box) {
      class_243 vec = new class_243(Math.cos(Math.toRadians(yaw + 90.0D)) * Math.abs(Math.cos(Math.toRadians(pitch))), -Math.sin(Math.toRadians(pitch)), Math.sin(Math.toRadians(yaw + 90.0D)) * Math.abs(Math.cos(Math.toRadians(pitch))));
      double rayX = pos.field_1352;
      double rayY = pos.field_1351;
      double rayZ = pos.field_1350;
      double dirX = vec.field_1352;
      double dirY = vec.field_1351;
      double dirZ = vec.field_1350;
      double minX = box.field_1323;
      double minY = box.field_1322;
      double minZ = box.field_1321;
      double maxX = box.field_1320;
      double maxY = box.field_1325;
      double maxZ = box.field_1324;
      double invDirX = dirX != 0.0D ? 1.0D / dirX : 1.0E10D;
      double invDirY = dirY != 0.0D ? 1.0D / dirY : 1.0E10D;
      double invDirZ = dirZ != 0.0D ? 1.0D / dirZ : 1.0E10D;
      double tMinX = (minX - rayX) * invDirX;
      double tMaxX = (maxX - rayX) * invDirX;
      double tMinY;
      if (tMinX > tMaxX) {
         tMinY = tMinX;
         tMinX = tMaxX;
         tMaxX = tMinY;
      }

      tMinY = (minY - rayY) * invDirY;
      double tMaxY = (maxY - rayY) * invDirY;
      double tMinZ;
      if (tMinY > tMaxY) {
         tMinZ = tMinY;
         tMinY = tMaxY;
         tMaxY = tMinZ;
      }

      tMinZ = (minZ - rayZ) * invDirZ;
      double tMaxZ = (maxZ - rayZ) * invDirZ;
      double tMin;
      if (tMinZ > tMaxZ) {
         tMin = tMinZ;
         tMinZ = tMaxZ;
         tMaxZ = tMin;
      }

      tMin = Math.max(Math.max(tMinX, tMinY), tMinZ);
      double tMax = Math.min(Math.min(tMaxX, tMaxY), tMaxZ);
      return tMax >= 0.0D && tMin <= tMax;
   }

   public void setRenderRotation(float yaw, float pitch, boolean force) {
      if (MeteorClient.mc.field_1724 != null && (MeteorClient.mc.field_1724.field_6012 != this.ticksExisted || force)) {
         this.ticksExisted = MeteorClient.mc.field_1724.field_6012;
         prevPitch = renderPitch;
         prevRenderYawOffset = renderYawOffset;
         renderYawOffset = this.getRenderYawOffset(yaw, prevRenderYawOffset);
         prevRotationYawHead = rotationYawHead;
         rotationYawHead = yaw;
         renderPitch = pitch;
      }
   }

   public static float getRenderPitch() {
      return renderPitch;
   }

   public static float getRotationYawHead() {
      return rotationYawHead;
   }

   public static float getRenderYawOffset() {
      return renderYawOffset;
   }

   public static float getPrevPitch() {
      return prevPitch;
   }

   public static float getPrevRotationYawHead() {
      return prevRotationYawHead;
   }

   public static float getPrevRenderYawOffset() {
      return prevRenderYawOffset;
   }

   private float getRenderYawOffset(float yaw, float offsetIn) {
      float result = offsetIn;
      double xDif = MeteorClient.mc.field_1724.method_23317() - MeteorClient.mc.field_1724.field_6014;
      double zDif = MeteorClient.mc.field_1724.method_23321() - MeteorClient.mc.field_1724.field_5969;
      float offset;
      if (xDif * xDif + zDif * zDif > 0.002500000176951289D) {
         offset = (float)class_3532.method_15349(zDif, xDif) * 57.295776F - 90.0F;
         float wrap = class_3532.method_15379(class_3532.method_15393(yaw) - offset);
         if (95.0F < wrap && wrap < 265.0F) {
            result = offset - 180.0F;
         } else {
            result = offset;
         }
      }

      if (MeteorClient.mc.field_1724.field_6251 > 0.0F) {
         result = yaw;
      }

      result = offsetIn + class_3532.method_15393(result - offsetIn) * 0.3F;
      offset = class_3532.method_15393(yaw - result);
      if (offset < -75.0F) {
         offset = -75.0F;
      } else if (offset >= 75.0F) {
         offset = 75.0F;
      }

      result = yaw - offset;
      if (offset * offset > 2500.0F) {
         result += offset * 0.2F;
      }

      return result;
   }

   public static class RotationRequest {
      public double priority;
      public float yaw;
      public float pitch;
      public boolean fulfilled = false;
      public Runnable callback = null;
   }
}
