package meteordevelopment.meteorclient.utils.render;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1007;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1690;
import net.minecraft.class_1799;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4592;
import net.minecraft.class_4595;
import net.minecraft.class_5597;
import net.minecraft.class_572;
import net.minecraft.class_578;
import net.minecraft.class_583;
import net.minecraft.class_591;
import net.minecraft.class_596;
import net.minecraft.class_630;
import net.minecraft.class_742;
import net.minecraft.class_7755;
import net.minecraft.class_7833;
import net.minecraft.class_881;
import net.minecraft.class_892;
import net.minecraft.class_897;
import net.minecraft.class_916;
import net.minecraft.class_922;
import net.minecraft.class_572.class_573;
import net.minecraft.class_630.class_593;
import net.minecraft.class_630.class_628;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

public class WireframeEntityRenderer {
   private static final class_4587 matrices = new class_4587();
   private static final Vector4f pos1 = new Vector4f();
   private static final Vector4f pos2 = new Vector4f();
   private static final Vector4f pos3 = new Vector4f();
   private static final Vector4f pos4 = new Vector4f();
   private static double offsetX;
   private static double offsetY;
   private static double offsetZ;
   private static Color sideColor;
   private static Color lineColor;
   private static ShapeMode shapeMode;

   private WireframeEntityRenderer() {
   }

