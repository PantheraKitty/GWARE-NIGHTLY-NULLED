package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Set;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1429;
import net.minecraft.class_1657;
import net.minecraft.class_1744;
import net.minecraft.class_1753;
import net.minecraft.class_1802;
import net.minecraft.class_243;

public class BowAimbot extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> range;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<SortPriority> priority;
   private final Setting<Boolean> babies;
   private final Setting<Boolean> nametagged;
   private final Setting<Boolean> pauseOnCombat;
   private boolean wasPathing;
   private class_1297 target;

   public BowAimbot() {
      super(Categories.Combat, "bow-aimbot", "Automatically aims your bow for you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The maximum range the entity can be to aim at it.")).defaultValue(20.0D).range(0.0D, 100.0D).sliderMax(100.0D).build());
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to attack.")).onlyAttackable().build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("priority")).description("What type of entities to target.")).defaultValue(SortPriority.LowestHealth)).build());
      this.babies = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("babies")).description("Whether or not to attack baby variants of the entity.")).defaultValue(true)).build());
      this.nametagged = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("nametagged")).description("Whether or not to attack mobs with a name tag.")).defaultValue(false)).build());
      this.pauseOnCombat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-combat")).description("Freezes Baritone temporarily until you released the bow.")).defaultValue(false)).build());
   }

   public void onDeactivate() {
      this.target = null;
      this.wasPathing = false;
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (PlayerUtils.isAlive() && this.itemInHand()) {
         if (this.mc.field_1724.method_31549().field_7477 || InvUtils.find((itemStack) -> {
            return itemStack.method_7909() instanceof class_1744;
         }).found()) {
            this.target = TargetUtils.get((entity) -> {
               if (entity != this.mc.field_1724 && entity != this.mc.field_1719) {
                  if ((!(entity instanceof class_1309) || !((class_1309)entity).method_29504()) && entity.method_5805()) {
                     if (!PlayerUtils.isWithin(entity, (Double)this.range.get())) {
                        return false;
                     } else if (!((Set)this.entities.get()).contains(entity.method_5864())) {
                        return false;
                     } else if (!(Boolean)this.nametagged.get() && entity.method_16914()) {
                        return false;
                     } else if (!PlayerUtils.canSeeEntity(entity)) {
                        return false;
                     } else {
                        if (entity instanceof class_1657) {
                           if (((class_1657)entity).method_7337()) {
                              return false;
                           }

                           if (!Friends.get().shouldAttack((class_1657)entity)) {
                              return false;
                           }
                        }

                        return !(entity instanceof class_1429) || (Boolean)this.babies.get() || !((class_1429)entity).method_6109();
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            }, (SortPriority)this.priority.get());
            if (this.target == null) {
               if (this.wasPathing) {
                  PathManagers.get().resume();
                  this.wasPathing = false;
               }

            } else {
               if (this.mc.field_1690.field_1904.method_1434() && this.itemInHand()) {
                  if ((Boolean)this.pauseOnCombat.get() && PathManagers.get().isPathing() && !this.wasPathing) {
                     PathManagers.get().pause();
                     this.wasPathing = true;
                  }

                  this.aim(event.tickDelta);
               }

            }
         }
      }
   }

   private boolean itemInHand() {
      return InvUtils.testInMainHand(class_1802.field_8102, class_1802.field_8399);
   }

   private void aim(float tickDelta) {
      float velocity = class_1753.method_7722(this.mc.field_1724.method_6048());
      class_243 pos = this.target.method_30950(tickDelta);
      double relativeX = pos.field_1352 - this.mc.field_1724.method_23317();
      double relativeY = pos.field_1351 + (double)(this.target.method_17682() / 2.0F) - this.mc.field_1724.method_23320();
      double relativeZ = pos.field_1350 - this.mc.field_1724.method_23321();
      double hDistance = Math.sqrt(relativeX * relativeX + relativeZ * relativeZ);
      double hDistanceSq = hDistance * hDistance;
      float g = 0.006F;
      float velocitySq = velocity * velocity;
      float pitch = (float)(-Math.toDegrees(Math.atan(((double)velocitySq - Math.sqrt((double)(velocitySq * velocitySq) - (double)g * ((double)g * hDistanceSq + 2.0D * relativeY * (double)velocitySq))) / ((double)g * hDistance))));
      if (Float.isNaN(pitch)) {
         Rotations.rotate(Rotations.getYaw(this.target), Rotations.getPitch(this.target));
      } else {
         Rotations.rotate(Rotations.getYaw(new class_243(pos.field_1352, pos.field_1351, pos.field_1350)), (double)pitch);
      }

   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }
}
