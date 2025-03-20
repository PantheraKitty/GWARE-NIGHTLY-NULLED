package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.entity.DamageEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.meteor.MouseScrollEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.ChunkOcclusionEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.GUIMove;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_5498;
import net.minecraft.class_5892;
import org.joml.Vector3d;

public class Freecam extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> speed;
   private final Setting<Double> speedScrollSensitivity;
   private final Setting<Boolean> toggleOnDamage;
   private final Setting<Boolean> toggleOnDeath;
   private final Setting<Boolean> toggleOnLog;
   private final Setting<Boolean> reloadChunks;
   private final Setting<Boolean> renderHands;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> staticView;
   public final Vector3d pos;
   public final Vector3d prevPos;
   private class_5498 perspective;
   private double speedValue;
   public float yaw;
   public float pitch;
   public float prevYaw;
   public float prevPitch;
   private double fovScale;
   private boolean bobView;
   private boolean forward;
   private boolean backward;
   private boolean right;
   private boolean left;
   private boolean up;
   private boolean down;

   public Freecam() {
      super(Categories.Render, "freecam", "Allows the camera to move away from the player.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.speed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("speed")).description("Your speed while in freecam.")).onChanged((aDouble) -> {
         this.speedValue = aDouble;
      })).defaultValue(1.0D).min(0.0D).build());
      this.speedScrollSensitivity = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("speed-scroll-sensitivity")).description("Allows you to change speed value using scroll wheel. 0 to disable.")).defaultValue(0.0D).min(0.0D).sliderMax(2.0D).build());
      this.toggleOnDamage = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-damage")).description("Disables freecam when you take damage.")).defaultValue(false)).build());
      this.toggleOnDeath = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-death")).description("Disables freecam when you die.")).defaultValue(false)).build());
      this.toggleOnLog = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-log")).description("Disables freecam when you disconnect from a server.")).defaultValue(true)).build());
      this.reloadChunks = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("reload-chunks")).description("Disables cave culling.")).defaultValue(true)).build());
      this.renderHands = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-hands")).description("Whether or not to render your hands in freecam.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates to the block or entity you are looking at.")).defaultValue(false)).build());
      this.staticView = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("static")).description("Disables settings that move the view.")).defaultValue(true)).build());
      this.pos = new Vector3d();
      this.prevPos = new Vector3d();
   }

   public void onActivate() {
      this.fovScale = (Double)this.mc.field_1690.method_42454().method_41753();
      this.bobView = (Boolean)this.mc.field_1690.method_42448().method_41753();
      if ((Boolean)this.staticView.get()) {
         this.mc.field_1690.method_42454().method_41748(0.0D);
         this.mc.field_1690.method_42448().method_41748(false);
      }

      this.yaw = this.mc.field_1724.method_36454();
      this.pitch = this.mc.field_1724.method_36455();
      this.perspective = this.mc.field_1690.method_31044();
      this.speedValue = (Double)this.speed.get();
      Utils.set(this.pos, this.mc.field_1773.method_19418().method_19326());
      Utils.set(this.prevPos, this.mc.field_1773.method_19418().method_19326());
      if (this.mc.field_1690.method_31044() == class_5498.field_26666) {
         this.yaw += 180.0F;
         this.pitch *= -1.0F;
      }

      this.prevYaw = this.yaw;
      this.prevPitch = this.pitch;
      this.forward = this.mc.field_1690.field_1894.method_1434();
      this.backward = this.mc.field_1690.field_1881.method_1434();
      this.right = this.mc.field_1690.field_1849.method_1434();
      this.left = this.mc.field_1690.field_1913.method_1434();
      this.up = this.mc.field_1690.field_1903.method_1434();
      this.down = this.mc.field_1690.field_1832.method_1434();
      this.unpress();
      if ((Boolean)this.reloadChunks.get()) {
         this.mc.field_1769.method_3279();
      }

   }

   public void onDeactivate() {
      if ((Boolean)this.reloadChunks.get()) {
         this.mc.field_1769.method_3279();
      }

      this.mc.field_1690.method_31043(this.perspective);
      if ((Boolean)this.staticView.get()) {
         this.mc.field_1690.method_42454().method_41748(this.fovScale);
         this.mc.field_1690.method_42448().method_41748(this.bobView);
      }

   }

   @EventHandler
   private void onOpenScreen(OpenScreenEvent event) {
      this.unpress();
      this.prevPos.set(this.pos);
      this.prevYaw = this.yaw;
      this.prevPitch = this.pitch;
   }

   private void unpress() {
      this.mc.field_1690.field_1894.method_23481(false);
      this.mc.field_1690.field_1881.method_23481(false);
      this.mc.field_1690.field_1849.method_23481(false);
      this.mc.field_1690.field_1913.method_23481(false);
      this.mc.field_1690.field_1903.method_23481(false);
      this.mc.field_1690.field_1832.method_23481(false);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1719.method_5757()) {
         this.mc.method_1560().field_5960 = true;
      }

      if (!this.perspective.method_31034()) {
         this.mc.field_1690.method_31043(class_5498.field_26664);
      }

      class_243 forward = class_243.method_1030(0.0F, this.yaw);
      class_243 right = class_243.method_1030(0.0F, this.yaw + 90.0F);
      double velX = 0.0D;
      double velY = 0.0D;
      double velZ = 0.0D;
      if ((Boolean)this.rotate.get()) {
         class_2338 crossHairPos;
         if (this.mc.field_1765 instanceof class_3966) {
            crossHairPos = ((class_3966)this.mc.field_1765).method_17782().method_24515();
            Rotations.rotate(Rotations.getYaw(crossHairPos), Rotations.getPitch(crossHairPos), 0, (Runnable)null);
         } else {
            class_243 crossHairPosition = this.mc.field_1765.method_17784();
            crossHairPos = ((class_3965)this.mc.field_1765).method_17777();
            if (!this.mc.field_1687.method_8320(crossHairPos).method_26215()) {
               Rotations.rotate(Rotations.getYaw(crossHairPosition), Rotations.getPitch(crossHairPosition), 0, (Runnable)null);
            }
         }
      }

      double s = 0.5D;
      if (this.mc.field_1690.field_1867.method_1434()) {
         s = 1.0D;
      }

      boolean a = false;
      if (this.forward) {
         velX += forward.field_1352 * s * this.speedValue;
         velZ += forward.field_1350 * s * this.speedValue;
         a = true;
      }

      if (this.backward) {
         velX -= forward.field_1352 * s * this.speedValue;
         velZ -= forward.field_1350 * s * this.speedValue;
         a = true;
      }

      boolean b = false;
      if (this.right) {
         velX += right.field_1352 * s * this.speedValue;
         velZ += right.field_1350 * s * this.speedValue;
         b = true;
      }

      if (this.left) {
         velX -= right.field_1352 * s * this.speedValue;
         velZ -= right.field_1350 * s * this.speedValue;
         b = true;
      }

      if (a && b) {
         double diagonal = 1.0D / Math.sqrt(2.0D);
         velX *= diagonal;
         velZ *= diagonal;
      }

      if (this.up) {
         velY += s * this.speedValue;
      }

      if (this.down) {
         velY -= s * this.speedValue;
      }

      this.prevPos.set(this.pos);
      this.pos.set(this.pos.x + velX, this.pos.y + velY, this.pos.z + velZ);
   }

   @EventHandler
   public void onKey(KeyEvent event) {
      if (!Input.isKeyPressed(292)) {
         if (!this.checkGuiMove()) {
            boolean cancel = true;
            if (this.mc.field_1690.field_1894.method_1417(event.key, 0)) {
               this.forward = event.action != KeyAction.Release;
               this.mc.field_1690.field_1894.method_23481(false);
            } else if (this.mc.field_1690.field_1881.method_1417(event.key, 0)) {
               this.backward = event.action != KeyAction.Release;
               this.mc.field_1690.field_1881.method_23481(false);
            } else if (this.mc.field_1690.field_1849.method_1417(event.key, 0)) {
               this.right = event.action != KeyAction.Release;
               this.mc.field_1690.field_1849.method_23481(false);
            } else if (this.mc.field_1690.field_1913.method_1417(event.key, 0)) {
               this.left = event.action != KeyAction.Release;
               this.mc.field_1690.field_1913.method_23481(false);
            } else if (this.mc.field_1690.field_1903.method_1417(event.key, 0)) {
               this.up = event.action != KeyAction.Release;
               this.mc.field_1690.field_1903.method_23481(false);
            } else if (this.mc.field_1690.field_1832.method_1417(event.key, 0)) {
               this.down = event.action != KeyAction.Release;
               this.mc.field_1690.field_1832.method_23481(false);
            } else {
               cancel = false;
            }

            if (cancel) {
               event.cancel();
            }

         }
      }
   }

   @EventHandler
   private void onMouseButton(MouseButtonEvent event) {
      if (!this.checkGuiMove()) {
         boolean cancel = true;
         if (this.mc.field_1690.field_1894.method_1433(event.button)) {
            this.forward = event.action != KeyAction.Release;
            this.mc.field_1690.field_1894.method_23481(false);
         } else if (this.mc.field_1690.field_1881.method_1433(event.button)) {
            this.backward = event.action != KeyAction.Release;
            this.mc.field_1690.field_1881.method_23481(false);
         } else if (this.mc.field_1690.field_1849.method_1433(event.button)) {
            this.right = event.action != KeyAction.Release;
            this.mc.field_1690.field_1849.method_23481(false);
         } else if (this.mc.field_1690.field_1913.method_1433(event.button)) {
            this.left = event.action != KeyAction.Release;
            this.mc.field_1690.field_1913.method_23481(false);
         } else if (this.mc.field_1690.field_1903.method_1433(event.button)) {
            this.up = event.action != KeyAction.Release;
            this.mc.field_1690.field_1903.method_23481(false);
         } else if (this.mc.field_1690.field_1832.method_1433(event.button)) {
            this.down = event.action != KeyAction.Release;
            this.mc.field_1690.field_1832.method_23481(false);
         } else {
            cancel = false;
         }

         if (cancel) {
            event.cancel();
         }

      }
   }

   @EventHandler(
      priority = -100
   )
   private void onMouseScroll(MouseScrollEvent event) {
      if ((Double)this.speedScrollSensitivity.get() > 0.0D && this.mc.field_1755 == null) {
         this.speedValue += event.value * 0.25D * (Double)this.speedScrollSensitivity.get() * this.speedValue;
         if (this.speedValue < 0.1D) {
            this.speedValue = 0.1D;
         }

         event.cancel();
      }

   }

   @EventHandler
   private void onChunkOcclusion(ChunkOcclusionEvent event) {
      event.cancel();
   }

   @EventHandler
   private void onDamage(DamageEvent event) {
      if (event.entity.method_5667() != null) {
         if (event.entity.method_5667().equals(this.mc.field_1724.method_5667())) {
            if ((Boolean)this.toggleOnDamage.get()) {
               this.toggle();
               this.info("Toggled off because you took damage.", new Object[0]);
            }

         }
      }
   }

   @EventHandler
   private void onGameLeft(GameLeftEvent event) {
      if ((Boolean)this.toggleOnLog.get()) {
         this.toggle();
      }
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_5892) {
         class_5892 packet = (class_5892)var3;
         class_1297 entity = this.mc.field_1687.method_8469(packet.comp_2275());
         if (entity == this.mc.field_1724 && (Boolean)this.toggleOnDeath.get()) {
            this.toggle();
            this.info("Toggled off because you died.", new Object[0]);
         }
      }

   }

   private boolean checkGuiMove() {
      GUIMove guiMove = (GUIMove)Modules.get().get(GUIMove.class);
      if (this.mc.field_1755 != null && !guiMove.isActive()) {
         return true;
      } else {
         return this.mc.field_1755 != null && guiMove.isActive() && guiMove.skip();
      }
   }

   public void changeLookDirection(double deltaX, double deltaY) {
      this.prevYaw = this.yaw;
      this.prevPitch = this.pitch;
      this.yaw = (float)((double)this.yaw + deltaX);
      this.pitch = (float)((double)this.pitch + deltaY);
      this.pitch = class_3532.method_15363(this.pitch, -90.0F, 90.0F);
   }

   public boolean renderHands() {
      return !this.isActive() || (Boolean)this.renderHands.get();
   }

   public double getX(float tickDelta) {
      return class_3532.method_16436((double)tickDelta, this.prevPos.x, this.pos.x);
   }

   public double getY(float tickDelta) {
      return class_3532.method_16436((double)tickDelta, this.prevPos.y, this.pos.y);
   }

   public double getZ(float tickDelta) {
      return class_3532.method_16436((double)tickDelta, this.prevPos.z, this.pos.z);
   }

   public double getYaw(float tickDelta) {
      return (double)class_3532.method_16439(tickDelta, this.prevYaw, this.yaw);
   }

   public double getPitch(float tickDelta) {
      return (double)class_3532.method_16439(tickDelta, this.prevPitch, this.pitch);
   }
}
