package meteordevelopment.meteorclient.mixininterface;

import net.minecraft.class_2382;
import org.joml.Vector3d;

public interface IVec3d {
   void set(double var1, double var3, double var5);

   default void set(class_2382 vec) {
      this.set((double)vec.method_10263(), (double)vec.method_10264(), (double)vec.method_10260());
   }

   default void set(Vector3d vec) {
      this.set(vec.x, vec.y, vec.z);
   }

   void setXZ(double var1, double var3);

   void setY(double var1);
}
