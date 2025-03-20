package meteordevelopment.meteorclient.systems.modules.render;

import java.lang.reflect.Field;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.class_310;
import net.minecraft.class_7172;
import org.lwjgl.opengl.GL11;

public class FOVChanger extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> fov;
   private final class_310 mc;

   public FOVChanger() {
      super(Categories.Render, "fov-changer", "Allows modification of the FOV and aspect ratio.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.fov = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fov")).description("The FOV value.")).defaultValue(90.0D).range(30.0D, 1000.0D).sliderRange(30.0D, 360.0D).onChanged((value) -> {
         this.updateFOV();
      })).build());
      this.mc = class_310.method_1551();
   }

   public void onDeactivate() {
      this.setFOVUnrestricted(90);
      GL11.glViewport(0, 0, this.mc.method_22683().method_4480(), this.mc.method_22683().method_4507());
   }

   private void updateFOV() {
      if (this.isActive()) {
         double newFOV = (Double)this.fov.get();
         if (newFOV < 30.0D) {
            newFOV = 30.0D;
         }

         if (newFOV > 1000.0D) {
            newFOV = 1000.0D;
         }

         this.setFOVUnrestricted((int)newFOV);
      }

   }

   private void setFOVUnrestricted(int fov) {
      try {
         Field fovField = this.mc.field_1690.getClass().getDeclaredField("fov");
         fovField.setAccessible(true);
         class_7172<Integer> fovOption = (class_7172)fovField.get(this.mc.field_1690);
         Field valueField = class_7172.class.getDeclaredField("value");
         valueField.setAccessible(true);
         valueField.set(fovOption, fov);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }
}
