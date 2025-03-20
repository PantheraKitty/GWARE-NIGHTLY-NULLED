package meteordevelopment.meteorclient.utils.tooltip;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1477;
import net.minecraft.class_308;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_5684;
import net.minecraft.class_6053;
import net.minecraft.class_7833;
import net.minecraft.class_898;
import net.minecraft.class_4597.class_4598;
import org.joml.Quaternionf;

public class EntityTooltipComponent implements MeteorTooltipData, class_5684 {
   protected final class_1297 entity;

   public EntityTooltipComponent(class_1297 entity) {
      this.entity = entity;
   }

   public class_5684 getComponent() {
      return this;
   }

   public int method_32661() {
      return 24;
   }

   public int method_32664(class_327 textRenderer) {
      return 60;
   }

   public void method_32666(class_327 textRenderer, int x, int y, class_332 context) {
      class_4587 matrices = context.method_51448();
      matrices.method_22903();
      matrices.method_46416(15.0F, 2.0F, 0.0F);
      this.entity.method_18800(1.0D, 1.0D, 1.0D);
      this.renderEntity(matrices, x, y);
      matrices.method_22909();
   }

   protected void renderEntity(class_4587 matrices, int x, int y) {
      if (MeteorClient.mc.field_1724 != null) {
         float size = 24.0F;
         if ((double)Math.max(this.entity.method_17681(), this.entity.method_17682()) > 1.0D) {
            size /= Math.max(this.entity.method_17681(), this.entity.method_17682());
         }

         class_308.method_24210();
         matrices.method_22903();
         int yOffset = 16;
         if (this.entity instanceof class_1477) {
            size = 16.0F;
            yOffset = 2;
         }

         matrices.method_46416((float)(x + 10), (float)(y + yOffset), 1050.0F);
         matrices.method_22905(1.0F, 1.0F, -1.0F);
         matrices.method_46416(0.0F, 0.0F, 1000.0F);
         matrices.method_22905(size, size, size);
         Quaternionf quaternion = class_7833.field_40718.rotationDegrees(180.0F);
         Quaternionf quaternion2 = class_7833.field_40714.rotationDegrees(-10.0F);
         this.hamiltonProduct(quaternion, quaternion2);
         matrices.method_22907(quaternion);
         this.setupAngles();
         class_898 entityRenderDispatcher = MeteorClient.mc.method_1561();
         quaternion2.conjugate();
         entityRenderDispatcher.method_24196(quaternion2);
         entityRenderDispatcher.method_3948(false);
         class_4598 immediate = MeteorClient.mc.method_22940().method_23000();
         this.entity.field_6012 = MeteorClient.mc.field_1724.field_6012;
         this.entity.method_5880(false);
         entityRenderDispatcher.method_3954(this.entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrices, immediate, 15728880);
         immediate.method_22993();
         entityRenderDispatcher.method_3948(true);
         matrices.method_22909();
         class_308.method_24211();
      }
   }

   public void hamiltonProduct(Quaternionf q, Quaternionf other) {
      float f = q.x();
      float g = q.y();
      float h = q.z();
      float i = q.w();
      float j = other.x();
      float k = other.y();
      float l = other.z();
      float m = other.w();
      q.x = i * j + f * m + g * l - h * k;
      q.y = i * k - f * l + g * m + h * j;
      q.z = i * l + f * k - g * j + h * m;
      q.w = i * m - f * j - g * k - h * l;
   }

   protected void setupAngles() {
      float yaw = (float)System.currentTimeMillis() / 10.0F % 360.0F;
      this.entity.method_36456(yaw);
      this.entity.method_5847(yaw);
      this.entity.method_36457(0.0F);
      class_1297 var3 = this.entity;
      if (var3 instanceof class_1309) {
         class_1309 livingEntity = (class_1309)var3;
         if (this.entity instanceof class_6053) {
            livingEntity.field_6241 = yaw;
         }

         livingEntity.field_6283 = yaw;
      }

   }
}
