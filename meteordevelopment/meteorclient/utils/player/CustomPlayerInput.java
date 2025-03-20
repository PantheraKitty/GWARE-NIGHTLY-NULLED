package meteordevelopment.meteorclient.utils.player;

import net.minecraft.class_744;

public class CustomPlayerInput extends class_744 {
   public void method_3129(boolean slowDown, float f) {
      this.field_3905 = this.field_3910 == this.field_3909 ? 0.0F : (this.field_3910 ? 1.0F : -1.0F);
      this.field_3907 = this.field_3908 == this.field_3906 ? 0.0F : (this.field_3908 ? 1.0F : -1.0F);
      if (this.field_3903) {
         this.field_3905 = (float)((double)this.field_3905 * 0.3D);
         this.field_3907 = (float)((double)this.field_3907 * 0.3D);
      }

   }

   public void stop() {
      this.field_3910 = false;
      this.field_3909 = false;
      this.field_3906 = false;
      this.field_3908 = false;
      this.field_3904 = false;
      this.field_3903 = false;
   }
}
