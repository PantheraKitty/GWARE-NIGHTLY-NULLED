package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2338;

public class AutoWeb extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> pauseEat;
   private final Setting<Double> range;
   private final Setting<SortPriority> priority;
   private final Setting<Boolean> placeHead;
   private final Setting<Boolean> placeFeet;
   private final Setting<Boolean> placeCrawling;
   private class_1657 target;

   public AutoWeb() {
      super(Categories.Combat, "auto-web", "Automatically places webs on other players.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-eat")).description("Pauses while eating.")).defaultValue(true)).build());
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The maximum distance to target players.")).defaultValue(5.0D).range(0.0D, 5.0D).sliderMax(5.0D).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to filter targets within range.")).defaultValue(SortPriority.LowestDistance)).build());
      this.placeHead = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place-head")).description("Places webs in the target's upper hitbox.")).defaultValue(true)).build());
      this.placeFeet = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place-feet")).description("Places webs in the target's lower hitbox.")).defaultValue(false)).build());
      this.placeCrawling = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place-crawling")).description("Places webs in the taget's lower hitbox when they're swimming.")).defaultValue(true)).build());
      this.target = null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (TargetUtils.isBadTarget(this.target, (Double)this.range.get())) {
         this.target = TargetUtils.getPlayerTarget((Double)this.range.get(), (SortPriority)this.priority.get());
         if (TargetUtils.isBadTarget(this.target, (Double)this.range.get())) {
            return;
         }
      }

      List<class_2338> placePoses = new ArrayList();
      if ((Boolean)this.placeHead.get()) {
         placePoses.add(this.target.method_24515().method_10084());
      }

      if (((Boolean)this.placeFeet.get() || (Boolean)this.placeCrawling.get() && this.target.method_20448()) && !PlayerUtils.isPlayerPhased(this.target)) {
         placePoses.add(this.target.method_24515());
      }

      if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
         if (MeteorClient.BLOCK.beginPlacement(placePoses, class_1802.field_8786)) {
            placePoses.forEach((blockPos) -> {
               MeteorClient.BLOCK.placeBlock(class_1802.field_8786, blockPos);
            });
            MeteorClient.BLOCK.endPlacement();
         }
      }
   }
}
