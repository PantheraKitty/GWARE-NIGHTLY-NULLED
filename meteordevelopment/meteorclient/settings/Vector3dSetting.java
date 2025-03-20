package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;
import net.minecraft.class_2487;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class Vector3dSetting extends Setting<Vector3d> {
   public final double min;
   public final double max;
   public final double sliderMin;
   public final double sliderMax;
   public final boolean onSliderRelease;
   public final int decimalPlaces;
   public final boolean noSlider;

   public Vector3dSetting(String name, String description, Vector3d defaultValue, Consumer<Vector3d> onChanged, Consumer<Setting<Vector3d>> onModuleActivated, IVisible visible, double min, double max, double sliderMin, double sliderMax, boolean onSliderRelease, int decimalPlaces, boolean noSlider) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.min = min;
      this.max = max;
      this.sliderMin = sliderMin;
      this.sliderMax = sliderMax;
      this.decimalPlaces = decimalPlaces;
      this.onSliderRelease = onSliderRelease;
      this.noSlider = noSlider;
   }

   public boolean set(double x, double y, double z) {
      ((Vector3d)this.value).set(x, y, z);
      return super.set((Vector3d)this.value);
   }

   protected void resetImpl() {
      if (this.value == null) {
         this.value = new Vector3d();
      }

      ((Vector3d)this.value).set((Vector3dc)this.defaultValue);
   }

   protected Vector3d parseImpl(String str) {
      try {
         String[] strs = str.split(" ");
         return new Vector3d(Double.parseDouble(strs[0]), Double.parseDouble(strs[1]), Double.parseDouble(strs[2]));
      } catch (NumberFormatException | IndexOutOfBoundsException var3) {
         return null;
      }
   }

   protected boolean isValueValid(Vector3d value) {
      return value.x >= this.min && value.x <= this.max && value.y >= this.min && value.y <= this.max && value.z >= this.min && value.z <= this.max;
   }

   protected class_2487 save(class_2487 tag) {
      class_2487 valueTag = new class_2487();
      valueTag.method_10549("x", ((Vector3d)this.get()).x);
      valueTag.method_10549("y", ((Vector3d)this.get()).y);
      valueTag.method_10549("z", ((Vector3d)this.get()).z);
      tag.method_10566("value", valueTag);
      return tag;
   }

   protected Vector3d load(class_2487 tag) {
      class_2487 valueTag = tag.method_10562("value");
      this.set(valueTag.method_10574("x"), valueTag.method_10574("y"), valueTag.method_10574("z"));
      return (Vector3d)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Vector3dSetting.Builder, Vector3d, Vector3dSetting> {
      public double min = Double.NEGATIVE_INFINITY;
      public double max = Double.POSITIVE_INFINITY;
      public double sliderMin = 0.0D;
      public double sliderMax = 10.0D;
      public boolean onSliderRelease = false;
      public int decimalPlaces = 3;
      public boolean noSlider = false;

      public Builder() {
         super(new Vector3d());
      }

      public Vector3dSetting.Builder defaultValue(Vector3d defaultValue) {
         ((Vector3d)this.defaultValue).set(defaultValue);
         return this;
      }

      public Vector3dSetting.Builder defaultValue(double x, double y, double z) {
         ((Vector3d)this.defaultValue).set(x, y, z);
         return this;
      }

      public Vector3dSetting.Builder min(double min) {
         this.min = min;
         return this;
      }

      public Vector3dSetting.Builder max(double max) {
         this.max = max;
         return this;
      }

      public Vector3dSetting.Builder range(double min, double max) {
         this.min = Math.min(min, max);
         this.max = Math.max(min, max);
         return this;
      }

      public Vector3dSetting.Builder sliderMin(double min) {
         this.sliderMin = min;
         return this;
      }

      public Vector3dSetting.Builder sliderMax(double max) {
         this.sliderMax = max;
         return this;
      }

      public Vector3dSetting.Builder sliderRange(double min, double max) {
         this.sliderMin = min;
         this.sliderMax = max;
         return this;
      }

      public Vector3dSetting.Builder onSliderRelease() {
         this.onSliderRelease = true;
         return this;
      }

      public Vector3dSetting.Builder decimalPlaces(int decimalPlaces) {
         this.decimalPlaces = decimalPlaces;
         return this;
      }

      public Vector3dSetting.Builder noSlider() {
         this.noSlider = true;
         return this;
      }

      public Vector3dSetting build() {
         return new Vector3dSetting(this.name, this.description, (Vector3d)this.defaultValue, this.onChanged, this.onModuleActivated, this.visible, this.min, this.max, this.sliderMin, this.sliderMax, this.onSliderRelease, this.decimalPlaces, this.noSlider);
      }
   }
}
