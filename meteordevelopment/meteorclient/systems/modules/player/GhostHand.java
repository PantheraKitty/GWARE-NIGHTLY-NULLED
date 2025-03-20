package meteordevelopment.meteorclient.systems.modules.player;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3965;

public class GhostHand extends Module {
   private final Set<class_2338> posList = new ObjectOpenHashSet();

   public GhostHand() {
      super(Categories.Player, "ghost-hand", "Opens containers through walls.");
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1690.field_1904.method_1434() && !this.mc.field_1724.method_5715()) {
         if (!this.mc.field_1687.method_8320(class_2338.method_49638(this.mc.field_1724.method_5745(this.mc.field_1724.method_55754(), this.mc.method_60646().method_60637(true), false).method_17784())).method_31709()) {
            class_243 direction = (new class_243(0.0D, 0.0D, 0.1D)).method_1037(-((float)Math.toRadians((double)this.mc.field_1724.method_36455()))).method_1024(-((float)Math.toRadians((double)this.mc.field_1724.method_36454())));
            this.posList.clear();

            for(int i = 1; (double)i < this.mc.field_1724.method_55754() * 10.0D; ++i) {
               class_2338 pos = class_2338.method_49638(this.mc.field_1724.method_5836(this.mc.method_60646().method_60637(true)).method_1019(direction.method_1021((double)i)));
               if (!this.posList.contains(pos)) {
                  this.posList.add(pos);
                  if (this.mc.field_1687.method_8320(pos).method_31709()) {
                     this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5808, new class_3965(new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D), class_2350.field_11036, pos, true));
                     return;
                  }
               }
            }

         }
      }
   }
}
