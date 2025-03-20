package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.managers.RotationManager;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.movement.MovementFix;
import meteordevelopment.meteorclient.utils.entity.ProjectileEntitySimulator;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1533;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2824;
import net.minecraft.class_2846;
import net.minecraft.class_2886;
import net.minecraft.class_3532;
import net.minecraft.class_3966;
import net.minecraft.class_408;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2846.class_2847;

public class PearlPhase extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Keybind> phaseBind;
   private final Setting<PearlPhase.RotateMode> rotateMode;
   private final Setting<Boolean> burrow;
   private final Setting<Boolean> antiPearlFail;
   private final Setting<Boolean> antiPearlFailStrict;
   private boolean active;
   private boolean keyUnpressed;
   private final ProjectileEntitySimulator simulator;

   public PearlPhase() {
      super(Categories.Combat, "pearl-phase", "Phases into walls using pearls");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.phaseBind = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("key-bind")).description("Phase on keybind press")).build());
      this.rotateMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("rotate-mode")).description("Which method of rotating should be used.")).defaultValue(PearlPhase.RotateMode.DelayedInstantWebOnly)).build());
      this.burrow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("borrow")).description("Places a block where you phase.")).defaultValue(true)).build());
      this.antiPearlFail = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-pearl-fail")).description("Hits entites below you when you phase.")).defaultValue(true)).build());
      this.antiPearlFailStrict = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-pearl-fail-strict")).description("Waits for the entity to disapear before phasing.")).defaultValue(false)).build());
      this.active = false;
      this.keyUnpressed = false;
      this.simulator = new ProjectileEntitySimulator();
   }

   private void activate() {
      this.active = true;
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.update();
      }
   }

   private void deactivate(boolean phased) {
      this.active = false;
      if (phased) {
         this.info("Phased", new Object[0]);
      }

   }

   private void update() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.active) {
            class_238 boundingBox = this.mc.field_1724.method_5829().method_1002(0.05D, 0.1D, 0.05D);
            double feetY = this.mc.field_1724.method_23318();
            class_238 feetBox = new class_238(boundingBox.field_1323, feetY, boundingBox.field_1321, boundingBox.field_1320, feetY + 0.1D, boundingBox.field_1324);
            if (class_2338.method_29715(feetBox).anyMatch((blockPos) -> {
               return this.mc.field_1687.method_8320(blockPos).method_26212(this.mc.field_1687, blockPos);
            })) {
               this.deactivate(false);
            }

            if (!MeteorClient.SWAP.canSwap(class_1802.field_8634)) {
               this.deactivate(false);
            } else if (this.mc.field_1724.method_7357().method_7904(class_1802.field_8634)) {
               this.deactivate(false);
            } else if (this.mc.field_1690.field_1832.method_1434() || this.mc.field_1724.method_20448()) {
               this.deactivate(false);
            }
         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.active) {
         class_243 targetPos = this.calculateTargetPos();
         float[] angle = MeteorClient.ROTATION.getRotation(targetPos);
         switch(((PearlPhase.RotateMode)this.rotateMode.get()).ordinal()) {
         case 0:
            MeteorClient.ROTATION.requestRotation(targetPos, 1000.0D);
            if (MeteorClient.ROTATION.lookingAt(class_238.method_30048(targetPos, 0.05D, 0.05D, 0.05D))) {
               this.throwPearl(angle[0], angle[1]);
            }
            break;
         case 1:
            if (this.mc.field_1724.method_24828()) {
               MeteorClient.ROTATION.snapAt(targetPos);
               this.throwPearl(angle[0], angle[1]);
            }
            break;
         case 2:
            MeteorClient.ROTATION.requestRotation(targetPos, 1000.0D);
            if (MeteorClient.ROTATION.lookingAt(class_238.method_30048(targetPos, 0.05D, 0.05D, 0.05D))) {
               MeteorClient.ROTATION.snapAt(targetPos);
               this.throwPearl(angle[0], angle[1]);
            }
            break;
         case 3:
            MeteorClient.ROTATION.requestRotation(targetPos, 1000.0D);
            if (MeteorClient.ROTATION.lookingAt(class_238.method_30048(targetPos, 0.05D, 0.05D, 0.05D))) {
               if (MovementFix.inWebs) {
                  MeteorClient.ROTATION.snapAt(targetPos);
               }

               this.throwPearl(angle[0], angle[1]);
            }
         }

      }
   }

   private void throwPearl(float yaw, float pitch) {
      if ((Boolean)this.antiPearlFail.get()) {
         class_239 hitResult = this.getEnderPearlHitResult();
         if (hitResult != null && hitResult.method_17783() == class_240.field_1331) {
            class_1297 hitEntity = ((class_3966)hitResult).method_17782();
            if (hitEntity instanceof class_1511 || hitEntity instanceof class_1533) {
               MeteorClient.ROTATION.requestRotation(hitEntity.method_19538(), 11.0D);
               if (!MeteorClient.ROTATION.lookingAt(hitEntity.method_5829()) && RotationManager.lastGround) {
                  MeteorClient.ROTATION.snapAt(hitEntity.method_19538());
               }

               if (MeteorClient.ROTATION.lookingAt(hitEntity.method_5829())) {
                  this.mc.method_1562().method_52787(class_2824.method_34206(hitEntity, this.mc.field_1724.method_5715()));
               }
            }

            if ((Boolean)this.antiPearlFailStrict.get() && hitEntity != null) {
               return;
            }
         }

         if (this.mc.field_1687.method_8320(this.mc.field_1724.method_24515()).method_27852(class_2246.field_16492)) {
            this.mc.method_1562().method_52787(new class_2846(class_2847.field_12968, this.mc.field_1724.method_24515(), class_2350.field_11036, this.mc.field_1687.method_41925().method_41937().method_41942()));
            if ((Boolean)this.antiPearlFailStrict.get()) {
               return;
            }
         }
      }

      if ((Boolean)this.burrow.get() && !this.mc.field_1724.method_6115()) {
         class_243 targetPos = this.calculateTargetPos();
         class_238 newHitbox = this.mc.field_1724.method_5829().method_989(targetPos.field_1352 - this.mc.field_1724.method_23317(), 0.0D, targetPos.field_1350 - this.mc.field_1724.method_23321()).method_1014(0.05D);
         List<class_2338> placePoses = new ArrayList();
         int minX = (int)Math.floor(newHitbox.field_1323);
         int maxX = (int)Math.floor(newHitbox.field_1320);
         int minZ = (int)Math.floor(newHitbox.field_1321);
         int maxZ = (int)Math.floor(newHitbox.field_1324);

         for(int x = minX; x <= maxX; ++x) {
            for(int z = minZ; z <= maxZ; ++z) {
               class_2338 feetPos = new class_2338(x, this.mc.field_1724.method_24515().method_10264(), z);
               placePoses.add(feetPos);
            }
         }

         if (MeteorClient.BLOCK.beginPlacement(placePoses, class_1802.field_8281)) {
            placePoses.forEach((blockPos) -> {
               MeteorClient.BLOCK.placeBlock(class_1802.field_8281, blockPos);
            });
            MeteorClient.BLOCK.endPlacement();
         }
      }

      if (MeteorClient.SWAP.beginSwap(class_1802.field_8634, true)) {
         int sequence = this.mc.field_1687.method_41925().method_41937().method_41942();
         this.mc.method_1562().method_52787(new class_2886(class_1268.field_5808, sequence, yaw, pitch));
         this.deactivate(true);
         MeteorClient.SWAP.endSwap(true);
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      if (!((Keybind)this.phaseBind.get()).isPressed()) {
         this.keyUnpressed = true;
      }

      if (((Keybind)this.phaseBind.get()).isPressed() && this.keyUnpressed && !(this.mc.field_1755 instanceof class_408)) {
         this.activate();
         this.keyUnpressed = false;
      }

      this.update();
   }

   private class_239 getEnderPearlHitResult() {
      if (!this.simulator.set(this.mc.field_1724, class_1802.field_8634.method_7854(), 0.0D, false, 1.0F)) {
         return null;
      } else {
         for(int i = 0; i < 256; ++i) {
            class_239 result = this.simulator.tick();
            if (result != null) {
               return result;
            }
         }

         return null;
      }
   }

   private class_243 calculateTargetPos() {
      double X_OFFSET = 0.241660973353061D;
      double Z_OFFSET = 0.7853981633974483D;
      double playerX = this.mc.field_1724.method_23317();
      double playerZ = this.mc.field_1724.method_23321();
      double x = playerX + class_3532.method_15350(this.toClosest(playerX, Math.floor(playerX) + 0.241660973353061D, Math.floor(playerX) + 0.7853981633974483D) - playerX, -0.2D, 0.2D);
      double z = playerZ + class_3532.method_15350(this.toClosest(playerZ, Math.floor(playerZ) + 0.241660973353061D, Math.floor(playerZ) + 0.7853981633974483D) - playerZ, -0.2D, 0.2D);
      return new class_243(x, this.mc.field_1724.method_23318() - 0.5D, z);
   }

   private double toClosest(double num, double min, double max) {
      double dmin = num - min;
      double dmax = max - num;
      return dmax > dmin ? min : max;
   }

   public static enum RotateMode {
      Movement,
      Instant,
      DelayedInstant,
      DelayedInstantWebOnly;

      // $FF: synthetic method
      private static PearlPhase.RotateMode[] $values() {
         return new PearlPhase.RotateMode[]{Movement, Instant, DelayedInstant, DelayedInstantWebOnly};
      }
   }

   public static enum SwitchMode {
      SilentHotbar,
      SilentSwap;

      // $FF: synthetic method
      private static PearlPhase.SwitchMode[] $values() {
         return new PearlPhase.SwitchMode[]{SilentHotbar, SilentSwap};
      }
   }
}
