package meteordevelopment.meteorclient.utils.entity;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.CrossbowItemAccessor;
import meteordevelopment.meteorclient.mixin.ProjectileInGroundAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.MissHitResult;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1667;
import net.minecraft.class_1670;
import net.minecraft.class_1674;
import net.minecraft.class_1675;
import net.minecraft.class_1680;
import net.minecraft.class_1681;
import net.minecraft.class_1683;
import net.minecraft.class_1684;
import net.minecraft.class_1685;
import net.minecraft.class_1686;
import net.minecraft.class_1687;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1771;
import net.minecraft.class_1776;
import net.minecraft.class_1779;
import net.minecraft.class_1787;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1823;
import net.minecraft.class_1835;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3610;
import net.minecraft.class_3612;
import net.minecraft.class_3959;
import net.minecraft.class_4076;
import net.minecraft.class_4537;
import net.minecraft.class_8956;
import net.minecraft.class_9239;
import net.minecraft.class_9278;
import net.minecraft.class_9334;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class ProjectileEntitySimulator {
   private static final class_2339 blockPos = new class_2339();
   private static final class_243 pos3d = new class_243(0.0D, 0.0D, 0.0D);
   private static final class_243 prevPos3d = new class_243(0.0D, 0.0D, 0.0D);
   public final Vector3d pos = new Vector3d();
   private final Vector3d velocity = new Vector3d();
   private class_1297 simulatingEntity;
   private double gravity;
   private double airDrag;
   private double waterDrag;
   private float height;
   private float width;

   public boolean set(class_1297 user, class_1799 itemStack, double simulated, boolean accurate, float tickDelta) {
      class_1792 item = itemStack.method_7909();
      Objects.requireNonNull(item);
      byte var9 = 0;
      switch(item.typeSwitch<invokedynamic>(item, var9)) {
      case 0:
         class_1753 ignored = (class_1753)item;
         double charge = (double)class_1753.method_7722(MeteorClient.mc.field_1724.method_6048());
         if (charge <= 0.1D) {
            return false;
         }

         this.set(user, 0.0D, charge * 3.0D, simulated, 0.05D, 0.6D, accurate, tickDelta, class_1299.field_6122);
         break;
      case 1:
         class_1764 ignored = (class_1764)item;
         class_9278 projectilesComponent = (class_9278)itemStack.method_57824(class_9334.field_49649);
         if (projectilesComponent == null) {
            return false;
         }

         if (projectilesComponent.method_57438(class_1802.field_8639)) {
            this.set(user, 0.0D, (double)CrossbowItemAccessor.getSpeed(projectilesComponent), simulated, 0.0D, 0.6D, accurate, tickDelta, class_1299.field_6133);
         } else {
            this.set(user, 0.0D, (double)CrossbowItemAccessor.getSpeed(projectilesComponent), simulated, 0.05D, 0.6D, accurate, tickDelta, class_1299.field_6122);
         }
         break;
      case 2:
         class_9239 ignored = (class_9239)item;
         this.set(user, 0.0D, 1.5D, simulated, 0.0D, 1.0D, accurate, tickDelta, class_1299.field_47243);
         this.airDrag = 1.0D;
         break;
      case 3:
         class_1787 ignored = (class_1787)item;
         this.setFishingBobber(user, tickDelta);
         break;
      case 4:
         class_1835 ignored = (class_1835)item;
         this.set(user, 0.0D, 2.5D, simulated, 0.05D, 0.99D, accurate, tickDelta, class_1299.field_6127);
         break;
      case 5:
         class_1823 ignored = (class_1823)item;
         this.set(user, 0.0D, 1.5D, simulated, 0.03D, 0.8D, accurate, tickDelta, class_1299.field_6068);
         break;
      case 6:
         class_1771 ignored = (class_1771)item;
         this.set(user, 0.0D, 1.5D, simulated, 0.03D, 0.8D, accurate, tickDelta, class_1299.field_6144);
         break;
      case 7:
         class_1776 ignored = (class_1776)item;
         this.set(user, 0.0D, 1.5D, simulated, 0.03D, 0.8D, accurate, tickDelta, class_1299.field_6082);
         break;
      case 8:
         class_1779 ignored = (class_1779)item;
         this.set(user, -20.0D, 0.7D, simulated, 0.07D, 0.8D, accurate, tickDelta, class_1299.field_6064);
         break;
      case 9:
         class_4537 ignored = (class_4537)item;
         this.set(user, -20.0D, 0.5D, simulated, 0.05D, 0.8D, accurate, tickDelta, class_1299.field_6045);
         break;
      default:
         return false;
      }

      return true;
   }

   public void set(class_1297 user, double roll, double speed, double simulated, double gravity, double waterDrag, boolean accurate, float tickDelta, class_1299<?> type) {
      Utils.set(this.pos, user, (double)tickDelta).add(0.0D, (double)user.method_18381(user.method_18376()), 0.0D);
      double yaw;
      double pitch;
      if (user == MeteorClient.mc.field_1724) {
         yaw = (double)MeteorClient.ROTATION.lastYaw;
         pitch = (double)MeteorClient.ROTATION.lastPitch;
      } else {
         yaw = (double)user.method_5705(tickDelta);
         pitch = (double)user.method_5695(tickDelta);
      }

      double x;
      double y;
      double z;
      class_243 vel;
      if (simulated == 0.0D) {
         x = -Math.sin(yaw * 0.017453292D) * Math.cos(pitch * 0.017453292D);
         y = -Math.sin((pitch + roll) * 0.017453292D);
         z = Math.cos(yaw * 0.017453292D) * Math.cos(pitch * 0.017453292D);
      } else {
         vel = user.method_18864(1.0F);
         Quaterniond quaternion = (new Quaterniond()).setAngleAxis(simulated, vel.field_1352, vel.field_1351, vel.field_1350);
         class_243 vec3d2 = user.method_5828(1.0F);
         Vector3d vector3f = new Vector3d(vec3d2.field_1352, vec3d2.field_1351, vec3d2.field_1350);
         vector3f.rotate(quaternion);
         x = vector3f.x;
         y = vector3f.y;
         z = vector3f.z;
      }

      this.velocity.set(x, y, z).normalize().mul(speed);
      if (accurate) {
         vel = user.method_18798();
         this.velocity.add(vel.field_1352, user.method_24828() ? 0.0D : vel.field_1351, vel.field_1350);
      }

      this.simulatingEntity = user;
      this.gravity = gravity;
      this.airDrag = 0.99D;
      this.waterDrag = waterDrag;
      this.width = type.method_17685();
      this.height = type.method_17686();
   }

   public boolean set(class_1297 entity, boolean accurate) {
      if (entity instanceof ProjectileInGroundAccessor) {
         ProjectileInGroundAccessor ppe = (ProjectileInGroundAccessor)entity;
         if (ppe.getInGround()) {
            return false;
         }
      }

      if (entity instanceof class_1667) {
         this.set(entity, 0.05D, 0.6D, accurate);
      } else if (entity instanceof class_1685) {
         this.set(entity, 0.05D, 0.99D, accurate);
      } else if (!(entity instanceof class_1684) && !(entity instanceof class_1680) && !(entity instanceof class_1681)) {
         if (entity instanceof class_1683) {
            this.set(entity, 0.07D, 0.8D, accurate);
         } else if (entity instanceof class_1686) {
            this.set(entity, 0.05D, 0.8D, accurate);
         } else {
            if (!(entity instanceof class_1687) && !(entity instanceof class_1674) && !(entity instanceof class_1670) && !(entity instanceof class_8956)) {
               return false;
            }

            this.set(entity, 0.0D, 1.0D, accurate);
            this.airDrag = 1.0D;
         }
      } else {
         this.set(entity, 0.03D, 0.8D, accurate);
      }

      if (entity.method_5740()) {
         this.gravity = 0.0D;
      }

      return true;
   }

   public void set(class_1297 entity, double gravity, double waterDrag, boolean accurate) {
      this.pos.set(entity.method_23317(), entity.method_23318(), entity.method_23321());
      double speed = entity.method_18798().method_1033();
      this.velocity.set(entity.method_18798().field_1352, entity.method_18798().field_1351, entity.method_18798().field_1350).normalize().mul(speed);
      if (accurate) {
         class_243 vel = entity.method_18798();
         this.velocity.add(vel.field_1352, entity.method_24828() ? 0.0D : vel.field_1351, vel.field_1350);
      }

      this.simulatingEntity = entity;
      this.gravity = gravity;
      this.airDrag = 0.99D;
      this.waterDrag = waterDrag;
      this.width = entity.method_17681();
      this.height = entity.method_17682();
   }

   public void setFishingBobber(class_1297 user, float tickDelta) {
      double yaw;
      double pitch;
      if (user == MeteorClient.mc.field_1724 && Rotations.rotating) {
         yaw = (double)Rotations.serverYaw;
         pitch = (double)Rotations.serverPitch;
      } else {
         yaw = (double)user.method_5705(tickDelta);
         pitch = (double)user.method_5695(tickDelta);
      }

      double h = Math.cos(-yaw * 0.01745329238474369D - 3.1415927410125732D);
      double i = Math.sin(-yaw * 0.01745329238474369D - 3.1415927410125732D);
      double j = -Math.cos(-pitch * 0.01745329238474369D);
      double k = Math.sin(-pitch * 0.01745329238474369D);
      Utils.set(this.pos, user, (double)tickDelta).sub(i * 0.3D, 0.0D, h * 0.3D).add(0.0D, (double)user.method_18381(user.method_18376()), 0.0D);
      this.velocity.set(-i, class_3532.method_15350(-(k / j), -5.0D, 5.0D), -h);
      double l = this.velocity.length();
      this.velocity.mul(0.6D / l + 0.5D, 0.6D / l + 0.5D, 0.6D / l + 0.5D);
      this.simulatingEntity = user;
      this.gravity = 0.03D;
      this.airDrag = 0.92D;
      this.waterDrag = 0.0D;
      this.width = class_1299.field_6103.method_17685();
      this.height = class_1299.field_6103.method_17686();
   }

   public class_239 tick() {
      ((IVec3d)prevPos3d).set(this.pos);
      this.pos.add(this.velocity);
      this.velocity.mul(this.isTouchingWater() ? this.waterDrag : this.airDrag);
      this.velocity.sub(0.0D, this.gravity, 0.0D);
      if (this.pos.y < (double)MeteorClient.mc.field_1687.method_31607()) {
         return MissHitResult.INSTANCE;
      } else {
         int chunkX = class_4076.method_32204(this.pos.x);
         int chunkZ = class_4076.method_32204(this.pos.z);
         if (!MeteorClient.mc.field_1687.method_2935().method_12123(chunkX, chunkZ)) {
            return MissHitResult.INSTANCE;
         } else {
            ((IVec3d)pos3d).set(this.pos);
            if (pos3d.equals(prevPos3d)) {
               return MissHitResult.INSTANCE;
            } else {
               class_239 hitResult = this.getCollision();
               return hitResult.method_17783() == class_240.field_1333 ? null : hitResult;
            }
         }
      }
   }

   private boolean isTouchingWater() {
      blockPos.method_10102(this.pos.x, this.pos.y, this.pos.z);
      class_3610 fluidState = MeteorClient.mc.field_1687.method_8316(blockPos);
      if (fluidState.method_15772() != class_3612.field_15910 && fluidState.method_15772() != class_3612.field_15909) {
         return false;
      } else {
         return this.pos.y - (double)((int)this.pos.y) <= (double)fluidState.method_20785();
      }
   }

   private class_239 getCollision() {
      class_239 hitResult = MeteorClient.mc.field_1687.method_17742(new class_3959(prevPos3d, pos3d, class_3960.field_17558, this.waterDrag == 0.0D ? class_242.field_1347 : class_242.field_1348, this.simulatingEntity));
      if (((class_239)hitResult).method_17783() != class_240.field_1333) {
         ((IVec3d)pos3d).set(((class_239)hitResult).method_17784().field_1352, ((class_239)hitResult).method_17784().field_1351, ((class_239)hitResult).method_17784().field_1350);
      }

      class_238 box = (new class_238(prevPos3d.field_1352 - (double)(this.width / 2.0F), prevPos3d.field_1351, prevPos3d.field_1350 - (double)(this.width / 2.0F), prevPos3d.field_1352 + (double)(this.width / 2.0F), prevPos3d.field_1351 + (double)this.height, prevPos3d.field_1350 + (double)(this.width / 2.0F))).method_1012(this.velocity.x, this.velocity.y, this.velocity.z).method_1014(1.0D);
      class_239 hitResult2 = class_1675.method_18077(MeteorClient.mc.field_1687, this.simulatingEntity == MeteorClient.mc.field_1724 ? null : this.simulatingEntity, prevPos3d, pos3d, box, (entity) -> {
         return !entity.method_7325() && entity.method_5805() && entity.method_5863();
      });
      if (hitResult2 != null) {
         hitResult = hitResult2;
      }

      return (class_239)hitResult;
   }
}