   public static void render(Render3DEvent event, class_1297 entity, double scale, Color sideColor, Color lineColor, ShapeMode shapeMode) {
      WireframeEntityRenderer.sideColor = sideColor;
      WireframeEntityRenderer.lineColor = lineColor;
      WireframeEntityRenderer.shapeMode = shapeMode;
      offsetX = class_3532.method_16436((double)event.tickDelta, entity.field_6038, entity.method_23317());
      offsetY = class_3532.method_16436((double)event.tickDelta, entity.field_5971, entity.method_23318());
      offsetZ = class_3532.method_16436((double)event.tickDelta, entity.field_5989, entity.method_23321());
      matrices.method_22903();
      matrices.method_22905((float)scale, (float)scale, (float)scale);
      class_897<?> entityRenderer = MeteorClient.mc.method_1561().method_3953(entity);
      float bodyYaw;
      float headYaw;
      float h;
      float pitch;
      float j;
      if (entityRenderer instanceof class_922) {
         class_922 renderer = (class_922)entityRenderer;
         class_1309 livingEntity = (class_1309)entity;
         class_583<class_1309> model = renderer.method_4038();
         if (entityRenderer instanceof class_1007) {
            class_1007 r = (class_1007)entityRenderer;
            class_591<class_742> playerModel = (class_591)r.method_4038();
            playerModel.field_3400 = entity.method_18276();
            class_573 armPose = class_1007.method_4210((class_742)entity, class_1268.field_5808);
            class_573 armPose2 = class_1007.method_4210((class_742)entity, class_1268.field_5810);
            if (armPose.method_30156()) {
               armPose2 = livingEntity.method_6079().method_7960() ? class_573.field_3409 : class_573.field_3410;
            }

            if (livingEntity.method_6068() == class_1306.field_6183) {
               playerModel.field_3395 = armPose;
               playerModel.field_3399 = armPose2;
            } else {
               playerModel.field_3395 = armPose2;
               playerModel.field_3399 = armPose;
            }
         }

         model.field_3447 = livingEntity.method_6055(event.tickDelta);
         model.field_3449 = livingEntity.method_5765();
         model.field_3448 = livingEntity.method_6109();
         bodyYaw = class_3532.method_17821(event.tickDelta, livingEntity.field_6220, livingEntity.field_6283);
         headYaw = class_3532.method_17821(event.tickDelta, livingEntity.field_6259, livingEntity.field_6241);
         h = headYaw - bodyYaw;
         if (livingEntity.method_5765()) {
            class_1297 var16 = livingEntity.method_5854();
            if (var16 instanceof class_1309) {
               class_1309 livingEntity2 = (class_1309)var16;
               bodyYaw = class_3532.method_17821(event.tickDelta, livingEntity2.field_6220, livingEntity2.field_6283);
               h = headYaw - bodyYaw;
               j = class_3532.method_15393(h);
               if (j < -85.0F) {
                  j = -85.0F;
               }

               if (j >= 85.0F) {
                  j = 85.0F;
               }

               bodyYaw = headYaw - j;
               if (j * j > 2500.0F) {
                  bodyYaw = (float)((double)bodyYaw + (double)j * 0.2D);
               }

               h = headYaw - bodyYaw;
            }
         }

         pitch = class_3532.method_16439(event.tickDelta, livingEntity.field_6004, livingEntity.method_36455());
         j = renderer.method_4045(livingEntity, event.tickDelta);
         float limbDistance = 0.0F;
         float limbAngle = 0.0F;
         if (!livingEntity.method_5765() && livingEntity.method_5805()) {
            limbDistance = livingEntity.field_42108.method_48570(event.tickDelta);
            limbAngle = livingEntity.field_42108.method_48572(event.tickDelta);
            if (livingEntity.method_6109()) {
               limbAngle *= 3.0F;
            }

            if (limbDistance > 1.0F) {
               limbDistance = 1.0F;
            }
         }

         model.method_2816(livingEntity, limbAngle, limbDistance, event.tickDelta);
         model.method_2819(livingEntity, limbAngle, limbDistance, j, h, pitch);
         renderer.method_4058(livingEntity, matrices, j, bodyYaw, event.tickDelta, livingEntity.method_55693());
         matrices.method_22905(-1.0F, -1.0F, 1.0F);
         renderer.method_4042(livingEntity, matrices, event.tickDelta);
         matrices.method_22904(0.0D, -1.5010000467300415D, 0.0D);
         if (model instanceof class_4592) {
            class_4592 m = (class_4592)model;
            if (m.field_3448) {
               matrices.method_22903();
               float g;
               if (m.field_20915) {
                  g = 1.5F / m.field_20918;
                  matrices.method_22905(g, g, g);
               }

               matrices.method_22904(0.0D, (double)(m.field_20916 / 16.0F), (double)(m.field_20917 / 16.0F));
               class_572 mo;
               if (model instanceof class_572) {
                  mo = (class_572)model;
                  render(event.renderer, mo.field_3398);
               } else {
                  m.method_22946().forEach((modelPart) -> {
                     render(event.renderer, (class_630)modelPart);
                  });
               }

               matrices.method_22909();
               matrices.method_22903();
               g = 1.0F / m.field_20919;
               matrices.method_22905(g, g, g);
               matrices.method_22904(0.0D, (double)(m.field_20920 / 16.0F), 0.0D);
               if (model instanceof class_572) {
                  mo = (class_572)model;
                  render(event.renderer, mo.field_3391);
                  render(event.renderer, mo.field_27433);
                  render(event.renderer, mo.field_3401);
                  render(event.renderer, mo.field_3397);
                  render(event.renderer, mo.field_3392);
               } else {
                  m.method_22948().forEach((modelPart) -> {
                     render(event.renderer, (class_630)modelPart);
                  });
               }

               matrices.method_22909();
            } else if (model instanceof class_572) {
               class_572 mo = (class_572)model;
               render(event.renderer, mo.field_3398);
               render(event.renderer, mo.field_3391);
               render(event.renderer, mo.field_27433);
               render(event.renderer, mo.field_3401);
               render(event.renderer, mo.field_3397);
               render(event.renderer, mo.field_3392);
            } else {
               m.method_22946().forEach((modelPart) -> {
                  render(event.renderer, (class_630)modelPart);
               });
               m.method_22948().forEach((modelPart) -> {
                  render(event.renderer, (class_630)modelPart);
               });
            }
         } else if (model instanceof class_5597) {
            class_5597 m = (class_5597)model;
            render(event.renderer, m.method_32008());
         } else if (model instanceof class_4595) {
            class_4595 m = (class_4595)model;
            m.method_22960().forEach((modelPart) -> {
               render(event.renderer, (class_630)modelPart);
            });
         } else if (model instanceof class_578) {
            class_578 m = (class_578)model;
            if (m.field_3448) {
               matrices.method_22903();
               matrices.method_22905(0.71428573F, 0.64935064F, 0.7936508F);
               matrices.method_22904(0.0D, 1.3125D, 0.2199999988079071D);
               render(event.renderer, m.field_27443);
               matrices.method_22909();
               matrices.method_22903();
               matrices.method_22905(0.625F, 0.45454544F, 0.45454544F);
               matrices.method_22904(0.0D, 2.0625D, 0.0D);
               render(event.renderer, m.field_27444);
               matrices.method_22909();
               matrices.method_22903();
               matrices.method_22905(0.45454544F, 0.41322312F, 0.45454544F);
               matrices.method_22904(0.0D, 2.0625D, 0.0D);
               render(event.renderer, m.field_27445);
               render(event.renderer, m.field_27446);
               render(event.renderer, m.field_27447);
               render(event.renderer, m.field_27448);
               render(event.renderer, m.field_27449);
               render(event.renderer, m.field_27450);
               matrices.method_22909();
            } else {
               render(event.renderer, m.field_27443);
               render(event.renderer, m.field_27444);
               render(event.renderer, m.field_27445);
               render(event.renderer, m.field_27446);
               render(event.renderer, m.field_27447);
               render(event.renderer, m.field_27448);
               render(event.renderer, m.field_27449);
               render(event.renderer, m.field_27450);
            }
         } else if (model instanceof class_596) {
            class_596 m = (class_596)model;
            if (m.field_3448) {
               matrices.method_22903();
               matrices.method_22905(0.56666666F, 0.56666666F, 0.56666666F);
               matrices.method_22904(0.0D, 1.375D, 0.125D);
               render(event.renderer, m.field_27486);
               render(event.renderer, m.field_27488);
               render(event.renderer, m.field_27487);
               render(event.renderer, m.field_3530);
               matrices.method_22909();
               matrices.method_22903();
               matrices.method_22905(0.4F, 0.4F, 0.4F);
               matrices.method_22904(0.0D, 2.25D, 0.0D);
               render(event.renderer, m.field_27480);
               render(event.renderer, m.field_27481);
               render(event.renderer, m.field_27482);
               render(event.renderer, m.field_27483);
               render(event.renderer, m.field_3528);
               render(event.renderer, m.field_27484);
               render(event.renderer, m.field_27485);
               render(event.renderer, m.field_3524);
               matrices.method_22909();
            } else {
               matrices.method_22903();
               matrices.method_22905(0.6F, 0.6F, 0.6F);
               matrices.method_22904(0.0D, 1.0D, 0.0D);
               render(event.renderer, m.field_27480);
               render(event.renderer, m.field_27481);
               render(event.renderer, m.field_27482);
               render(event.renderer, m.field_27483);
               render(event.renderer, m.field_3528);
               render(event.renderer, m.field_27484);
               render(event.renderer, m.field_27485);
               render(event.renderer, m.field_27486);
               render(event.renderer, m.field_27487);
               render(event.renderer, m.field_27488);
               render(event.renderer, m.field_3524);
               render(event.renderer, m.field_3530);
               matrices.method_22909();
            }
         }
      }

      if (entityRenderer instanceof class_892) {
         class_892 renderer = (class_892)entityRenderer;
         class_1511 crystalEntity = (class_1511)entity;
         Chams chams = (Chams)Modules.get().get(Chams.class);
         boolean chamsEnabled = chams.isActive() && (Boolean)chams.crystals.get();
         matrices.method_22903();
         if (chamsEnabled) {
            j = (float)crystalEntity.field_7034 + event.tickDelta;
            pitch = class_3532.method_15374(j * 0.2F) / 2.0F + 0.5F;
            pitch = (pitch * pitch + pitch) * 0.4F * ((Double)chams.crystalsBounce.get()).floatValue();
            h = pitch - 1.4F;
         } else {
            h = class_892.method_23155(crystalEntity, event.tickDelta);
         }

         j = ((float)crystalEntity.field_7034 + event.tickDelta) * 3.0F;
         matrices.method_22903();
         if (chamsEnabled) {
            matrices.method_22905(2.0F * ((Double)chams.crystalsScale.get()).floatValue(), 2.0F * ((Double)chams.crystalsScale.get()).floatValue(), 2.0F * ((Double)chams.crystalsScale.get()).floatValue());
         } else {
            matrices.method_22905(2.0F, 2.0F, 2.0F);
         }

         matrices.method_22904(0.0D, -0.5D, 0.0D);
         if (crystalEntity.method_6836()) {
            render(event.renderer, renderer.field_21005);
         }

         if (chamsEnabled) {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j * ((Double)chams.crystalsRotationSpeed.get()).floatValue()));
         } else {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j));
         }

         matrices.method_22904(0.0D, (double)(1.5F + h / 2.0F), 0.0D);
         matrices.method_22907((new Quaternionf()).setAngleAxis(60.0F, class_892.field_21002, 0.0F, class_892.field_21002));
         if (!chamsEnabled || (Boolean)chams.renderFrame1.get()) {
            render(event.renderer, renderer.field_21004);
         }

         matrices.method_22905(0.875F, 0.875F, 0.875F);
         matrices.method_22907((new Quaternionf()).setAngleAxis(60.0F, class_892.field_21002, 0.0F, class_892.field_21002));
         if (chamsEnabled) {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j * ((Double)chams.crystalsRotationSpeed.get()).floatValue()));
         } else {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j));
         }

         if (!chamsEnabled || (Boolean)chams.renderFrame2.get()) {
            render(event.renderer, renderer.field_21004);
         }

         matrices.method_22905(0.875F, 0.875F, 0.875F);
         matrices.method_22907((new Quaternionf()).setAngleAxis(60.0F, class_892.field_21002, 0.0F, class_892.field_21002));
         if (chamsEnabled) {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j * ((Double)chams.crystalsRotationSpeed.get()).floatValue()));
         } else {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j));
         }

         if (!chamsEnabled || (Boolean)chams.renderCore.get()) {
            render(event.renderer, renderer.field_21003);
         }

         matrices.method_22909();
         matrices.method_22909();
      } else if (entityRenderer instanceof class_881) {
         class_881 renderer = (class_881)entityRenderer;
         class_1690 boatEntity = (class_1690)entity;
         matrices.method_22903();
         matrices.method_22904(0.0D, 0.375D, 0.0D);
         matrices.method_22907(class_7833.field_40716.rotationDegrees(180.0F - class_3532.method_16439(event.tickDelta, entity.field_5982, entity.method_36454())));
         bodyYaw = (float)boatEntity.method_54295() - event.tickDelta;
         headYaw = boatEntity.method_54294() - event.tickDelta;
         if (headYaw < 0.0F) {
            headYaw = 0.0F;
         }

         if (bodyYaw > 0.0F) {
            matrices.method_22907(class_7833.field_40714.rotationDegrees(class_3532.method_15374(bodyYaw) * bodyYaw * headYaw / 10.0F * (float)boatEntity.method_54296()));
         }

         h = boatEntity.method_7547(event.tickDelta);
         if (!class_3532.method_15347(h, 0.0F)) {
            matrices.method_22907((new Quaternionf()).setAngleAxis(boatEntity.method_7547(event.tickDelta), 1.0F, 0.0F, 1.0F));
         }

         class_4595<class_1690> boatEntityModel = (class_4595)((Pair)renderer.field_27758.get(boatEntity.method_47885())).getSecond();
         matrices.method_22905(-1.0F, -1.0F, 1.0F);
         matrices.method_22907(class_7833.field_40716.rotationDegrees(90.0F));
         boatEntityModel.method_2819(boatEntity, event.tickDelta, 0.0F, -0.1F, 0.0F, 0.0F);
         boatEntityModel.method_22960().forEach((modelPart) -> {
            render(event.renderer, modelPart);
         });
         if (!boatEntity.method_5869() && boatEntityModel instanceof class_7755) {
            class_7755 modelWithWaterPatch = (class_7755)boatEntityModel;
            render(event.renderer, modelWithWaterPatch.method_22954());
         }

         matrices.method_22909();
      } else if (entityRenderer instanceof class_916) {
         double dx = (entity.method_23317() - entity.field_6014) * (double)event.tickDelta;
         double dy = (entity.method_23318() - entity.field_6036) * (double)event.tickDelta;
         double dz = (entity.method_23321() - entity.field_5969) * (double)event.tickDelta;
         class_238 box = entity.method_5829();
         event.renderer.box(dx + box.field_1323, dy + box.field_1322, dz + box.field_1321, dx + box.field_1320, dy + box.field_1325, dz + box.field_1324, sideColor, lineColor, shapeMode, 0);
      }

      matrices.method_22909();
   }

   private static void render(Renderer3D renderer, class_630 part) {
      if (part.field_3665 && (!part.field_3663.isEmpty() || !part.field_3661.isEmpty())) {
         matrices.method_22903();
         part.method_22703(matrices);
         Iterator var2 = part.field_3663.iterator();

         while(var2.hasNext()) {
            class_628 cuboid = (class_628)var2.next();
            render(renderer, cuboid, offsetX, offsetY, offsetZ);
         }

         var2 = part.field_3661.values().iterator();

         while(var2.hasNext()) {
            class_630 child = (class_630)var2.next();
            render(renderer, child);
         }

         matrices.method_22909();
      }
   }

   private static void render(Renderer3D renderer, class_628 cuboid, double offsetX, double offsetY, double offsetZ) {
      Matrix4f matrix = matrices.method_23760().method_23761();
      class_593[] var9 = cuboid.field_3649;
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         class_593 quad = var9[var11];
         pos1.set(quad.field_3502[0].field_3605.x / 16.0F, quad.field_3502[0].field_3605.y / 16.0F, quad.field_3502[0].field_3605.z / 16.0F, 1.0F);
         pos1.mul(matrix);
         pos2.set(quad.field_3502[1].field_3605.x / 16.0F, quad.field_3502[1].field_3605.y / 16.0F, quad.field_3502[1].field_3605.z / 16.0F, 1.0F);
         pos2.mul(matrix);
         pos3.set(quad.field_3502[2].field_3605.x / 16.0F, quad.field_3502[2].field_3605.y / 16.0F, quad.field_3502[2].field_3605.z / 16.0F, 1.0F);
         pos3.mul(matrix);
         pos4.set(quad.field_3502[3].field_3605.x / 16.0F, quad.field_3502[3].field_3605.y / 16.0F, quad.field_3502[3].field_3605.z / 16.0F, 1.0F);
         pos4.mul(matrix);
         if (shapeMode.sides()) {
            renderer.triangles.quad(renderer.triangles.vec3(offsetX + (double)pos1.x, offsetY + (double)pos1.y, offsetZ + (double)pos1.z).color(sideColor).next(), renderer.triangles.vec3(offsetX + (double)pos2.x, offsetY + (double)pos2.y, offsetZ + (double)pos2.z).color(sideColor).next(), renderer.triangles.vec3(offsetX + (double)pos3.x, offsetY + (double)pos3.y, offsetZ + (double)pos3.z).color(sideColor).next(), renderer.triangles.vec3(offsetX + (double)pos4.x, offsetY + (double)pos4.y, offsetZ + (double)pos4.z).color(sideColor).next());
         }

         if (shapeMode.lines()) {
            renderer.line(offsetX + (double)pos1.x, offsetY + (double)pos1.y, offsetZ + (double)pos1.z, offsetX + (double)pos2.x, offsetY + (double)pos2.y, offsetZ + (double)pos2.z, lineColor);
            renderer.line(offsetX + (double)pos2.x, offsetY + (double)pos2.y, offsetZ + (double)pos2.z, offsetX + (double)pos3.x, offsetY + (double)pos3.y, offsetZ + (double)pos3.z, lineColor);
            renderer.line(offsetX + (double)pos3.x, offsetY + (double)pos3.y, offsetZ + (double)pos3.z, offsetX + (double)pos4.x, offsetY + (double)pos4.y, offsetZ + (double)pos4.z, lineColor);
            renderer.line(offsetX + (double)pos1.x, offsetY + (double)pos1.y, offsetZ + (double)pos1.z, offsetX + (double)pos1.x, offsetY + (double)pos1.y, offsetZ + (double)pos1.z, lineColor);
         }
      }

   }

   public static List<WireframeEntityRenderer.RenderablePart> cloneEntityForRendering(Render3DEvent event, class_1297 entity, class_243 offset) {
      List<WireframeEntityRenderer.RenderablePart> parts = new ArrayList();
      offsetX = class_3532.method_16436((double)event.tickDelta, entity.field_6038, entity.method_23317());
      offsetY = class_3532.method_16436((double)event.tickDelta, entity.field_5971, entity.method_23318());
      offsetZ = class_3532.method_16436((double)event.tickDelta, entity.field_5989, entity.method_23321());
      ((IVec3d)offset).set(offsetX, offsetY, offsetZ);
      matrices.method_22903();
      class_897<?> entityRenderer = MeteorClient.mc.method_1561().method_3953(entity);
      float bodyYaw;
      float headYaw;
      float h;
      float pitch;
      float j;
      if (entityRenderer instanceof class_922) {
         class_922 renderer = (class_922)entityRenderer;
         class_1309 livingEntity = (class_1309)entity;
         class_583<class_1309> model = renderer.method_4038();
         if (entityRenderer instanceof class_1007) {
            class_1007 r = (class_1007)entityRenderer;
            class_591<class_742> playerModel = (class_591)r.method_4038();
            playerModel.field_3400 = entity.method_18276();
            class_573 armPose = class_1007.method_4210((class_742)entity, class_1268.field_5808);
            class_573 armPose2 = class_1007.method_4210((class_742)entity, class_1268.field_5810);
            if (armPose.method_30156()) {
               armPose2 = livingEntity.method_6079().method_7960() ? class_573.field_3409 : class_573.field_3410;
            }

            if (livingEntity.method_6068() == class_1306.field_6183) {
               playerModel.field_3395 = armPose;
               playerModel.field_3399 = armPose2;
            } else {
               playerModel.field_3395 = armPose2;
               playerModel.field_3399 = armPose;
            }
         }

         model.field_3447 = livingEntity.method_6055(event.tickDelta);
         model.field_3449 = livingEntity.method_5765();
         model.field_3448 = livingEntity.method_6109();
         bodyYaw = class_3532.method_17821(event.tickDelta, livingEntity.field_6220, livingEntity.field_6283);
         headYaw = class_3532.method_17821(event.tickDelta, livingEntity.field_6259, livingEntity.field_6241);
         h = headYaw - bodyYaw;
         if (livingEntity.method_5765()) {
            class_1297 var13 = livingEntity.method_5854();
            if (var13 instanceof class_1309) {
               class_1309 livingEntity2 = (class_1309)var13;
               bodyYaw = class_3532.method_17821(event.tickDelta, livingEntity2.field_6220, livingEntity2.field_6283);
               h = headYaw - bodyYaw;
               j = class_3532.method_15393(h);
               if (j < -85.0F) {
                  j = -85.0F;
               }

               if (j >= 85.0F) {
                  j = 85.0F;
               }

               bodyYaw = headYaw - j;
               if (j * j > 2500.0F) {
                  bodyYaw = (float)((double)bodyYaw + (double)j * 0.2D);
               }

               h = headYaw - bodyYaw;
            }
         }

         pitch = class_3532.method_16439(event.tickDelta, livingEntity.field_6004, livingEntity.method_36455());
         j = renderer.method_4045(livingEntity, event.tickDelta);
         float limbDistance = 0.0F;
         float limbAngle = 0.0F;
         if (!livingEntity.method_5765() && livingEntity.method_5805()) {
            limbDistance = livingEntity.field_42108.method_48570(event.tickDelta);
            limbAngle = livingEntity.field_42108.method_48572(event.tickDelta);
            if (livingEntity.method_6109()) {
               limbAngle *= 3.0F;
            }

            if (limbDistance > 1.0F) {
               limbDistance = 1.0F;
            }
         }

         model.method_2816(livingEntity, limbAngle, limbDistance, event.tickDelta);
         model.method_2819(livingEntity, limbAngle, limbDistance, j, h, pitch);
         renderer.method_4058(livingEntity, matrices, j, bodyYaw, event.tickDelta, livingEntity.method_55693());
         matrices.method_22905(-1.0F, -1.0F, 1.0F);
         renderer.method_4042(livingEntity, matrices, event.tickDelta);
         matrices.method_22904(0.0D, -1.5010000467300415D, 0.0D);
         if (model instanceof class_4592) {
            class_4592 m = (class_4592)model;
            if (m.field_3448) {
               matrices.method_22903();
               float g;
               if (m.field_20915) {
                  g = 1.5F / m.field_20918;
                  matrices.method_22905(g, g, g);
               }

               matrices.method_22904(0.0D, (double)(m.field_20916 / 16.0F), (double)(m.field_20917 / 16.0F));
               class_572 mo;
               if (model instanceof class_572) {
                  mo = (class_572)model;
                  cloneRenderParts(parts, mo.field_3398);
               } else {
                  m.method_22946().forEach((modelPart) -> {
                     cloneRenderParts(parts, (class_630)modelPart);
                  });
               }

               matrices.method_22909();
               matrices.method_22903();
               g = 1.0F / m.field_20919;
               matrices.method_22905(g, g, g);
               matrices.method_22904(0.0D, (double)(m.field_20920 / 16.0F), 0.0D);
               if (model instanceof class_572) {
                  mo = (class_572)model;
                  cloneRenderParts(parts, mo.field_3391);
                  cloneRenderParts(parts, mo.field_27433);
                  cloneRenderParts(parts, mo.field_3401);
                  cloneRenderParts(parts, mo.field_3397);
                  cloneRenderParts(parts, mo.field_3392);
               } else {
                  m.method_22948().forEach((modelPart) -> {
                     cloneRenderParts(parts, (class_630)modelPart);
                  });
               }

               matrices.method_22909();
            } else if (model instanceof class_572) {
               class_572 mo = (class_572)model;
               cloneRenderParts(parts, mo.field_3398);
               cloneRenderParts(parts, mo.field_3391);
               cloneRenderParts(parts, mo.field_27433);
               cloneRenderParts(parts, mo.field_3401);
               cloneRenderParts(parts, mo.field_3397);
               cloneRenderParts(parts, mo.field_3392);
            } else {
               m.method_22946().forEach((modelPart) -> {
                  cloneRenderParts(parts, (class_630)modelPart);
               });
               m.method_22948().forEach((modelPart) -> {
                  cloneRenderParts(parts, (class_630)modelPart);
               });
            }
         } else if (model instanceof class_5597) {
            class_5597 m = (class_5597)model;
            cloneRenderParts(parts, m.method_32008());
         } else if (model instanceof class_4595) {
            class_4595 m = (class_4595)model;
            m.method_22960().forEach((modelPart) -> {
               cloneRenderParts(parts, (class_630)modelPart);
            });
         } else if (model instanceof class_578) {
            class_578 m = (class_578)model;
            if (m.field_3448) {
               matrices.method_22903();
               matrices.method_22905(0.71428573F, 0.64935064F, 0.7936508F);
               matrices.method_22904(0.0D, 1.3125D, 0.2199999988079071D);
               cloneRenderParts(parts, m.field_27443);
               matrices.method_22909();
               matrices.method_22903();
               matrices.method_22905(0.625F, 0.45454544F, 0.45454544F);
               matrices.method_22904(0.0D, 2.0625D, 0.0D);
               cloneRenderParts(parts, m.field_27444);
               matrices.method_22909();
               matrices.method_22903();
               matrices.method_22905(0.45454544F, 0.41322312F, 0.45454544F);
               matrices.method_22904(0.0D, 2.0625D, 0.0D);
               cloneRenderParts(parts, m.field_27445);
               cloneRenderParts(parts, m.field_27446);
               cloneRenderParts(parts, m.field_27447);
               cloneRenderParts(parts, m.field_27448);
               cloneRenderParts(parts, m.field_27449);
               cloneRenderParts(parts, m.field_27450);
               matrices.method_22909();
            } else {
               cloneRenderParts(parts, m.field_27443);
               cloneRenderParts(parts, m.field_27444);
               cloneRenderParts(parts, m.field_27445);
               cloneRenderParts(parts, m.field_27446);
               cloneRenderParts(parts, m.field_27447);
               cloneRenderParts(parts, m.field_27448);
               cloneRenderParts(parts, m.field_27449);
               cloneRenderParts(parts, m.field_27450);
            }
         } else if (model instanceof class_596) {
            class_596 m = (class_596)model;
            if (m.field_3448) {
               matrices.method_22903();
               matrices.method_22905(0.56666666F, 0.56666666F, 0.56666666F);
               matrices.method_22904(0.0D, 1.375D, 0.125D);
               cloneRenderParts(parts, m.field_27486);
               cloneRenderParts(parts, m.field_27488);
               cloneRenderParts(parts, m.field_27487);
               cloneRenderParts(parts, m.field_3530);
               matrices.method_22909();
               matrices.method_22903();
               matrices.method_22905(0.4F, 0.4F, 0.4F);
               matrices.method_22904(0.0D, 2.25D, 0.0D);
               cloneRenderParts(parts, m.field_27480);
               cloneRenderParts(parts, m.field_27481);
               cloneRenderParts(parts, m.field_27482);
               cloneRenderParts(parts, m.field_27483);
               cloneRenderParts(parts, m.field_3528);
               cloneRenderParts(parts, m.field_27484);
               cloneRenderParts(parts, m.field_27485);
               cloneRenderParts(parts, m.field_3524);
               matrices.method_22909();
            } else {
               matrices.method_22903();
               matrices.method_22905(0.6F, 0.6F, 0.6F);
               matrices.method_22904(0.0D, 1.0D, 0.0D);
               cloneRenderParts(parts, m.field_27480);
               cloneRenderParts(parts, m.field_27481);
               cloneRenderParts(parts, m.field_27482);
               cloneRenderParts(parts, m.field_27483);
               cloneRenderParts(parts, m.field_3528);
               cloneRenderParts(parts, m.field_27484);
               cloneRenderParts(parts, m.field_27485);
               cloneRenderParts(parts, m.field_27486);
               cloneRenderParts(parts, m.field_27487);
               cloneRenderParts(parts, m.field_27488);
               cloneRenderParts(parts, m.field_3524);
               cloneRenderParts(parts, m.field_3530);
               matrices.method_22909();
            }
         }
      }

      if (entityRenderer instanceof class_892) {
         class_892 renderer = (class_892)entityRenderer;
         class_1511 crystalEntity = (class_1511)entity;
         Chams chams = (Chams)Modules.get().get(Chams.class);
         boolean chamsEnabled = chams.isActive() && (Boolean)chams.crystals.get();
         matrices.method_22903();
         if (chamsEnabled) {
            j = (float)crystalEntity.field_7034 + event.tickDelta;
            pitch = class_3532.method_15374(j * 0.2F) / 2.0F + 0.5F;
            pitch = (pitch * pitch + pitch) * 0.4F * ((Double)chams.crystalsBounce.get()).floatValue();
            h = pitch - 1.4F;
         } else {
            h = class_892.method_23155(crystalEntity, event.tickDelta);
         }

         j = ((float)crystalEntity.field_7034 + event.tickDelta) * 3.0F;
         matrices.method_22903();
         if (chamsEnabled) {
            matrices.method_22905(2.0F * ((Double)chams.crystalsScale.get()).floatValue(), 2.0F * ((Double)chams.crystalsScale.get()).floatValue(), 2.0F * ((Double)chams.crystalsScale.get()).floatValue());
         } else {
            matrices.method_22905(2.0F, 2.0F, 2.0F);
         }

         matrices.method_22904(0.0D, -0.5D, 0.0D);
         if (crystalEntity.method_6836()) {
            cloneRenderParts(parts, renderer.field_21005);
         }

         if (chamsEnabled) {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j * ((Double)chams.crystalsRotationSpeed.get()).floatValue()));
         } else {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j));
         }

         matrices.method_22904(0.0D, (double)(1.5F + h / 2.0F), 0.0D);
         matrices.method_22907((new Quaternionf()).setAngleAxis(60.0F, class_892.field_21002, 0.0F, class_892.field_21002));
         if (!chamsEnabled || (Boolean)chams.renderFrame1.get()) {
            cloneRenderParts(parts, renderer.field_21004);
         }

         matrices.method_22905(0.875F, 0.875F, 0.875F);
         matrices.method_22907((new Quaternionf()).setAngleAxis(60.0F, class_892.field_21002, 0.0F, class_892.field_21002));
         if (chamsEnabled) {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j * ((Double)chams.crystalsRotationSpeed.get()).floatValue()));
         } else {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j));
         }

         if (!chamsEnabled || (Boolean)chams.renderFrame2.get()) {
            cloneRenderParts(parts, renderer.field_21004);
         }

         matrices.method_22905(0.875F, 0.875F, 0.875F);
         matrices.method_22907((new Quaternionf()).setAngleAxis(60.0F, class_892.field_21002, 0.0F, class_892.field_21002));
         if (chamsEnabled) {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j * ((Double)chams.crystalsRotationSpeed.get()).floatValue()));
         } else {
            matrices.method_22907(class_7833.field_40716.rotationDegrees(j));
         }

         if (!chamsEnabled || (Boolean)chams.renderCore.get()) {
            cloneRenderParts(parts, renderer.field_21003);
         }

         matrices.method_22909();
         matrices.method_22909();
      } else if (entityRenderer instanceof class_881) {
         class_881 renderer = (class_881)entityRenderer;
         class_1690 boatEntity = (class_1690)entity;
         matrices.method_22903();
         matrices.method_22904(0.0D, 0.375D, 0.0D);
         matrices.method_22907(class_7833.field_40716.rotationDegrees(180.0F - class_3532.method_16439(event.tickDelta, entity.field_5982, entity.method_36454())));
         bodyYaw = (float)boatEntity.method_54295() - event.tickDelta;
         headYaw = boatEntity.method_54294() - event.tickDelta;
         if (headYaw < 0.0F) {
            headYaw = 0.0F;
         }

         if (bodyYaw > 0.0F) {
            matrices.method_22907(class_7833.field_40714.rotationDegrees(class_3532.method_15374(bodyYaw) * bodyYaw * headYaw / 10.0F * (float)boatEntity.method_54296()));
         }

         h = boatEntity.method_7547(event.tickDelta);
         if (!class_3532.method_15347(h, 0.0F)) {
            matrices.method_22907((new Quaternionf()).setAngleAxis(boatEntity.method_7547(event.tickDelta), 1.0F, 0.0F, 1.0F));
         }

         class_4595<class_1690> boatEntityModel = (class_4595)((Pair)renderer.field_27758.get(boatEntity.method_47885())).getSecond();
         matrices.method_22905(-1.0F, -1.0F, 1.0F);
         matrices.method_22907(class_7833.field_40716.rotationDegrees(90.0F));
         boatEntityModel.method_2819(boatEntity, event.tickDelta, 0.0F, -0.1F, 0.0F, 0.0F);
         boatEntityModel.method_22960().forEach((modelPart) -> {
            cloneRenderParts(parts, modelPart);
         });
         if (!boatEntity.method_5869() && boatEntityModel instanceof class_7755) {
            class_7755 modelWithWaterPatch = (class_7755)boatEntityModel;
            cloneRenderParts(parts, modelWithWaterPatch.method_22954());
         }

         matrices.method_22909();
      }

      matrices.method_22909();
      return parts;
   }

   private static void cloneRenderParts(List<WireframeEntityRenderer.RenderablePart> list, class_630 part) {
      if (part.field_3665 && (!part.field_3663.isEmpty() || !part.field_3661.isEmpty())) {
         matrices.method_22903();
         part.method_22703(matrices);
         Iterator var2 = part.field_3663.iterator();

         while(var2.hasNext()) {
            class_628 cuboid = (class_628)var2.next();
            cloneRenderCuboids(list, cuboid);
         }

         var2 = part.field_3661.values().iterator();

         while(var2.hasNext()) {
            class_630 child = (class_630)var2.next();
            cloneRenderParts(list, child);
         }

         matrices.method_22909();
      }
   }

   private static void cloneRenderCuboids(List<WireframeEntityRenderer.RenderablePart> list, class_628 cuboid) {
      WireframeEntityRenderer.RenderablePart part = new WireframeEntityRenderer.RenderablePart();

      try {
         part.matrix = (Matrix4f)matrices.method_23760().method_23761().clone();
      } catch (CloneNotSupportedException var4) {
      }

      part.cuboid = cuboid;
      list.add(part);
   }

   public static void render(Render3DEvent event, class_243 offset, List<WireframeEntityRenderer.RenderablePart> parts, double scale, Color sideColor, Color lineColor, ShapeMode shapeMode) {
      WireframeEntityRenderer.sideColor = sideColor;
      WireframeEntityRenderer.lineColor = lineColor;
      WireframeEntityRenderer.shapeMode = shapeMode;
      matrices.method_22903();
      matrices.method_22905((float)scale, (float)scale, (float)scale);
      Iterator var8 = parts.iterator();

      while(var8.hasNext()) {
         WireframeEntityRenderer.RenderablePart part = (WireframeEntityRenderer.RenderablePart)var8.next();
         matrices.method_22903();
         matrices.method_34425(part.matrix);
         renderPart(event.renderer, offset, part);
         matrices.method_22909();
      }

      matrices.method_22909();
   }

   private static void renderPart(Renderer3D renderer, class_243 offset, WireframeEntityRenderer.RenderablePart part) {
      Matrix4f matrix = matrices.method_23760().method_23761();
      class_628 cuboid = part.cuboid;
      double offsetX = offset.field_1352;
      double offsetY = offset.field_1351;
      double offsetZ = offset.field_1350;
      class_593[] var11 = cuboid.field_3649;
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         class_593 quad = var11[var13];
         pos1.set(quad.field_3502[0].field_3605.x / 16.0F, quad.field_3502[0].field_3605.y / 16.0F, quad.field_3502[0].field_3605.z / 16.0F, 1.0F);
         pos1.mul(matrix);
         pos2.set(quad.field_3502[1].field_3605.x / 16.0F, quad.field_3502[1].field_3605.y / 16.0F, quad.field_3502[1].field_3605.z / 16.0F, 1.0F);
         pos2.mul(matrix);
         pos3.set(quad.field_3502[2].field_3605.x / 16.0F, quad.field_3502[2].field_3605.y / 16.0F, quad.field_3502[2].field_3605.z / 16.0F, 1.0F);
         pos3.mul(matrix);
         pos4.set(quad.field_3502[3].field_3605.x / 16.0F, quad.field_3502[3].field_3605.y / 16.0F, quad.field_3502[3].field_3605.z / 16.0F, 1.0F);
         pos4.mul(matrix);
         if (shapeMode.sides()) {
            renderer.triangles.quad(renderer.triangles.vec3(offsetX + (double)pos1.x, offsetY + (double)pos1.y, offsetZ + (double)pos1.z).color(sideColor).next(), renderer.triangles.vec3(offsetX + (double)pos2.x, offsetY + (double)pos2.y, offsetZ + (double)pos2.z).color(sideColor).next(), renderer.triangles.vec3(offsetX + (double)pos3.x, offsetY + (double)pos3.y, offsetZ + (double)pos3.z).color(sideColor).next(), renderer.triangles.vec3(offsetX + (double)pos4.x, offsetY + (double)pos4.y, offsetZ + (double)pos4.z).color(sideColor).next());
         }

         if (shapeMode.lines()) {
            renderer.line(offsetX + (double)pos1.x, offsetY + (double)pos1.y, offsetZ + (double)pos1.z, offsetX + (double)pos2.x, offsetY + (double)pos2.y, offsetZ + (double)pos2.z, lineColor);
            renderer.line(offsetX + (double)pos2.x, offsetY + (double)pos2.y, offsetZ + (double)pos2.z, offsetX + (double)pos3.x, offsetY + (double)pos3.y, offsetZ + (double)pos3.z, lineColor);
            renderer.line(offsetX + (double)pos3.x, offsetY + (double)pos3.y, offsetZ + (double)pos3.z, offsetX + (double)pos4.x, offsetY + (double)pos4.y, offsetZ + (double)pos4.z, lineColor);
            renderer.line(offsetX + (double)pos1.x, offsetY + (double)pos1.y, offsetZ + (double)pos1.z, offsetX + (double)pos1.x, offsetY + (double)pos1.y, offsetZ + (double)pos1.z, lineColor);
         }
      }

   }

   public static void renderHeldItem(Render3DEvent event, class_1309 entity, Renderer3D renderer) {
      if (entity instanceof class_1657) {
         class_1657 player = (class_1657)entity;
         class_1268 hand = player.method_6068() == class_1306.field_6183 ? class_1268.field_5808 : class_1268.field_5810;
         class_1799 stack = player.method_5998(hand);
         if (!stack.method_7960()) {
            matrices.method_22903();
            matrices.method_22904(0.0D, -1.501D, 0.0D);
            float bodyYaw = class_3532.method_17821(event.tickDelta, entity.field_6220, entity.field_6283);
            float headYaw = class_3532.method_17821(event.tickDelta, entity.field_6259, entity.field_6241);
            float pitch = class_3532.method_16439(event.tickDelta, entity.field_6004, entity.method_36455());
            matrices.method_22907(class_7833.field_40716.rotationDegrees(headYaw - bodyYaw));
            matrices.method_22907(class_7833.field_40714.rotationDegrees(pitch));
            if (hand == class_1268.field_5808) {
               matrices.method_22904(0.4D, 0.2D, -0.4D);
            } else {
               matrices.method_22904(-0.4D, 0.2D, -0.4D);
            }

            matrices.method_22905(0.5F, 0.5F, 0.5F);
            class_238 box = new class_238(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D);
            renderer.box(box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324, sideColor, lineColor, shapeMode, 0);
            matrices.method_22909();
         }
      }
   }

   public static class RenderablePart {
      public Matrix4f matrix;
      public class_628 cuboid;
   }
}
