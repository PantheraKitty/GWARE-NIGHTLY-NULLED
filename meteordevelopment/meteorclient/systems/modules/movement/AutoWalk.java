package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.NopPathManager;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_304;

public class AutoWalk extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<AutoWalk.Mode> mode;
   private final Setting<AutoWalk.Direction> direction;

   public AutoWalk() {
      super(Categories.Movement, "auto-walk", "Automatically walks forward.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Walking mode.")).defaultValue(AutoWalk.Mode.Smart)).onChanged((mode1) -> {
         if (this.isActive()) {
            if (mode1 == AutoWalk.Mode.Simple) {
               PathManagers.get().stop();
            } else {
               this.createGoal();
            }

            this.unpress();
         }

      })).build());
      this.direction = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("simple-direction")).description("The direction to walk in simple mode.")).defaultValue(AutoWalk.Direction.Forwards)).onChanged((direction1) -> {
         if (this.isActive()) {
            this.unpress();
         }

      })).visible(() -> {
         return this.mode.get() == AutoWalk.Mode.Simple;
      })).build());
   }

   public void onActivate() {
      if (this.mode.get() == AutoWalk.Mode.Smart) {
         this.createGoal();
      }

   }

   public void onDeactivate() {
      if (this.mode.get() == AutoWalk.Mode.Simple) {
         this.unpress();
      } else {
         PathManagers.get().stop();
      }

   }

   @EventHandler(
      priority = 100
   )
   private void onTick(TickEvent.Pre event) {
      if (this.mode.get() == AutoWalk.Mode.Simple) {
         switch(((AutoWalk.Direction)this.direction.get()).ordinal()) {
         case 0:
            this.setPressed(this.mc.field_1690.field_1894, true);
            break;
         case 1:
            this.setPressed(this.mc.field_1690.field_1881, true);
            break;
         case 2:
            this.setPressed(this.mc.field_1690.field_1913, true);
            break;
         case 3:
            this.setPressed(this.mc.field_1690.field_1849, true);
         }
      } else if (PathManagers.get() instanceof NopPathManager) {
         this.info("Smart mode requires Baritone", new Object[0]);
         this.toggle();
      }

   }

   private void unpress() {
      this.setPressed(this.mc.field_1690.field_1894, false);
      this.setPressed(this.mc.field_1690.field_1881, false);
      this.setPressed(this.mc.field_1690.field_1913, false);
      this.setPressed(this.mc.field_1690.field_1849, false);
   }

   private void setPressed(class_304 key, boolean pressed) {
      key.method_23481(pressed);
      Input.setKeyState(key, pressed);
   }

   private void createGoal() {
      PathManagers.get().moveInDirection(this.mc.field_1724.method_36454());
   }

   public static enum Mode {
      Simple,
      Smart;

      // $FF: synthetic method
      private static AutoWalk.Mode[] $values() {
         return new AutoWalk.Mode[]{Simple, Smart};
      }
   }

   public static enum Direction {
      Forwards,
      Backwards,
      Left,
      Right;

      // $FF: synthetic method
      private static AutoWalk.Direction[] $values() {
         return new AutoWalk.Direction[]{Forwards, Backwards, Left, Right};
      }
   }
}
