package meteordevelopment.meteorclient.mixininterface;

import net.minecraft.class_2338;

public interface IBox {
   void expand(double var1);

   void set(double var1, double var3, double var5, double var7, double var9, double var11);

   default void set(class_2338 pos) {
      this.set((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1));
   }
}
