package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Vector3dSetting;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1304;
import net.minecraft.class_1657;
import net.minecraft.class_1770;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2661;
import net.minecraft.class_2848;
import net.minecraft.class_2848.class_2849;
import org.joml.Vector3d;

public class AutoWasp extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> horizontalSpeed;
   private final Setting<Double> verticalSpeed;
   private final Setting<Boolean> avoidLanding;
   private final Setting<Boolean> predictMovement;
   private final Setting<Boolean> onlyFriends;
   private final Setting<AutoWasp.Action> action;
   private final Setting<Vector3d> offset;
   public class_1657 target;
   private int jumpTimer;
   private boolean incrementJumpTimer;

   public AutoWasp() {
      super(Categories.Movement, "auto-wasp", "Wasps for you. Unable to traverse around blocks, assumes a clear straight line to the target.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.horizontalSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("horizontal-speed")).description("Horizontal elytra speed.")).defaultValue(2.0D).build());
      this.verticalSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vertical-speed")).description("Vertical elytra speed.")).defaultValue(3.0D).build());
      this.avoidLanding = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("avoid-landing")).description("Will try to avoid landing if your target is on the ground.")).defaultValue(true)).build());
      this.predictMovement = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict-movement")).description("Tries to predict the targets position according to their movement.")).defaultValue(true)).build());
      this.onlyFriends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-friends")).description("Will only follow friends.")).defaultValue(false)).build());
      this.action = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("action-on-target-loss")).description("What to do if you lose the target.")).defaultValue(AutoWasp.Action.TOGGLE)).build());
      this.offset = this.sgGeneral.add(((Vector3dSetting.Builder)((Vector3dSetting.Builder)(new Vector3dSetting.Builder()).name("offset")).description("How many blocks offset to wasp at from the target.")).defaultValue(0.0D, 0.0D, 0.0D).build());
      this.jumpTimer = 0;
      this.incrementJumpTimer = false;
   }

   public void onActivate() {
      if (this.target == null || this.target.method_31481()) {
         this.target = (class_1657)TargetUtils.get((entity) -> {
            if (entity instanceof class_1657 && entity != this.mc.field_1724) {
               if (!((class_1657)entity).method_29504() && !(((class_1657)entity).method_6032() <= 0.0F)) {
                  return !(Boolean)this.onlyFriends.get() || Friends.get().get((class_1657)entity) != null;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }, SortPriority.LowestDistance);
         if (this.target == null) {
            this.error("No valid targets.", new Object[0]);
            this.toggle();
            return;
         }

         this.info(this.target.method_5477().getString() + " set as target.", new Object[0]);
      }

      this.jumpTimer = 0;
      this.incrementJumpTimer = false;
   }

   public void onDeactivate() {
      this.target = null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.target.method_31481()) {
         this.warning("Lost target!", new Object[0]);
         switch(((AutoWasp.Action)this.action.get()).ordinal()) {
         case 0:
            this.toggle();
            break;
         case 1:
            this.onActivate();
            break;
         case 2:
            this.mc.field_1724.field_3944.method_52781(new class_2661(class_2561.method_43470("%s[%sAuto Wasp%s] Lost target.".formatted(new Object[]{class_124.field_1080, class_124.field_1078, class_124.field_1080}))));
         }

         if (!this.isActive()) {
            return;
         }
      }

      if (this.mc.field_1724.method_6118(class_1304.field_6174).method_7909() instanceof class_1770) {
         if (this.incrementJumpTimer) {
            ++this.jumpTimer;
         }

         if (!this.mc.field_1724.method_6128()) {
            if (!this.incrementJumpTimer) {
               this.incrementJumpTimer = true;
            }

            if (this.mc.field_1724.method_24828() && this.incrementJumpTimer) {
               this.mc.field_1724.method_6043();
               return;
            }

            if (this.jumpTimer >= 4) {
               this.jumpTimer = 0;
               this.mc.field_1724.method_6100(false);
               this.mc.field_1724.method_5728(true);
               this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
            }
         } else {
            this.incrementJumpTimer = false;
            this.jumpTimer = 0;
         }

      }
   }

   @EventHandler
   private void onMove(PlayerMoveEvent event) {
      if (this.mc.field_1724.method_6118(class_1304.field_6174).method_7909() instanceof class_1770) {
         if (this.mc.field_1724.method_6128()) {
            double xVel = 0.0D;
            double yVel = 0.0D;
            double zVel = 0.0D;
            class_243 targetPos = this.target.method_19538().method_1031(((Vector3d)this.offset.get()).x, ((Vector3d)this.offset.get()).y, ((Vector3d)this.offset.get()).z);
            if ((Boolean)this.predictMovement.get()) {
               targetPos.method_1019(class_1657.method_20736(this.target, this.target.method_18798(), this.target.method_5829(), this.mc.field_1687, this.mc.field_1687.method_20743(this.target, this.target.method_5829().method_18804(this.target.method_18798()))));
            }

            double xDist;
            if ((Boolean)this.avoidLanding.get()) {
               xDist = this.target.method_5829().method_17939() / 2.0D;
               class_2350[] var11 = class_2350.field_11041;
               int var12 = var11.length;

               for(int var13 = 0; var13 < var12; ++var13) {
                  class_2350 dir = var11[var13];
                  class_2338 pos = class_2338.method_49638(targetPos.method_43206(dir, xDist).method_43206(dir.method_10170(), xDist)).method_10074();
                  if (this.mc.field_1687.method_8320(pos).method_26204().field_23159 && Math.abs(targetPos.method_10214() - (double)(pos.method_10264() + 1)) <= 0.25D) {
                     targetPos = new class_243(targetPos.field_1352, (double)pos.method_10264() + 1.25D, targetPos.field_1350);
                     break;
                  }
               }
            }

            xDist = targetPos.method_10216() - this.mc.field_1724.method_23317();
            double zDist = targetPos.method_10215() - this.mc.field_1724.method_23321();
            double absX = Math.abs(xDist);
            double absZ = Math.abs(zDist);
            double diag = 0.0D;
            if (absX > 9.999999747378752E-6D && absZ > 9.999999747378752E-6D) {
               diag = 1.0D / Math.sqrt(absX * absX + absZ * absZ);
            }

            if (absX > 9.999999747378752E-6D) {
               if (absX < (Double)this.horizontalSpeed.get()) {
                  xVel = xDist;
               } else {
                  xVel = (Double)this.horizontalSpeed.get() * Math.signum(xDist);
               }

               if (diag != 0.0D) {
                  xVel *= absX * diag;
               }
            }

            if (absZ > 9.999999747378752E-6D) {
               if (absZ < (Double)this.horizontalSpeed.get()) {
                  zVel = zDist;
               } else {
                  zVel = (Double)this.horizontalSpeed.get() * Math.signum(zDist);
               }

               if (diag != 0.0D) {
                  zVel *= absZ * diag;
               }
            }

            double yDist = targetPos.method_10214() - this.mc.field_1724.method_23318();
            if (Math.abs(yDist) > 9.999999747378752E-6D) {
               if (Math.abs(yDist) < (Double)this.verticalSpeed.get()) {
                  yVel = yDist;
               } else {
                  yVel = (Double)this.verticalSpeed.get() * Math.signum(yDist);
               }
            }

            ((IVec3d)event.movement).set(xVel, yVel, zVel);
         }
      }
   }

   public static enum Action {
      TOGGLE,
      CHOOSE_NEW_TARGET,
      DISCONNECT;

      public String toString() {
         String var10000;
         switch(this.ordinal()) {
         case 0:
            var10000 = "Toggle module";
            break;
         case 1:
            var10000 = "Choose new target";
            break;
         case 2:
            var10000 = "Disconnect";
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static AutoWasp.Action[] $values() {
         return new AutoWasp.Action[]{TOGGLE, CHOOSE_NEW_TARGET, DISCONNECT};
      }
   }
}
