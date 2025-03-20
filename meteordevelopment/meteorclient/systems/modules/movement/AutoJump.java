package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class AutoJump extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<AutoJump.Mode> mode;
   private final Setting<AutoJump.JumpWhen> jumpIf;
   private final Setting<Double> velocityHeight;

   public AutoJump() {
      super(Categories.Movement, "auto-jump", "Automatically jumps.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The method of jumping.")).defaultValue(AutoJump.Mode.Jump)).build());
      this.jumpIf = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("jump-if")).description("Jump if.")).defaultValue(AutoJump.JumpWhen.Always)).build());
      this.velocityHeight = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("velocity-height")).description("The distance that velocity mode moves you.")).defaultValue(0.25D).min(0.0D).sliderMax(2.0D).build());
   }

   private boolean jump() {
      boolean var10000;
      switch(((AutoJump.JumpWhen)this.jumpIf.get()).ordinal()) {
      case 0:
         var10000 = this.mc.field_1724.method_5624() && (this.mc.field_1724.field_6250 != 0.0F || this.mc.field_1724.field_6212 != 0.0F);
         break;
      case 1:
         var10000 = this.mc.field_1724.field_6250 != 0.0F || this.mc.field_1724.field_6212 != 0.0F;
         break;
      case 2:
         var10000 = true;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1724.method_24828() && !this.mc.field_1724.method_5715() && this.jump()) {
         if (this.mode.get() == AutoJump.Mode.Jump) {
            this.mc.field_1724.method_6043();
         } else {
            ((IVec3d)this.mc.field_1724.method_18798()).setY((Double)this.velocityHeight.get());
         }

      }
   }

   public static enum Mode {
      Jump,
      LowHop;

      // $FF: synthetic method
      private static AutoJump.Mode[] $values() {
         return new AutoJump.Mode[]{Jump, LowHop};
      }
   }

   public static enum JumpWhen {
      Sprinting,
      Walking,
      Always;

      // $FF: synthetic method
      private static AutoJump.JumpWhen[] $values() {
         return new AutoJump.JumpWhen[]{Sprinting, Walking, Always};
      }
   }
}
