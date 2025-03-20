package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.Collections;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BreakIndicators;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2338;
import net.minecraft.class_3532;

public class PhaseCompassHud extends HudElement {
   public static final HudElementInfo<PhaseCompassHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgColors;
   private final Setting<Double> scale;
   private final Setting<Integer> radius;
   private final Setting<SettingColor> colorBestPhase;
   private final Setting<SettingColor> colorOther;
   private final Setting<Boolean> shadow;

   public PhaseCompassHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgColors = this.settings.createGroup("Colors");
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale of the compass.")).defaultValue(1.0D).min(1.0D).sliderRange(1.0D, 5.0D).onChanged((aDouble) -> {
         this.calculateSize();
      })).build());
      this.radius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("radius")).description("The radius in blocks to scan for phase locations.")).defaultValue(3)).min(1).sliderRange(1, 10).build());
      this.colorBestPhase = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("best-phase-color")).description("Color of the best phase location.")).defaultValue(new SettingColor(0, 255, 0, 255)).build());
      this.colorOther = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("other-color")).description("Color of other directions.")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.shadow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("shadow")).description("Text shadow.")).defaultValue(false)).build());
      this.calculateSize();
   }

   public void setSize(double width, double height) {
      super.setSize(width, height);
   }

   private void calculateSize() {
      this.setSize(100.0D * (Double)this.scale.get(), 100.0D * (Double)this.scale.get());
   }

   public void render(HudRenderer renderer) {
      double centerX = (double)this.x + (double)this.getWidth() / 2.0D;
      double centerY = (double)this.y + (double)this.getHeight() / 2.0D;
      double yaw = this.isInEditor() ? 180.0D : (double)class_3532.method_15393(MeteorClient.mc.field_1724.method_36454());
      yaw = Math.toRadians(yaw);
      class_2338 bestPhasePos = this.findBestPhaseLocation();
      if (bestPhasePos != null) {
         double angle = Math.atan2((double)bestPhasePos.method_10260() - MeteorClient.mc.field_1724.method_23321(), (double)bestPhasePos.method_10263() - MeteorClient.mc.field_1724.method_23317()) - yaw;
         double endX = centerX + Math.sin(angle) * 40.0D * (Double)this.scale.get();
         double endY = centerY - Math.cos(angle) * 40.0D * (Double)this.scale.get();
         renderer.line(centerX, centerY, endX, endY, (Color)this.colorBestPhase.get());
         renderer.text("⬆", endX - renderer.textWidth("⬆", (Boolean)this.shadow.get(), 1.0D) / 2.0D, endY - renderer.textHeight((Boolean)this.shadow.get(), 1.0D) / 2.0D, (Color)this.colorBestPhase.get(), (Boolean)this.shadow.get(), 1.0D);
      }

      PhaseCompassHud.Direction[] var16 = PhaseCompassHud.Direction.values();
      int var10 = var16.length;

      for(int var15 = 0; var15 < var10; ++var15) {
         PhaseCompassHud.Direction direction = var16[var15];
         renderer.text(direction.name(), centerX + this.getX(direction, yaw) - renderer.textWidth(direction.name(), (Boolean)this.shadow.get(), 1.0D) / 2.0D, centerY + this.getY(direction, yaw) - renderer.textHeight((Boolean)this.shadow.get(), 1.0D) / 2.0D, (Color)this.colorOther.get(), (Boolean)this.shadow.get(), 1.0D);
      }

   }

   private class_2338 findBestPhaseLocation() {
      class_2338 playerPos = MeteorClient.mc.field_1724.method_24515();
      BreakIndicators breakIndicators = (BreakIndicators)Modules.get().get(BreakIndicators.class);
      Set<class_2338> breakingBlocks = breakIndicators != null ? breakIndicators.breakStartTimes.keySet() : Collections.emptySet();
      if (!breakingBlocks.isEmpty()) {
         class_2338 breakingBlock = (class_2338)breakingBlocks.iterator().next();
         if (!breakingBlock.equals(playerPos)) {
            double angle = Math.atan2((double)(breakingBlock.method_10260() - playerPos.method_10260()), (double)(breakingBlock.method_10263() - playerPos.method_10263())) + 3.141592653589793D;
            int distance = 5;
            return new class_2338(playerPos.method_10263() + (int)(Math.cos(angle) * (double)distance), playerPos.method_10264(), playerPos.method_10260() + (int)(Math.sin(angle) * (double)distance));
         }
      }

      return null;
   }

   private double getX(PhaseCompassHud.Direction direction, double yaw) {
      return Math.sin(this.getPos(direction, yaw)) * (Double)this.scale.get() * 40.0D;
   }

   private double getY(PhaseCompassHud.Direction direction, double yaw) {
      return Math.cos(this.getPos(direction, yaw)) * (Double)this.scale.get() * 40.0D;
   }

   private double getPos(PhaseCompassHud.Direction direction, double yaw) {
      return yaw + (double)direction.ordinal() * 3.141592653589793D / 2.0D;
   }

   static {
      INFO = new HudElementInfo(Hud.GROUP, "phase-compass", "Displays a compass that points to the best phase location.", PhaseCompassHud::new);
   }

   private static enum Direction {
      N,
      W,
      S,
      E;

      // $FF: synthetic method
      private static PhaseCompassHud.Direction[] $values() {
         return new PhaseCompassHud.Direction[]{N, W, S, E};
      }
   }
}
