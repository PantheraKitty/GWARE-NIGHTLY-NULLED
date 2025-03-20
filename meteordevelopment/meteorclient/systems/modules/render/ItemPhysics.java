package meteordevelopment.meteorclient.systems.modules.render;

import java.util.Iterator;
import meteordevelopment.meteorclient.events.render.ApplyTransformationEvent;
import meteordevelopment.meteorclient.events.render.RenderItemEntityEvent;
import meteordevelopment.meteorclient.mixininterface.IBakedQuad;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1087;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_5819;
import net.minecraft.class_777;
import net.minecraft.class_7833;
import net.minecraft.class_804;
import net.minecraft.class_811;
import net.minecraft.class_918;

public class ItemPhysics extends Module {
   private static final class_2350[] FACES;
   private static final float PIXEL_SIZE = 0.0625F;
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> randomRotation;
   private final class_5819 random;
   private boolean renderingItem;

   public ItemPhysics() {
      super(Categories.Render, "item-physics", "Applies physics to items on the ground.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.randomRotation = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("random-rotation")).description("Adds a random rotation to every item.")).defaultValue(true)).build());
      this.random = class_5819.method_43053();
   }

   @EventHandler
   private void onRenderItemEntity(RenderItemEntityEvent event) {
      class_4587 matrices = event.matrixStack;
      matrices.method_22903();
      class_1799 itemStack = event.itemEntity.method_6983();
      class_1087 model = this.getModel(event.itemEntity);
      ItemPhysics.ModelInfo info = this.getInfo(model);
      this.random.method_43052((long)event.itemEntity.method_5628() * 2365798L);
      this.applyTransformation(matrices, model);
      matrices.method_46416(0.0F, info.offsetY, 0.0F);
      this.offsetInWater(matrices, event.itemEntity);
      this.preventZFighting(matrices, event.itemEntity);
      if (info.flat) {
         matrices.method_22907(class_7833.field_40714.rotationDegrees(90.0F));
         matrices.method_46416(0.0F, 0.0F, info.offsetZ);
      }

      if ((Boolean)this.randomRotation.get()) {
         class_7833 axis = class_7833.field_40716;
         if (info.flat) {
            axis = class_7833.field_40718;
         }

         float degrees = (this.random.method_43057() * 2.0F - 1.0F) * 90.0F;
         matrices.method_22907(axis.rotationDegrees(degrees));
      }

      this.renderItem(event, matrices, itemStack, model, info);
      matrices.method_22909();
      event.cancel();
   }

   @EventHandler
   private void onApplyTransformation(ApplyTransformationEvent event) {
      if (this.renderingItem) {
         event.cancel();
      }

   }

   private void renderItem(RenderItemEntityEvent event, class_4587 matrices, class_1799 itemStack, class_1087 model, ItemPhysics.ModelInfo info) {
      this.renderingItem = true;
      int count = this.getRenderedCount(itemStack);

      for(int i = 0; i < count; ++i) {
         matrices.method_22903();
         float y;
         if (i > 0) {
            y = (this.random.method_43057() * 2.0F - 1.0F) * 0.25F;
            float z = (this.random.method_43057() * 2.0F - 1.0F) * 0.25F;
            this.translate(matrices, info, y, 0.0F, z);
         }

         event.itemRenderer.method_23179(itemStack, class_811.field_4318, false, matrices, event.vertexConsumerProvider, event.light, class_4608.field_21444, model);
         matrices.method_22909();
         y = Math.max(this.random.method_43057() * 0.0625F, 0.03125F);
         this.translate(matrices, info, 0.0F, y, 0.0F);
      }

      this.renderingItem = false;
   }

   private void translate(class_4587 matrices, ItemPhysics.ModelInfo info, float x, float y, float z) {
      if (info.flat) {
         float temp = y;
         y = z;
         z = -temp;
      }

      matrices.method_46416(x, y, z);
   }

   private int getRenderedCount(class_1799 stack) {
      int i = 1;
      if (stack.method_7947() > 48) {
         i = 5;
      } else if (stack.method_7947() > 32) {
         i = 4;
      } else if (stack.method_7947() > 16) {
         i = 3;
      } else if (stack.method_7947() > 1) {
         i = 2;
      }

      return i;
   }

   private void applyTransformation(class_4587 matrices, class_1087 model) {
      class_804 transformation = model.method_4709().field_4303;
      float prevY = transformation.field_4286.y;
      transformation.field_4286.y = 0.0F;
      transformation.method_23075(false, matrices);
      transformation.field_4286.y = prevY;
   }

   private void offsetInWater(class_4587 matrices, class_1542 entity) {
      if (entity.method_5799()) {
         matrices.method_46416(0.0F, 0.333F, 0.0F);
      }

   }

   private void preventZFighting(class_4587 matrices, class_1542 entity) {
      float offset = 1.0E-4F;
      float distance = (float)this.mc.field_1773.method_19418().method_19326().method_1022(entity.method_19538());
      offset = Math.min(offset * Math.max(1.0F, distance), 0.01F);
      matrices.method_46416(0.0F, offset, 0.0F);
   }

   private class_1087 getModel(class_1542 entity) {
      class_1799 itemStack = entity.method_6983();
      if (itemStack.method_31574(class_1802.field_8547)) {
         return this.mc.method_1480().method_4012().method_3303().method_4742(class_918.field_40532);
      } else {
         return itemStack.method_31574(class_1802.field_27070) ? this.mc.method_1480().method_4012().method_3303().method_4742(class_918.field_40533) : this.mc.method_1480().method_4019(itemStack, entity.method_37908(), (class_1309)null, entity.method_5628());
      }
   }

   private ItemPhysics.ModelInfo getInfo(class_1087 model) {
      class_5819 random = class_5819.method_43053();
      float minX = Float.MAX_VALUE;
      float maxX = Float.MIN_VALUE;
      float minY = Float.MAX_VALUE;
      float maxY = Float.MIN_VALUE;
      float minZ = Float.MAX_VALUE;
      float maxZ = Float.MIN_VALUE;
      class_2350[] var9 = FACES;
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         class_2350 face = var9[var11];
         Iterator var13 = model.method_4707((class_2680)null, face, random).iterator();

         while(var13.hasNext()) {
            class_777 _quad = (class_777)var13.next();
            IBakedQuad quad = (IBakedQuad)_quad;

            for(int i = 0; i < 4; ++i) {
               switch(_quad.method_3358()) {
               case field_11033:
                  minY = Math.min(minY, quad.meteor$getY(i));
                  break;
               case field_11036:
                  maxY = Math.max(maxY, quad.meteor$getY(i));
                  break;
               case field_11043:
                  minZ = Math.min(minZ, quad.meteor$getZ(i));
                  break;
               case field_11035:
                  maxZ = Math.max(maxZ, quad.meteor$getZ(i));
                  break;
               case field_11039:
                  minX = Math.min(minX, quad.meteor$getX(i));
                  break;
               case field_11034:
                  maxX = Math.max(maxX, quad.meteor$getX(i));
               }
            }
         }
      }

      if (minX == Float.MAX_VALUE) {
         minX = 0.0F;
      }

      if (minY == Float.MAX_VALUE) {
         minY = 0.0F;
      }

      if (minZ == Float.MAX_VALUE) {
         minZ = 0.0F;
      }

      if (maxX == Float.MIN_VALUE) {
         maxX = 1.0F;
      }

      if (maxY == Float.MIN_VALUE) {
         maxY = 1.0F;
      }

      if (maxZ == Float.MIN_VALUE) {
         maxZ = 1.0F;
      }

      float x = maxX - minX;
      float y = maxY - minY;
      float z = maxZ - minZ;
      boolean flat = x > 0.0625F && y > 0.0625F && z <= 0.0625F;
      return new ItemPhysics.ModelInfo(flat, 0.5F - minY, minZ - minY);
   }

   static {
      FACES = new class_2350[]{null, class_2350.field_11036, class_2350.field_11033, class_2350.field_11034, class_2350.field_11043, class_2350.field_11035, class_2350.field_11039};
   }

   static record ModelInfo(boolean flat, float offsetY, float offsetZ) {
      ModelInfo(boolean flat, float offsetY, float offsetZ) {
         this.flat = flat;
         this.offsetY = offsetY;
         this.offsetZ = offsetZ;
      }

      public boolean flat() {
         return this.flat;
      }

      public float offsetY() {
         return this.offsetY;
      }

      public float offsetZ() {
         return this.offsetZ;
      }
   }
}
