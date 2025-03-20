package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.combat.autocrystal.AutoCrystalUtil;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_2595;
import net.minecraft.class_2646;
import net.minecraft.class_2848;
import net.minecraft.class_2885;
import net.minecraft.class_2886;
import net.minecraft.class_3965;
import net.minecraft.class_2848.class_2849;

public class Waterlogger extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> placeTime;
   private long lastPlaceTimeMS;
   private List<class_2338> placePoses;

   public Waterlogger() {
      super(Categories.World, "waterlogger", "Places water buckets in chests near you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.placeTime = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-time")).description("Time between places")).defaultValue(0.06D).min(0.0D).max(0.5D).build());
      this.lastPlaceTimeMS = 0L;
      this.placePoses = new ArrayList();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      int r = 5;
      long currentTime = System.currentTimeMillis();
      if ((double)(currentTime - this.lastPlaceTimeMS) / 1000.0D > (Double)this.placeTime.get()) {
         this.lastPlaceTimeMS = currentTime;
         if (!this.mc.field_1724.method_6115()) {
            this.placePoses.clear();
            class_2338 eyePos = class_2338.method_49638(this.mc.field_1724.method_33571());

            for(int y = r; y > -r; --y) {
               for(int x = -r; x <= r; ++x) {
                  for(int z = -r; z <= r; ++z) {
                     class_2338 pos = eyePos.method_10069(x, y, z);
                     if (this.placePoses.isEmpty() && pos.method_46558().method_24802(eyePos.method_46558(), 5.0D) && this.mc.field_1687.method_8320(pos).method_26227().method_15769()) {
                        class_2586 blockEntity = this.mc.field_1687.method_8321(pos);
                        if (blockEntity != null && (blockEntity instanceof class_2595 || blockEntity instanceof class_2646)) {
                           this.placePoses.add(pos);
                        }
                     }
                  }
               }
            }

            if (!this.placePoses.isEmpty()) {
               this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12979));
               this.mc.field_1724.method_5660(true);
               Iterator var11 = this.placePoses.iterator();

               while(var11.hasNext()) {
                  class_2338 pos = (class_2338)var11.next();
                  class_3965 hitResult = AutoCrystalUtil.getPlaceBlockHitResult(pos);
                  if (MeteorClient.SWAP.beginSwap(class_1802.field_8705, true)) {
                     MeteorClient.ROTATION.snapAt(hitResult.method_17784());
                     this.mc.method_1562().method_52787(new class_2885(class_1268.field_5808, hitResult, this.mc.field_1687.method_41925().method_41937().method_41942()));
                     this.mc.method_1562().method_52787(new class_2886(class_1268.field_5808, this.mc.field_1687.method_41925().method_41937().method_41942(), MeteorClient.ROTATION.lastYaw, MeteorClient.ROTATION.lastPitch));
                     MeteorClient.SWAP.endSwap(true);
                  }
               }

               this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12984));
               this.mc.field_1724.method_5660(false);
            }
         }
      }
   }
}
