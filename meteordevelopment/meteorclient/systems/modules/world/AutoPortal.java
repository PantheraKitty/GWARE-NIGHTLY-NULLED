package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.BaritoneUtils;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_3965;
import net.minecraft.class_408;

public class AutoPortal extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Keybind> buildBind;
   private final Setting<Boolean> lightPortal;
   private final Setting<Boolean> baritonePathToPortal;
   private boolean active;
   private boolean keyUnpressed;
   private List<class_2338> bestPortalFrameBlocks;
   private class_2338 ignitionPos;

   public AutoPortal() {
      super(Categories.World, "auto-portal", "Automatically builds and paths to a portal");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.buildBind = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("key-bind")).description("Build a portal on keybind press")).build());
      this.lightPortal = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("light-portal")).description("Whether or not to light the portal")).defaultValue(true)).build());
      this.baritonePathToPortal = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("baritone-to-portal")).description("Baritones to the portal after finishing building")).defaultValue(true)).visible(() -> {
         return BaritoneUtils.IS_AVAILABLE;
      })).build());
      this.active = false;
      this.keyUnpressed = false;
      this.bestPortalFrameBlocks = null;
      this.ignitionPos = null;
   }

   private void activate() {
      this.active = true;
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.update();
      }
   }

   private void deactivate(boolean built) {
      this.bestPortalFrameBlocks = null;
      this.ignitionPos = null;
      this.active = false;
      if (built) {
         this.info("Built portal", new Object[0]);
      }

   }

   private void update() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.active) {
            if (!InvUtils.find(class_1802.field_8281).found()) {
               this.deactivate(false);
            }
         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.active) {
         if (this.bestPortalFrameBlocks == null || this.bestPortalFrameBlocks.isEmpty()) {
            this.bestPortalFrameBlocks = this.findBestPortalFrame();
         }

         if (this.bestPortalFrameBlocks != null && !this.bestPortalFrameBlocks.isEmpty()) {
            if (!this.mc.field_1724.method_6115()) {
               List<class_2338> placesLeft = this.bestPortalFrameBlocks.stream().filter((blockPos) -> {
                  return this.mc.field_1687.method_22347(blockPos);
               }).toList();
               if (placesLeft.isEmpty()) {
                  if ((Boolean)this.lightPortal.get()) {
                     if (MeteorClient.SWAP.beginSwap(class_1802.field_8884, true)) {
                        this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5808, new class_3965(this.ignitionPos.method_10074().method_46558(), class_2350.field_11036, this.ignitionPos.method_10074(), false));
                        MeteorClient.SWAP.endSwap(true);
                        if ((Boolean)this.baritonePathToPortal.get() && BaritoneUtils.IS_AVAILABLE) {
                           PathManagers.get().moveToBlockPos(this.ignitionPos);
                        }
                     } else {
                        this.info("Failed to light portal", new Object[0]);
                     }

                     this.deactivate(true);
                  } else {
                     this.deactivate(true);
                  }
               } else {
                  if (MeteorClient.BLOCK.beginPlacement(placesLeft, class_1802.field_8281)) {
                     placesLeft.forEach((blockPos) -> {
                        MeteorClient.BLOCK.placeBlock(class_1802.field_8281, blockPos);
                     });
                     MeteorClient.BLOCK.endPlacement();
                  }

               }
            }
         } else {
            this.deactivate(false);
         }
      }
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      if (!((Keybind)this.buildBind.get()).isPressed()) {
         this.keyUnpressed = true;
      }

      if (((Keybind)this.buildBind.get()).isPressed() && this.keyUnpressed && !(this.mc.field_1755 instanceof class_408)) {
         this.activate();
         this.keyUnpressed = false;
      }

      this.update();
      if (this.ignitionPos != null) {
         event.renderer.box((class_2338)this.ignitionPos, Color.RED, Color.RED, ShapeMode.Both, 0);
      }

   }

   private List<class_2338> findBestPortalFrame() {
      class_2338 startPos = this.mc.field_1724.method_24515();
      List<class_2338> bestFrame = new ArrayList();
      class_2338 bestIgnitionPos = null;
      double bestPortalScore = 0.0D;

      for(int x = -10; x <= 10; ++x) {
         for(int y = -5; y <= 5; ++y) {
            for(int z = -10; z <= 10; ++z) {
               class_2338 pos = startPos.method_10069(x, y, z);
               if (this.canBuildPortalAtPosition(pos)) {
                  int distance = pos.method_10069(1, 2, 0).method_19455(this.mc.field_1724.method_24515());
                  double score = 1.0D / (double)distance;
                  if (this.mc.field_1687.method_8320(pos.method_10074()).method_26212(this.mc.field_1687, pos.method_10074())) {
                     score += 10.0D;
                  }

                  if (score > bestPortalScore) {
                     bestPortalScore = score;
                     bestFrame = this.getPortalFramePositions(pos);
                     bestIgnitionPos = pos.method_10069(1, 1, 0);
                  }
               }
            }
         }
      }

      if (!((List)bestFrame).isEmpty()) {
         this.ignitionPos = bestIgnitionPos;
      } else {
         this.ignitionPos = null;
      }

      return (List)bestFrame;
   }

   private List<class_2338> getPortalFramePositions(class_2338 basePos) {
      List<class_2338> framePositions = new ArrayList();

      int x;
      for(x = 1; x < 4; ++x) {
         framePositions.add(basePos.method_10069(0, x, 0));
         framePositions.add(basePos.method_10069(3, x, 0));
      }

      for(x = 1; x < 3; ++x) {
         framePositions.add(basePos.method_10069(x, 0, 0));
         framePositions.add(basePos.method_10069(x, 4, 0));
      }

      return framePositions;
   }

   private boolean canBuildPortalAtPosition(class_2338 pos) {
      for(int y = 0; y < 5; ++y) {
         for(int x = 0; x < 4; ++x) {
            class_2338 checkPos = pos.method_10069(x, y, 0);
            if (!BlockUtils.canPlace(checkPos, true) || this.mc.field_1724.method_33571().method_1022(checkPos.method_46558()) > 6.0D) {
               return false;
            }
         }
      }

      return true;
   }
}
