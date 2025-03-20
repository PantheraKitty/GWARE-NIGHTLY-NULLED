package meteordevelopment.meteorclient.pathing;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.process.IBaritoneProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.Rotation;
import baritone.api.utils.SettingsUtil;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public class BaritonePathManager implements IPathManager {
   private final VarHandle rotationField;
   private final BaritoneSettings settings;
   private BaritonePathManager.GoalDirection directionGoal;
   private boolean pathingPaused;

   public BaritonePathManager() {
      MeteorClient.EVENT_BUS.subscribe((Object)this);
      Class<?> klass = BaritoneAPI.getProvider().getPrimaryBaritone().getLookBehavior().getClass();
      VarHandle rotationField = null;
      Field[] var3 = klass.getDeclaredFields();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Field field = var3[var5];
         if (field.getType() == Rotation.class) {
            try {
               rotationField = MethodHandles.lookup().unreflectVarHandle(field);
               break;
            } catch (IllegalAccessException var8) {
               throw new RuntimeException(var8);
            }
         }
      }

      this.rotationField = rotationField;
      this.settings = new BaritoneSettings();
      BaritoneAPI.getProvider().getPrimaryBaritone().getPathingControlManager().registerProcess(new BaritonePathManager.BaritoneProcess());
   }

   public String getName() {
      return "Baritone";
   }

   public boolean isPathing() {
      return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
   }

   public void pause() {
      this.pathingPaused = true;
   }

   public void resume() {
      this.pathingPaused = false;
   }

   public void stop() {
      BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
   }

   public void moveTo(class_2338 pos, boolean ignoreY) {
      if (ignoreY) {
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalXZ(pos.method_10263(), pos.method_10260()));
      } else {
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(pos));
      }
   }

   public void moveToBlockPos(class_2338 pos) {
      BaritoneAPI.getProvider().getBaritoneForPlayer(MeteorClient.mc.field_1724).getCustomGoalProcess().setGoalAndPath(new GoalBlock(pos));
   }

   public void moveInDirection(float yaw) {
      this.directionGoal = new BaritonePathManager.GoalDirection(yaw);
      BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(this.directionGoal);
   }

   public void mine(class_2248... blocks) {
      BaritoneAPI.getProvider().getPrimaryBaritone().getMineProcess().mine(blocks);
   }

   public void follow(Predicate<class_1297> entity) {
      BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().follow(entity);
   }

   public float getTargetYaw() {
      Rotation rotation = this.rotationField.get(BaritoneAPI.getProvider().getPrimaryBaritone().getLookBehavior());
      return rotation == null ? 0.0F : rotation.getYaw();
   }

   public float getTargetPitch() {
      Rotation rotation = this.rotationField.get(BaritoneAPI.getProvider().getPrimaryBaritone().getLookBehavior());
      return rotation == null ? 0.0F : rotation.getPitch();
   }

   public IPathManager.ISettings getSettings() {
      return this.settings;
   }

   @EventHandler(
      priority = 200
   )
   private void onTick(TickEvent.Pre event) {
      if (this.directionGoal != null) {
         if (this.directionGoal != BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().getGoal()) {
            this.directionGoal = null;
         } else {
            this.directionGoal.tick();
         }
      }
   }

   private class BaritoneProcess implements IBaritoneProcess {
      public boolean isActive() {
         return BaritonePathManager.this.pathingPaused;
      }

      public PathingCommand onTick(boolean b, boolean b1) {
         BaritoneAPI.getProvider().getPrimaryBaritone().getInputOverrideHandler().clearAllKeys();
         return new PathingCommand((Goal)null, PathingCommandType.REQUEST_PAUSE);
      }

      public boolean isTemporary() {
         return true;
      }

      public void onLostControl() {
      }

      public double priority() {
         return 0.0D;
      }

      public String displayName0() {
         return "Meteor Client";
      }
   }

   private static class GoalDirection implements Goal {
      private static final double SQRT_2 = Math.sqrt(2.0D);
      private final float yaw;
      private int x;
      private int z;
      private int timer;

      public GoalDirection(float yaw) {
         this.yaw = yaw;
         this.tick();
      }

      public static double calculate(double xDiff, double zDiff) {
         double x = Math.abs(xDiff);
         double z = Math.abs(zDiff);
         double straight;
         double diagonal;
         if (x < z) {
            straight = z - x;
            diagonal = x;
         } else {
            straight = x - z;
            diagonal = z;
         }

         diagonal *= SQRT_2;
         return (diagonal + straight) * (Double)BaritoneAPI.getSettings().costHeuristic.value;
      }

      public void tick() {
         if (this.timer <= 0) {
            this.timer = 20;
            class_243 pos = MeteorClient.mc.field_1724.method_19538();
            float theta = (float)Math.toRadians((double)this.yaw);
            this.x = (int)Math.floor(pos.field_1352 - (double)class_3532.method_15374(theta) * 100.0D);
            this.z = (int)Math.floor(pos.field_1350 + (double)class_3532.method_15362(theta) * 100.0D);
         }

         --this.timer;
      }

      public boolean isInGoal(int x, int y, int z) {
         return x == this.x && z == this.z;
      }

      public double heuristic(int x, int y, int z) {
         int xDiff = x - this.x;
         int zDiff = z - this.z;
         return calculate((double)xDiff, (double)zDiff);
      }

      public String toString() {
         return String.format("GoalXZ{x=%s,z=%s}", SettingsUtil.maybeCensor(this.x), SettingsUtil.maybeCensor(this.z));
      }

      public int getX() {
         return this.x;
      }

      public int getZ() {
         return this.z;
      }
   }
}
