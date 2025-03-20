package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ProjectileEntityAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.ProjectileEntitySimulator;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1667;
import net.minecraft.class_1676;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2828.class_2829;
import org.joml.Vector3d;

public class ArrowDodge extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgMovement;
   private final Setting<ArrowDodge.MoveType> moveType;
   private final Setting<Double> moveSpeed;
   private final Setting<Double> distanceCheck;
   private final Setting<Boolean> accurate;
   private final Setting<Boolean> groundCheck;
   private final Setting<Boolean> allProjectiles;
   private final Setting<Boolean> ignoreOwn;
   public final Setting<Integer> simulationSteps;
   private final List<class_243> possibleMoveDirections;
   private final ProjectileEntitySimulator simulator;
   private final Pool<Vector3d> vec3s;
   private final List<Vector3d> points;

   public ArrowDodge() {
      super(Categories.Combat, "arrow-dodge", "Tries to dodge arrows coming at you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgMovement = this.settings.createGroup("Movement");
      this.moveType = this.sgMovement.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("move-type")).description("The way you are moved by this module.")).defaultValue(ArrowDodge.MoveType.Velocity)).build());
      this.moveSpeed = this.sgMovement.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("move-speed")).description("How fast should you be when dodging arrow.")).defaultValue(1.0D).min(0.01D).sliderRange(0.01D, 5.0D).build());
      this.distanceCheck = this.sgMovement.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("distance-check")).description("How far should an arrow be from the player to be considered not hitting.")).defaultValue(1.0D).min(0.01D).sliderRange(0.01D, 5.0D).build());
      this.accurate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("accurate")).description("Whether or not to calculate more accurate.")).defaultValue(false)).build());
      this.groundCheck = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ground-check")).description("Tries to prevent you from falling to your death.")).defaultValue(true)).build());
      this.allProjectiles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("all-projectiles")).description("Dodge all projectiles, not only arrows.")).defaultValue(false)).build());
      this.ignoreOwn = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-own")).description("Ignore your own projectiles.")).defaultValue(false)).build());
      this.simulationSteps = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("simulation-steps")).description("How many steps to simulate projectiles. Zero for no limit.")).defaultValue(500)).sliderMax(5000).build());
      this.possibleMoveDirections = Arrays.asList(new class_243(1.0D, 0.0D, 1.0D), new class_243(0.0D, 0.0D, 1.0D), new class_243(-1.0D, 0.0D, 1.0D), new class_243(1.0D, 0.0D, 0.0D), new class_243(-1.0D, 0.0D, 0.0D), new class_243(1.0D, 0.0D, -1.0D), new class_243(0.0D, 0.0D, -1.0D), new class_243(-1.0D, 0.0D, -1.0D));
      this.simulator = new ProjectileEntitySimulator();
      this.vec3s = new Pool(Vector3d::new);
      this.points = new ArrayList();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      Iterator var2 = this.points.iterator();

      while(var2.hasNext()) {
         Vector3d point = (Vector3d)var2.next();
         this.vec3s.free(point);
      }

      this.points.clear();
      var2 = this.mc.field_1687.method_18112().iterator();

      while(true) {
         UUID owner;
         class_1297 e;
         int i;
         do {
            do {
               do {
                  do {
                     if (!var2.hasNext()) {
                        if (this.isValid(class_243.field_1353, false)) {
                           return;
                        }

                        double speed = (Double)this.moveSpeed.get();

                        for(i = 0; i < 500; ++i) {
                           boolean didMove = false;
                           Collections.shuffle(this.possibleMoveDirections);
                           Iterator var6 = this.possibleMoveDirections.iterator();

                           while(var6.hasNext()) {
                              class_243 direction = (class_243)var6.next();
                              class_243 velocity = direction.method_1021(speed);
                              if (this.isValid(velocity, true)) {
                                 this.move(velocity);
                                 didMove = true;
                                 break;
                              }
                           }

                           if (didMove) {
                              break;
                           }

                           speed += (Double)this.moveSpeed.get();
                        }

                        return;
                     }

                     e = (class_1297)var2.next();
                  } while(!(e instanceof class_1676));
               } while(!(Boolean)this.allProjectiles.get() && !(e instanceof class_1667));

               if (!(Boolean)this.ignoreOwn.get()) {
                  break;
               }

               owner = ((ProjectileEntityAccessor)e).getOwnerUuid();
            } while(owner != null && owner.equals(this.mc.field_1724.method_5667()));
         } while(!this.simulator.set(e, (Boolean)this.accurate.get()));

         for(i = 0; i < ((Integer)this.simulationSteps.get() > 0 ? (Integer)this.simulationSteps.get() : Integer.MAX_VALUE); ++i) {
            this.points.add(((Vector3d)this.vec3s.get()).set(this.simulator.pos));
            if (this.simulator.tick() != null) {
               break;
            }
         }
      }
   }

   private void move(class_243 vel) {
      this.move(vel.field_1352, vel.field_1351, vel.field_1350);
   }

   private void move(double velX, double velY, double velZ) {
      switch(((ArrowDodge.MoveType)this.moveType.get()).ordinal()) {
      case 0:
         this.mc.field_1724.method_18800(velX, velY, velZ);
         break;
      case 1:
         class_243 newPos = this.mc.field_1724.method_19538().method_1031(velX, velY, velZ);
         this.mc.field_1724.field_3944.method_52787(new class_2829(newPos.field_1352, newPos.field_1351, newPos.field_1350, false));
         this.mc.field_1724.field_3944.method_52787(new class_2829(newPos.field_1352, newPos.field_1351 - 0.01D, newPos.field_1350, true));
      }

   }

   private boolean isValid(class_243 velocity, boolean checkGround) {
      class_243 playerPos = this.mc.field_1724.method_19538().method_1019(velocity);
      class_243 headPos = playerPos.method_1031(0.0D, 1.0D, 0.0D);
      Iterator var5 = this.points.iterator();

      class_243 projectilePos;
      do {
         if (!var5.hasNext()) {
            if (checkGround) {
               class_2338 blockPos = this.mc.field_1724.method_24515().method_10081(class_2338.method_49637(velocity.field_1352, velocity.field_1351, velocity.field_1350));
               if (!this.mc.field_1687.method_8320(blockPos).method_26220(this.mc.field_1687, blockPos).method_1110()) {
                  return false;
               }

               if (!this.mc.field_1687.method_8320(blockPos.method_10084()).method_26220(this.mc.field_1687, blockPos.method_10084()).method_1110()) {
                  return false;
               }

               if ((Boolean)this.groundCheck.get()) {
                  return !this.mc.field_1687.method_8320(blockPos.method_10074()).method_26220(this.mc.field_1687, blockPos.method_10074()).method_1110();
               }
            }

            return true;
         }

         Vector3d pos = (Vector3d)var5.next();
         projectilePos = new class_243(pos.x, pos.y, pos.z);
         if (projectilePos.method_24802(playerPos, (Double)this.distanceCheck.get())) {
            return false;
         }
      } while(!projectilePos.method_24802(headPos, (Double)this.distanceCheck.get()));

      return false;
   }

   public static enum MoveType {
      Velocity,
      Packet;

      // $FF: synthetic method
      private static ArrowDodge.MoveType[] $values() {
         return new ArrowDodge.MoveType[]{Velocity, Packet};
      }
   }
}
