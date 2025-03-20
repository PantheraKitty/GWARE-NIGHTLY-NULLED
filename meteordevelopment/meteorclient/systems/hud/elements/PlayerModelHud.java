package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_490;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PlayerModelHud extends HudElement {
   public static final HudElementInfo<PlayerModelHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgBackground;
   private final Setting<Double> scale;
   private final Setting<Boolean> copyYaw;
   private final Setting<Integer> customYaw;
   private final Setting<Boolean> copyPitch;
   private final Setting<Integer> customPitch;
   private final Setting<PlayerModelHud.CenterOrientation> centerOrientation;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;

   public PlayerModelHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgBackground = this.settings.createGroup("Background");
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale.")).defaultValue(2.0D).min(1.0D).sliderRange(1.0D, 5.0D).onChanged((aDouble) -> {
         this.calculateSize();
      })).build());
      this.copyYaw = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("copy-yaw")).description("Makes the player model's yaw equal to yours.")).defaultValue(true)).build());
      this.customYaw = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("custom-yaw")).description("Custom yaw for when copy yaw is off.")).defaultValue(0)).range(-180, 180).sliderRange(-180, 180).visible(() -> {
         return !(Boolean)this.copyYaw.get();
      })).build());
      this.copyPitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("copy-pitch")).description("Makes the player model's pitch equal to yours.")).defaultValue(true)).build());
      this.customPitch = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("custom-pitch")).description("Custom pitch for when copy pitch is off.")).defaultValue(0)).range(-90, 90).sliderRange(-90, 90).visible(() -> {
         return !(Boolean)this.copyPitch.get();
      })).build());
      this.centerOrientation = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("center-orientation")).description("Which direction the player faces when the HUD model faces directly forward.")).defaultValue(PlayerModelHud.CenterOrientation.South)).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgBackground;
      ColorSetting.Builder var10002 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      Setting var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.calculateSize();
   }

   public void render(HudRenderer renderer) {
      renderer.post(() -> {
         class_1657 player = MeteorClient.mc.field_1724;
         if (player != null) {
            float offset = this.centerOrientation.get() == PlayerModelHud.CenterOrientation.North ? 180.0F : 0.0F;
            float yaw = (Boolean)this.copyYaw.get() ? class_3532.method_15393(player.field_5982 + (player.method_36454() - player.field_5982) * MeteorClient.mc.method_60646().method_60637(true) + offset) : (float)(Integer)this.customYaw.get();
            float pitch = (Boolean)this.copyPitch.get() ? player.method_36455() : (float)(Integer)this.customPitch.get();
            this.drawEntity(renderer.drawContext, this.x, this.y, (int)(30.0D * (Double)this.scale.get()), -yaw, -pitch, player);
         }
      });
      if ((Boolean)this.background.get()) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), (Color)this.backgroundColor.get());
      } else if (MeteorClient.mc.field_1724 == null) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), (Color)this.backgroundColor.get());
         renderer.line((double)this.x, (double)this.y, (double)(this.x + this.getWidth()), (double)(this.y + this.getHeight()), Color.GRAY);
         renderer.line((double)(this.x + this.getWidth()), (double)this.y, (double)this.x, (double)(this.y + this.getHeight()), Color.GRAY);
      }

   }

   private void calculateSize() {
      this.setSize(50.0D * (Double)this.scale.get(), 75.0D * (Double)this.scale.get());
   }

   private void drawEntity(class_332 context, int x, int y, int size, float yaw, float pitch, class_1309 entity) {
      float tanYaw = (float)Math.atan((double)(yaw / 40.0F));
      float tanPitch = (float)Math.atan((double)(pitch / 40.0F));
      Quaternionf quaternion = (new Quaternionf()).rotateZ(3.1415927F);
      float previousBodyYaw = entity.field_6283;
      float previousYaw = entity.method_36454();
      float previousPitch = entity.method_36455();
      float previousPrevHeadYaw = entity.field_6259;
      float prevHeadYaw = entity.field_6241;
      entity.field_6283 = 180.0F + tanYaw * 20.0F;
      entity.method_36456(180.0F + tanYaw * 40.0F);
      entity.method_36457(-tanPitch * 20.0F);
      entity.field_6241 = entity.method_36454();
      entity.field_6259 = entity.method_36454();
      class_490.method_48472(context, (float)(x + this.getWidth() / 2), (float)y + (float)this.getHeight() * 0.9F, (float)size, new Vector3f(), quaternion, (Quaternionf)null, entity);
      entity.field_6283 = previousBodyYaw;
      entity.method_36456(previousYaw);
      entity.method_36457(previousPitch);
      entity.field_6259 = previousPrevHeadYaw;
      entity.field_6241 = prevHeadYaw;
   }

   static {
      INFO = new HudElementInfo(Hud.GROUP, "player-model", "Displays a model of your player.", PlayerModelHud::new);
   }

   private static enum CenterOrientation {
      North,
      South;

      // $FF: synthetic method
      private static PlayerModelHud.CenterOrientation[] $values() {
         return new PlayerModelHud.CenterOrientation[]{North, South};
      }
   }
}
