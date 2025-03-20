package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.Iterator;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1671;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2708;
import net.minecraft.class_3532;

public class ElytraSpeed extends Module {
   private final SettingGroup sgGeneral;
   private boolean using;
   private double yaw;
   private double pitch;
   private class_243 lastMovement;
   private boolean rubberband;
   private final Setting<Double> startSpeed;
   private final Setting<Double> accel;
   private final Setting<Double> maxSpeed;

   public ElytraSpeed() {
      super(Categories.Movement, "elytra-speed", "Makes your elytra faster when you use a firework.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.startSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("start-speed")).description("Initial speed when you use a firework")).defaultValue(30.0D).min(0.0D).sliderMax(100.0D).build());
      this.accel = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("accel-speed")).description("Acceleration")).defaultValue(3.0D).min(0.0D).sliderMax(5.0D).build());
      this.maxSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-speed")).description("Maximum speed you can go while flying")).defaultValue(100.0D).min(0.0D).sliderMax(250.0D).build());
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.using = false;
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      while(var2.hasNext()) {
         class_1297 entity = (class_1297)var2.next();
         if (entity instanceof class_1671) {
            class_1671 firework = (class_1671)entity;
            if (firework.method_24921() != null && firework.method_24921().equals(this.mc.field_1724)) {
               this.using = true;
            }
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (!this.rubberband) {
         this.yaw = Math.toRadians((double)this.mc.field_1724.method_36454());
         this.pitch = Math.toRadians((double)this.mc.field_1724.method_36455());
      }
   }

   public void onDeactivate() {
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (this.isActive()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2708) {
            class_2708 lookS2CPacket = (class_2708)var3;
            this.rubberband = true;
            this.lastMovement = new class_243(0.0D, 0.0D, 0.0D);
            this.yaw = Math.toRadians((double)lookS2CPacket.method_11736());
            this.pitch = Math.toRadians((double)lookS2CPacket.method_11739());
         }

      }
   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent event) {
      ElytraFly eFly = (ElytraFly)Modules.get().get(ElytraFly.class);
      ElytraFakeFly geFly = (ElytraFakeFly)Modules.get().get(ElytraFakeFly.class);
      if (this.rubberband || this.using && this.mc.field_1724.method_6128() && !eFly.isActive() && !geFly.isActive()) {
         if (this.isActive()) {
            if (this.lastMovement == null) {
               this.lastMovement = event.movement;
            }

            class_243 direction = (new class_243(-Math.sin(this.yaw) * Math.cos(this.pitch), -Math.sin(this.pitch), Math.cos(this.yaw) * Math.cos(this.pitch))).method_1029();
            class_243 currentMovement = direction.method_1021(this.lastMovement.method_1033());
            class_243 newMovement;
            if (this.rubberband) {
               newMovement = currentMovement;
            } else {
               if (this.lastMovement.method_1033() < (Double)this.startSpeed.get() / 20.0D) {
                  newMovement = direction.method_1021((Double)this.startSpeed.get() / 20.0D);
               } else {
                  newMovement = currentMovement.method_1019(direction.method_1021((Double)this.accel.get() / 20.0D));
               }

               if (newMovement.method_1033() > (Double)this.maxSpeed.get() / 20.0D) {
                  newMovement = newMovement.method_1029().method_1021((Double)this.maxSpeed.get() / 20.0D);
               }
            }

            double speed = this.lastMovement.method_1033();
            double speedFactor = Math.max(0.1D, Math.min(1.0D, ((Double)this.maxSpeed.get() * 2.5D / 20.0D - speed) / ((Double)this.maxSpeed.get() * 2.5D / 20.0D)));
            class_243 lastDirection = this.lastMovement.method_1029();
            class_243 newDirection = newMovement.method_1029();
            double dot = lastDirection.method_1026(newDirection);
            dot = class_3532.method_15350(dot, -1.0D, 1.0D);
            double theta = Math.acos(dot) * speedFactor;
            class_243 slerpedDirection;
            if (Math.abs(theta) < 0.001D) {
               slerpedDirection = newDirection;
            } else {
               class_243 relativeDirection = newDirection.method_1020(lastDirection.method_1021(dot)).method_1029();
               slerpedDirection = lastDirection.method_1021(Math.cos(theta)).method_1019(relativeDirection.method_1021(Math.sin(theta)));
            }

            if (this.lastMovement.method_1033() < (Double)this.startSpeed.get()) {
               newMovement = slerpedDirection.method_1021(newMovement.method_1033());
            }

            if (newMovement.method_1033() > (Double)this.maxSpeed.get() / 20.0D) {
               newMovement = newMovement.method_1029().method_1021((Double)this.maxSpeed.get() / 20.0D);
            }

            this.mc.field_1724.method_18799(newMovement);
            ((IVec3d)event.movement).set(newMovement.field_1352, newMovement.field_1351, newMovement.field_1350);
            this.lastMovement = newMovement;
            if (this.rubberband) {
               this.rubberband = false;
            }

         }
      } else {
         this.lastMovement = event.movement;
      }
   }
}
