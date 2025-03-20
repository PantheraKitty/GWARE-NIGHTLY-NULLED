package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1294;
import net.minecraft.class_2708;
import net.minecraft.class_746;

public class LongJump extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<LongJump.JumpMode> jumpMode;
   private final Setting<Double> vanillaBoostFactor;
   private final Setting<Double> burstInitialSpeed;
   private final Setting<Double> burstBoostFactor;
   private final Setting<Boolean> onlyOnGround;
   private final Setting<Boolean> onJump;
   private final Setting<Double> glideMultiplier;
   public final Setting<Double> timer;
   private final Setting<Boolean> autoDisable;
   private final Setting<Boolean> disableOnRubberband;
   private int stage;
   private double moveSpeed;
   private boolean jumping;
   private int airTicks;
   private int groundTicks;
   private boolean jumped;

   public LongJump() {
      super(Categories.Movement, "long-jump", "Allows you to jump further than normal.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.jumpMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The method of jumping.")).defaultValue(LongJump.JumpMode.Vanilla)).build());
      this.vanillaBoostFactor = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vanilla-boost-factor")).description("The amount by which to boost the jump.")).visible(() -> {
         return this.jumpMode.get() == LongJump.JumpMode.Vanilla;
      })).defaultValue(1.261D).min(0.0D).sliderMax(5.0D).build());
      this.burstInitialSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("burst-initial-speed")).description("The initial speed of the runup.")).visible(() -> {
         return this.jumpMode.get() == LongJump.JumpMode.Burst;
      })).defaultValue(6.0D).min(0.0D).sliderMax(20.0D).build());
      this.burstBoostFactor = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("burst-boost-factor")).description("The amount by which to boost the jump.")).visible(() -> {
         return this.jumpMode.get() == LongJump.JumpMode.Burst;
      })).defaultValue(2.149D).min(0.0D).sliderMax(20.0D).build());
      this.onlyOnGround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Only performs the jump if you are on the ground.")).visible(() -> {
         return this.jumpMode.get() == LongJump.JumpMode.Burst;
      })).defaultValue(true)).build());
      this.onJump = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("on-jump")).description("Whether the player needs to jump first or not.")).visible(() -> {
         return this.jumpMode.get() == LongJump.JumpMode.Burst;
      })).defaultValue(false)).build());
      this.glideMultiplier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("glide-multiplier")).description("The amount by to multiply the glide velocity.")).visible(() -> {
         return this.jumpMode.get() == LongJump.JumpMode.Glide;
      })).defaultValue(1.0D).min(0.0D).sliderMax(5.0D).build());
      this.timer = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("timer")).description("Timer override.")).defaultValue(1.0D).min(0.01D).sliderMin(0.01D).build());
      this.autoDisable = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-disable")).description("Automatically disabled the module after jumping.")).visible(() -> {
         return this.jumpMode.get() != LongJump.JumpMode.Vanilla;
      })).defaultValue(true)).build());
      this.disableOnRubberband = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-rubberband")).description("Disables the module when you get lagged back.")).defaultValue(true)).build());
      this.jumping = false;
      this.jumped = false;
   }

   public void onActivate() {
      this.stage = 0;
      this.jumping = false;
      this.airTicks = 0;
      this.groundTicks = -5;
   }

   public void onDeactivate() {
      ((Timer)Modules.get().get(Timer.class)).setOverride(1.0D);
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (event.packet instanceof class_2708 && (Boolean)this.disableOnRubberband.get()) {
         this.info("Rubberband detected! Disabling...", new Object[0]);
         this.toggle();
      }

   }

   @EventHandler
   private void onPlayerMove(PlayerMoveEvent event) {
      if ((Double)this.timer.get() != 1.0D) {
         ((Timer)Modules.get().get(Timer.class)).setOverride(PlayerUtils.isMoving() ? (Double)this.timer.get() : 1.0D);
      }

      double xDist;
      double zDist;
      double lastDist;
      switch(((LongJump.JumpMode)this.jumpMode.get()).ordinal()) {
      case 0:
         if (PlayerUtils.isMoving() && this.mc.field_1690.field_1903.method_1434()) {
            xDist = this.getDir();
            zDist = Math.cos(Math.toRadians(xDist + 90.0D));
            lastDist = Math.sin(Math.toRadians(xDist + 90.0D));
            if (!this.mc.field_1687.method_18026(this.mc.field_1724.method_5829().method_989(0.0D, this.mc.field_1724.method_18798().field_1351, 0.0D)) || this.mc.field_1724.field_5992) {
               ((IVec3d)event.movement).setXZ(zDist * 0.28999999165534973D, lastDist * 0.28999999165534973D);
            }

            if (event.movement.method_10214() == 0.33319999363422365D) {
               ((IVec3d)event.movement).setXZ(zDist * (Double)this.vanillaBoostFactor.get(), lastDist * (Double)this.vanillaBoostFactor.get());
            }
         }
         break;
      case 1:
         if (this.stage != 0 && !this.mc.field_1724.method_24828() && (Boolean)this.autoDisable.get()) {
            this.jumping = true;
         }

         if (this.jumping && this.mc.field_1724.method_23318() - (double)((int)this.mc.field_1724.method_23318()) < 0.01D) {
            this.jumping = false;
            this.toggle();
            this.info("Disabling after jump.", new Object[0]);
         }

         if ((Boolean)this.onlyOnGround.get() && !this.mc.field_1724.method_24828() && this.stage == 0) {
            return;
         }

         xDist = this.mc.field_1724.method_23317() - this.mc.field_1724.field_6014;
         zDist = this.mc.field_1724.method_23321() - this.mc.field_1724.field_5969;
         lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
         if (PlayerUtils.isMoving() && (!(Boolean)this.onJump.get() || this.mc.field_1690.field_1903.method_1434()) && !this.mc.field_1724.method_5771() && !this.mc.field_1724.method_5799()) {
            if (this.stage == 0) {
               this.moveSpeed = this.getMoveSpeed() * (Double)this.burstInitialSpeed.get();
            } else if (this.stage == 1) {
               ((IVec3d)event.movement).setY(0.42D);
               this.moveSpeed *= (Double)this.burstBoostFactor.get();
            } else if (this.stage == 2) {
               double difference = lastDist - this.getMoveSpeed();
               this.moveSpeed = lastDist - difference;
            } else {
               this.moveSpeed = lastDist - lastDist / 159.0D;
            }

            this.setMoveSpeed(event, this.moveSpeed = Math.max(this.getMoveSpeed(), this.moveSpeed));
            if (!this.mc.field_1724.field_5992 && !this.mc.field_1687.method_18026(this.mc.field_1724.method_5829().method_989(0.0D, this.mc.field_1724.method_18798().field_1351, 0.0D)) && !this.mc.field_1687.method_18026(this.mc.field_1724.method_5829().method_989(0.0D, -0.4D, 0.0D))) {
               ((IVec3d)event.movement).setY(-0.001D);
            }

            ++this.stage;
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (Utils.canUpdate() && this.jumpMode.get() == LongJump.JumpMode.Glide) {
         if (!PlayerUtils.isMoving()) {
            return;
         }

         float yaw = this.mc.field_1724.method_36454() + 90.0F;
         double forward = (double)(this.mc.field_1724.field_6250 != 0.0F ? (this.mc.field_1724.field_6250 > 0.0F ? 1 : -1) : 0);
         float[] motion = new float[]{0.4206065F, 0.4179245F, 0.41525924F, 0.41261F, 0.409978F, 0.407361F, 0.404761F, 0.402178F, 0.399611F, 0.39706F, 0.394525F, 0.392F, 0.3894F, 0.38644F, 0.383655F, 0.381105F, 0.37867F, 0.37625F, 0.37384F, 0.37145F, 0.369F, 0.3666F, 0.3642F, 0.3618F, 0.35945F, 0.357F, 0.354F, 0.351F, 0.348F, 0.345F, 0.342F, 0.339F, 0.336F, 0.333F, 0.33F, 0.327F, 0.324F, 0.321F, 0.318F, 0.315F, 0.312F, 0.309F, 0.307F, 0.305F, 0.303F, 0.3F, 0.297F, 0.295F, 0.293F, 0.291F, 0.289F, 0.287F, 0.285F, 0.283F, 0.281F, 0.279F, 0.277F, 0.275F, 0.273F, 0.271F, 0.269F, 0.267F, 0.265F, 0.263F, 0.261F, 0.259F, 0.257F, 0.255F, 0.253F, 0.251F, 0.249F, 0.247F, 0.245F, 0.243F, 0.241F, 0.239F, 0.237F};
         float[] glide = new float[]{0.3425F, 0.5445F, 0.65425F, 0.685F, 0.675F, 0.2F, 0.895F, 0.719F, 0.76F};
         double cos = Math.cos(Math.toRadians((double)yaw));
         double sin = Math.sin(Math.toRadians((double)yaw));
         if (!this.mc.field_1724.field_5992 && !this.mc.field_1724.method_24828()) {
            this.jumped = true;
            ++this.airTicks;
            this.groundTicks = -5;
            double velocityY = this.mc.field_1724.method_18798().field_1351;
            if (this.airTicks - 6 >= 0 && this.airTicks - 6 < glide.length) {
               this.updateY(velocityY * (double)glide[this.airTicks - 6] * (Double)this.glideMultiplier.get());
            }

            if (velocityY < -0.2D && velocityY > -0.24D) {
               this.updateY(velocityY * 0.7D * (Double)this.glideMultiplier.get());
            } else if (velocityY < -0.25D && velocityY > -0.32D) {
               this.updateY(velocityY * 0.8D * (Double)this.glideMultiplier.get());
            } else if (velocityY < -0.35D && velocityY > -0.8D) {
               this.updateY(velocityY * 0.98D * (Double)this.glideMultiplier.get());
            }

            if (this.airTicks - 1 >= 0 && this.airTicks - 1 < motion.length) {
               class_746 var10000 = this.mc.field_1724;
               double var10001 = forward * (double)motion[this.airTicks - 1] * 3.0D * cos * (Double)this.glideMultiplier.get();
               double var10003 = forward * (double)motion[this.airTicks - 1];
               var10000.method_18800(var10001, this.mc.field_1724.method_18798().field_1351, var10003 * 3.0D * sin * (Double)this.glideMultiplier.get());
            } else {
               this.mc.field_1724.method_18800(0.0D, this.mc.field_1724.method_18798().field_1351, 0.0D);
            }
         } else {
            if ((Boolean)this.autoDisable.get() && this.jumped) {
               this.jumped = false;
               this.toggle();
               this.info("Disabling after jump.", new Object[0]);
            }

            this.airTicks = 0;
            ++this.groundTicks;
            if (this.groundTicks <= 2) {
               this.mc.field_1724.method_18800(forward * 0.009999999776482582D * cos * (Double)this.glideMultiplier.get(), this.mc.field_1724.method_18798().field_1351, forward * 0.009999999776482582D * sin * (Double)this.glideMultiplier.get());
            } else {
               this.mc.field_1724.method_18800(forward * 0.30000001192092896D * cos * (Double)this.glideMultiplier.get(), 0.42399999499320984D, forward * 0.30000001192092896D * sin * (Double)this.glideMultiplier.get());
            }
         }
      }

   }

   private void updateY(double amount) {
      this.mc.field_1724.method_18800(this.mc.field_1724.method_18798().field_1352, amount, this.mc.field_1724.method_18798().field_1350);
   }

   private double getDir() {
      double dir = 0.0D;
      if (Utils.canUpdate()) {
         dir = (double)(this.mc.field_1724.method_36454() + (float)(this.mc.field_1724.field_6250 < 0.0F ? 180 : 0));
         if (this.mc.field_1724.field_6212 > 0.0F) {
            dir += (double)(-90.0F * (this.mc.field_1724.field_6250 < 0.0F ? -0.5F : (this.mc.field_1724.field_6250 > 0.0F ? 0.5F : 1.0F)));
         } else if (this.mc.field_1724.field_6212 < 0.0F) {
            dir += (double)(90.0F * (this.mc.field_1724.field_6250 < 0.0F ? -0.5F : (this.mc.field_1724.field_6250 > 0.0F ? 0.5F : 1.0F)));
         }
      }

      return dir;
   }

   private double getMoveSpeed() {
      double base = 0.2873D;
      if (this.mc.field_1724.method_6059(class_1294.field_5904)) {
         base *= 1.0D + 0.2D * (double)(this.mc.field_1724.method_6112(class_1294.field_5904).method_5578() + 1);
      }

      return base;
   }

   private void setMoveSpeed(PlayerMoveEvent event, double speed) {
      double forward = (double)this.mc.field_1724.field_6250;
      double strafe = (double)this.mc.field_1724.field_6212;
      float yaw = this.mc.field_1724.method_36454();
      if (!PlayerUtils.isMoving()) {
         ((IVec3d)event.movement).setXZ(0.0D, 0.0D);
      } else {
         if (forward != 0.0D) {
            if (strafe > 0.0D) {
               yaw += (float)(forward > 0.0D ? -45 : 45);
            } else if (strafe < 0.0D) {
               yaw += (float)(forward > 0.0D ? 45 : -45);
            }
         }

         strafe = 0.0D;
         if (forward > 0.0D) {
            forward = 1.0D;
         } else if (forward < 0.0D) {
            forward = -1.0D;
         }
      }

      double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
      ((IVec3d)event.movement).setXZ(forward * speed * cos + strafe * speed * sin, forward * speed * sin + strafe * speed * cos);
   }

   public static enum JumpMode {
      Vanilla,
      Burst,
      Glide;

      // $FF: synthetic method
      private static LongJump.JumpMode[] $values() {
         return new LongJump.JumpMode[]{Vanilla, Burst, Glide};
      }
   }
}
