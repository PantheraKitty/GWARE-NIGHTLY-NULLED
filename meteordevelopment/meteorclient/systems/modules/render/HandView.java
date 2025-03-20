package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.render.ArmRenderEvent;
import meteordevelopment.meteorclient.events.render.HeldItemRendererEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Vector3dSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import org.joml.Vector3d;

public class HandView extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgMainHand;
   private final SettingGroup sgOffHand;
   private final SettingGroup sgArm;
   private final Setting<Boolean> followRotations;
   public final Setting<Boolean> oldAnimations;
   public final Setting<Boolean> showSwapping;
   private final Setting<Boolean> disableFoodAnimation;
   public final Setting<HandView.SwingMode> swingMode;
   public final Setting<Integer> swingSpeed;
   public final Setting<Double> mainSwing;
   public final Setting<Double> offSwing;
   private final Setting<Vector3d> scaleMain;
   private final Setting<Vector3d> posMain;
   private final Setting<Vector3d> rotMain;
   private final Setting<Vector3d> scaleOff;
   private final Setting<Vector3d> posOff;
   private final Setting<Vector3d> rotOff;
   private final Setting<Vector3d> scaleArm;
   private final Setting<Vector3d> posArm;
   private final Setting<Vector3d> rotArm;

   public HandView() {
      super(Categories.Render, "hand-view", "Alters the way items are rendered in your hands.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgMainHand = this.settings.createGroup("Main Hand");
      this.sgOffHand = this.settings.createGroup("Off Hand");
      this.sgArm = this.settings.createGroup("Arm");
      this.followRotations = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("server-rotations")).description("Makes your hands follow your serverside rotations.")).defaultValue(false)).build());
      this.oldAnimations = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("old-animations")).description("Changes hit animations to those like 1.8")).defaultValue(false)).build());
      this.showSwapping = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-swapping")).description("Whether or not to show the item swapping animation")).defaultValue(true)).build());
      this.disableFoodAnimation = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-eating-animation")).description("Disables the eating animation. Potentially desirable if it goes offscreen.")).defaultValue(false)).build());
      this.swingMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("swing-mode")).description("Modifies your client & server hand swinging.")).defaultValue(HandView.SwingMode.None)).build());
      this.swingSpeed = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("swing-speed")).description("The swing speed of your hands.")).defaultValue(6)).range(0, 20).sliderMax(20).build());
      this.mainSwing = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("main-hand-progress")).description("The swing progress of your main hand.")).defaultValue(0.0D).range(0.0D, 1.0D).sliderMax(1.0D).build());
      this.offSwing = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("off-hand-progress")).description("The swing progress of your off hand.")).defaultValue(0.0D).range(0.0D, 1.0D).sliderMax(1.0D).build());
      this.scaleMain = this.sgMainHand.add(((Vector3dSetting.Builder)((Vector3dSetting.Builder)(new Vector3dSetting.Builder()).name("scale")).description("The scale of your main hand.")).defaultValue(1.0D, 1.0D, 1.0D).sliderMax(5.0D).decimalPlaces(1).build());
      this.posMain = this.sgMainHand.add(((Vector3dSetting.Builder)((Vector3dSetting.Builder)(new Vector3dSetting.Builder()).name("position")).description("The position of your main hand.")).defaultValue(0.0D, 0.0D, 0.0D).sliderRange(-3.0D, 3.0D).decimalPlaces(1).build());
      this.rotMain = this.sgMainHand.add(((Vector3dSetting.Builder)((Vector3dSetting.Builder)(new Vector3dSetting.Builder()).name("rotation")).description("The rotation of your main hand.")).defaultValue(0.0D, 0.0D, 0.0D).sliderRange(-180.0D, 180.0D).decimalPlaces(0).build());
      this.scaleOff = this.sgOffHand.add(((Vector3dSetting.Builder)((Vector3dSetting.Builder)(new Vector3dSetting.Builder()).name("scale")).description("The scale of your off hand.")).defaultValue(1.0D, 1.0D, 1.0D).sliderMax(5.0D).decimalPlaces(1).build());
      this.posOff = this.sgOffHand.add(((Vector3dSetting.Builder)((Vector3dSetting.Builder)(new Vector3dSetting.Builder()).name("position")).description("The position of your off hand.")).defaultValue(0.0D, 0.0D, 0.0D).sliderRange(-3.0D, 3.0D).decimalPlaces(1).build());
      this.rotOff = this.sgOffHand.add(((Vector3dSetting.Builder)((Vector3dSetting.Builder)(new Vector3dSetting.Builder()).name("rotation")).description("The rotation of your off hand.")).defaultValue(0.0D, 0.0D, 0.0D).sliderRange(-180.0D, 180.0D).decimalPlaces(0).build());
      this.scaleArm = this.sgArm.add(((Vector3dSetting.Builder)(new Vector3dSetting.Builder()).name("scale")).defaultValue(1.0D, 1.0D, 1.0D).sliderMax(5.0D).decimalPlaces(1).build());
      this.posArm = this.sgArm.add(((Vector3dSetting.Builder)(new Vector3dSetting.Builder()).name("position")).defaultValue(0.0D, 0.0D, 0.0D).sliderRange(-3.0D, 3.0D).decimalPlaces(1).build());
      this.rotArm = this.sgArm.add(((Vector3dSetting.Builder)(new Vector3dSetting.Builder()).name("rotation")).defaultValue(0.0D, 0.0D, 0.0D).sliderRange(-180.0D, 180.0D).decimalPlaces(0).build());
   }

   @EventHandler
   private void onHeldItemRender(HeldItemRendererEvent event) {
      if (Rotations.rotating && (Boolean)this.followRotations.get()) {
         this.applyServerRotations(event.matrix);
      }

      if (event.hand == class_1268.field_5808) {
         this.rotate(event.matrix, (Vector3d)this.rotMain.get());
         this.scale(event.matrix, (Vector3d)this.scaleMain.get());
         this.translate(event.matrix, (Vector3d)this.posMain.get());
      } else {
         this.rotate(event.matrix, (Vector3d)this.rotOff.get());
         this.scale(event.matrix, (Vector3d)this.scaleOff.get());
         this.translate(event.matrix, (Vector3d)this.posOff.get());
      }

   }

   @EventHandler
   private void onRenderArm(ArmRenderEvent event) {
      this.rotate(event.matrix, (Vector3d)this.rotArm.get());
      this.scale(event.matrix, (Vector3d)this.scaleArm.get());
      this.translate(event.matrix, (Vector3d)this.posArm.get());
   }

   private void rotate(class_4587 matrix, Vector3d rotation) {
      matrix.method_22907(class_7833.field_40714.rotationDegrees((float)rotation.x));
      matrix.method_22907(class_7833.field_40716.rotationDegrees((float)rotation.y));
      matrix.method_22907(class_7833.field_40718.rotationDegrees((float)rotation.z));
   }

   private void scale(class_4587 matrix, Vector3d scale) {
      matrix.method_22905((float)scale.x, (float)scale.y, (float)scale.z);
   }

   private void translate(class_4587 matrix, Vector3d translation) {
      matrix.method_46416((float)translation.x, (float)translation.y, (float)translation.z);
   }

   private void applyServerRotations(class_4587 matrix) {
      matrix.method_22907(class_7833.field_40714.rotationDegrees(this.mc.field_1724.method_36455() - Rotations.serverPitch));
      matrix.method_22907(class_7833.field_40716.rotationDegrees(this.mc.field_1724.method_36454() - Rotations.serverYaw));
   }

   public boolean oldAnimations() {
      return this.isActive() && (Boolean)this.oldAnimations.get();
   }

   public boolean showSwapping() {
      return this.isActive() && (Boolean)this.showSwapping.get();
   }

   public boolean disableFoodAnimation() {
      return this.isActive() && (Boolean)this.disableFoodAnimation.get();
   }

   public static enum SwingMode {
      Offhand,
      Mainhand,
      None;

      // $FF: synthetic method
      private static HandView.SwingMode[] $values() {
         return new HandView.SwingMode[]{Offhand, Mainhand, None};
      }
   }
}
