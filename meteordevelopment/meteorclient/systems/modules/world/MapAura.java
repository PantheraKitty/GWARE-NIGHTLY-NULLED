package meteordevelopment.meteorclient.systems.modules.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1533;
import net.minecraft.class_1802;
import net.minecraft.class_1806;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_5575;
import net.minecraft.class_2338.class_2339;

public class MapAura extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> placeRange;
   private final Setting<Double> placeDelay;
   private final class_2339 mutablePos;
   private final Map<class_2338, Long> timeOfLastPlace;
   private final Map<Integer, Long> timeOfLastMapInteract;

   public MapAura() {
      super(Categories.World, "map-aura", "Places maps and item frames on every surface");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.placeRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("How far you can reach")).defaultValue(4.0D).min(0.0D).sliderMax(6.0D).build());
      this.placeDelay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-delay")).description("How many seconds to wait between placing in the same spot")).defaultValue(0.2D).min(0.0D).sliderMax(2.0D).build());
      this.mutablePos = new class_2339();
      this.timeOfLastPlace = new HashMap();
      this.timeOfLastMapInteract = new HashMap();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      FindItemResult itemFrameResult = InvUtils.findInHotbar(class_1802.field_8143);
      if (itemFrameResult.found()) {
         InvUtils.swap(itemFrameResult.slot(), true);
         this.placeNextItemFrame();
         InvUtils.swapBack();
      }

      FindItemResult mapItemResult = InvUtils.findInHotbar((item) -> {
         return item.method_7909() instanceof class_1806 && item.method_7947() > 1;
      });
      if (mapItemResult.found()) {
         InvUtils.swap(mapItemResult.slot(), true);
         this.placeNextMap();
         InvUtils.swapBack();
      }

   }

   private boolean placeNextItemFrame() {
      long currentTime = System.currentTimeMillis();
      int r = (int)Math.floor((Double)this.placeRange.get());
      class_2338 eyePos = class_2338.method_49638(this.mc.field_1724.method_33571());
      int ex = eyePos.method_10263();
      int ey = eyePos.method_10264();
      int ez = eyePos.method_10260();

      for(int x = -r; x <= r; ++x) {
         for(int y = -r; y <= r; ++y) {
            for(int z = -r; z <= r; ++z) {
               class_2338 blockPos = this.mutablePos.method_10103(ex + x, ey + y, ez + z);
               class_2680 state = this.mc.field_1687.method_8320(blockPos);
               if (!state.method_26215() && (!this.timeOfLastPlace.containsKey(blockPos) || !(((double)currentTime - (double)(Long)this.timeOfLastPlace.get(blockPos)) / 1000.0D < (Double)this.placeDelay.get()))) {
                  class_2350[] var13 = class_2350.values();
                  int var14 = var13.length;

                  for(int var15 = 0; var15 < var14; ++var15) {
                     class_2350 dir = var13[var15];
                     class_2338 neighbour = blockPos.method_10093(dir);
                     if (this.mc.field_1687.method_8320(neighbour).method_26215() && class_1937.method_25953(neighbour) && neighbour.method_10264() >= -64) {
                        class_243 hitPos = blockPos.method_46558().method_1031((double)dir.method_10148() * 0.5D, (double)dir.method_10164() * 0.5D, (double)dir.method_10165() * 0.5D);
                        List<class_1533> entities = this.mc.field_1687.method_18023(class_5575.method_31795(class_1533.class), class_238.method_30048(hitPos, 0.1D, 0.1D, 0.1D), (entity) -> {
                           return true;
                        });
                        if (entities.isEmpty()) {
                           this.mc.method_1562().method_52787(new class_2885(class_1268.field_5808, new class_3965(hitPos, dir, blockPos, false), this.mc.field_1687.method_41925().method_41937().method_41942()));
                           this.timeOfLastPlace.put(blockPos, currentTime);
                           return true;
                        }
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private boolean placeNextMap() {
      List<class_1533> entities = this.mc.field_1687.method_18023(class_5575.method_31795(class_1533.class), class_238.method_30048(this.mc.field_1724.method_33571(), (Double)this.placeRange.get() * 2.0D, (Double)this.placeRange.get() * 2.0D, (Double)this.placeRange.get() * 2.0D), this::checkEntity);
      if (entities.isEmpty()) {
         return false;
      } else {
         long currentTime = System.currentTimeMillis();
         class_1533 entity = (class_1533)entities.getFirst();
         MeteorClient.ROTATION.requestRotation(this.getClosestPointOnBox(entity.method_5829(), this.mc.field_1724.method_33571()), 5.0D);
         if (!MeteorClient.ROTATION.lookingAt(entity.method_5829())) {
            return false;
         } else if (this.timeOfLastMapInteract.containsKey(entity.method_5628()) && ((double)currentTime - (double)(Long)this.timeOfLastMapInteract.get(entity.method_5628())) / 1000.0D < (Double)this.placeDelay.get()) {
            return false;
         } else {
            class_3966 entityHitResult = new class_3966(entity, this.getClosestPointOnBox(entity.method_5829(), this.mc.field_1724.method_33571()));
            class_1269 actionResult = this.mc.field_1761.method_2917(this.mc.field_1724, entity, entityHitResult, class_1268.field_5808);
            if (!actionResult.method_23665()) {
               actionResult = this.mc.field_1761.method_2905(this.mc.field_1724, entity, class_1268.field_5808);
            }

            if (actionResult.method_23665() && actionResult.method_23666()) {
               this.mc.method_1562().method_52787(new class_2879(class_1268.field_5808));
            }

            this.info("Placed map", new Object[0]);
            this.timeOfLastMapInteract.put(entity.method_5628(), currentTime);
            return true;
         }
      }
   }

   private boolean checkEntity(class_1297 entity) {
      if (entity instanceof class_1533) {
         class_1533 itemFrame = (class_1533)entity;
         if (!this.getClosestPointOnBox(entity.method_5829(), this.mc.field_1724.method_33571()).method_55230(this.mc.field_1724.method_33571(), (Double)this.placeRange.get(), (Double)this.placeRange.get())) {
            return false;
         } else {
            return itemFrame.method_6940() == null || itemFrame.method_6940().method_7960();
         }
      } else {
         return false;
      }
   }

   public class_243 getClosestPointOnBox(class_238 box, class_243 point) {
      double x = Math.max(box.field_1323, Math.min(point.field_1352, box.field_1320));
      double y = Math.max(box.field_1322, Math.min(point.field_1351, box.field_1325));
      double z = Math.max(box.field_1321, Math.min(point.field_1350, box.field_1324));
      return new class_243(x, y, z);
   }
}
