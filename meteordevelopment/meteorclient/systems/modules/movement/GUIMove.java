package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.mixin.CreativeInventoryScreenAccessor;
import meteordevelopment.meteorclient.mixin.KeyBindingAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_304;
import net.minecraft.class_3532;
import net.minecraft.class_408;
import net.minecraft.class_463;
import net.minecraft.class_471;
import net.minecraft.class_481;
import net.minecraft.class_497;
import net.minecraft.class_498;
import net.minecraft.class_7706;
import net.minecraft.class_3675.class_306;
import net.minecraft.class_3675.class_307;

public class GUIMove extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<GUIMove.Screens> screens;
   private final Setting<Boolean> jump;
   private final Setting<Boolean> sneak;
   public final Setting<Boolean> sprint;
   private final Setting<Boolean> arrowsRotate;
   private final Setting<Double> rotateSpeed;

   public GUIMove() {
      super(Categories.Movement, "gui-move", "Allows you to perform various actions while in GUIs.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.screens = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("guis")).description("Which GUIs to move in.")).defaultValue(GUIMove.Screens.Inventory)).build());
      this.jump = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("jump")).description("Allows you to jump while in GUIs.")).defaultValue(true)).onChanged((aBoolean) -> {
         if (this.isActive() && !aBoolean) {
            this.set(this.mc.field_1690.field_1903, false);
         }

      })).build());
      this.sneak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sneak")).description("Allows you to sneak while in GUIs.")).defaultValue(true)).onChanged((aBoolean) -> {
         if (this.isActive() && !aBoolean) {
            this.set(this.mc.field_1690.field_1832, false);
         }

      })).build());
      this.sprint = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sprint")).description("Allows you to sprint while in GUIs.")).defaultValue(true)).onChanged((aBoolean) -> {
         if (this.isActive() && !aBoolean) {
            this.set(this.mc.field_1690.field_1867, false);
         }

      })).build());
      this.arrowsRotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("arrows-rotate")).description("Allows you to use your arrow keys to rotate while in GUIs.")).defaultValue(true)).build());
      this.rotateSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rotate-speed")).description("Rotation speed while in GUIs.")).defaultValue(4.0D).min(0.0D).build());
   }

   public void onDeactivate() {
      this.set(this.mc.field_1690.field_1894, false);
      this.set(this.mc.field_1690.field_1881, false);
      this.set(this.mc.field_1690.field_1913, false);
      this.set(this.mc.field_1690.field_1849, false);
      if ((Boolean)this.jump.get()) {
         this.set(this.mc.field_1690.field_1903, false);
      }

      if ((Boolean)this.sneak.get()) {
         this.set(this.mc.field_1690.field_1832, false);
      }

      if ((Boolean)this.sprint.get()) {
         this.set(this.mc.field_1690.field_1867, false);
      }

   }

   public boolean disableSpace() {
      return this.isActive() && (Boolean)this.jump.get() && this.mc.field_1690.field_1903.method_1427();
   }

   public boolean disableArrows() {
      return this.isActive() && (Boolean)this.arrowsRotate.get();
   }

   @EventHandler
   private void onPlayerMoveEvent(PlayerTickMovementEvent event) {
      if (!this.skip()) {
         if (this.screens.get() != GUIMove.Screens.GUI || this.mc.field_1755 instanceof WidgetScreen) {
            if (this.screens.get() != GUIMove.Screens.Inventory || !(this.mc.field_1755 instanceof WidgetScreen)) {
               this.set(this.mc.field_1690.field_1894, Input.isPressed(this.mc.field_1690.field_1894));
               this.set(this.mc.field_1690.field_1881, Input.isPressed(this.mc.field_1690.field_1881));
               this.set(this.mc.field_1690.field_1913, Input.isPressed(this.mc.field_1690.field_1913));
               this.set(this.mc.field_1690.field_1849, Input.isPressed(this.mc.field_1690.field_1849));
               if ((Boolean)this.jump.get()) {
                  this.set(this.mc.field_1690.field_1903, Input.isPressed(this.mc.field_1690.field_1903));
               }

               if ((Boolean)this.sneak.get()) {
                  this.set(this.mc.field_1690.field_1832, Input.isPressed(this.mc.field_1690.field_1832));
               }

               if ((Boolean)this.sprint.get()) {
                  this.set(this.mc.field_1690.field_1867, Input.isPressed(this.mc.field_1690.field_1867));
               }

            }
         }
      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if (!this.skip()) {
         if (this.screens.get() != GUIMove.Screens.GUI || this.mc.field_1755 instanceof WidgetScreen) {
            if (this.screens.get() != GUIMove.Screens.Inventory || !(this.mc.field_1755 instanceof WidgetScreen)) {
               float rotationDelta = Math.min((float)((Double)this.rotateSpeed.get() * event.frameTime * 20.0D), 100.0F);
               if ((Boolean)this.arrowsRotate.get()) {
                  float yaw = this.mc.field_1724.method_36454();
                  float pitch = this.mc.field_1724.method_36455();
                  if (Input.isKeyPressed(263)) {
                     yaw -= rotationDelta;
                  }

                  if (Input.isKeyPressed(262)) {
                     yaw += rotationDelta;
                  }

                  if (Input.isKeyPressed(265)) {
                     pitch -= rotationDelta;
                  }

                  if (Input.isKeyPressed(264)) {
                     pitch += rotationDelta;
                  }

                  pitch = class_3532.method_15363(pitch, -90.0F, 90.0F);
                  this.mc.field_1724.method_36456(yaw);
                  this.mc.field_1724.method_36457(pitch);
               }

            }
         }
      }
   }

   private void set(class_304 bind, boolean pressed) {
      boolean wasPressed = bind.method_1434();
      bind.method_23481(pressed);
      class_306 key = ((KeyBindingAccessor)bind).getKey();
      if (wasPressed != pressed && key.method_1442() == class_307.field_1668) {
         MeteorClient.EVENT_BUS.post((ICancellable)KeyEvent.get(key.method_1444(), 0, pressed ? KeyAction.Press : KeyAction.Release));
      }

   }

   public boolean skip() {
      return this.mc.field_1755 == null || this.mc.field_1755 instanceof class_481 && CreativeInventoryScreenAccessor.getSelectedTab() == class_7706.method_47344() || this.mc.field_1755 instanceof class_408 || this.mc.field_1755 instanceof class_498 || this.mc.field_1755 instanceof class_471 || this.mc.field_1755 instanceof class_463 || this.mc.field_1755 instanceof class_497;
   }

   public static enum Screens {
      GUI,
      Inventory,
      Both;

      // $FF: synthetic method
      private static GUIMove.Screens[] $values() {
         return new GUIMove.Screens[]{GUI, Inventory, Both};
      }
   }
}
