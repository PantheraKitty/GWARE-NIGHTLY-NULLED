package meteordevelopment.meteorclient.systems.modules.movement.speed;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.speed.modes.Strafe;
import meteordevelopment.meteorclient.systems.modules.movement.speed.modes.Vanilla;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1313;
import net.minecraft.class_2708;

public class Speed extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<SpeedModes> speedMode;
   public final Setting<Double> vanillaSpeed;
   public final Setting<Double> ncpSpeed;
   public final Setting<Boolean> ncpSpeedLimit;
   public final Setting<Double> timer;
   public final Setting<Boolean> inLiquids;
   public final Setting<Boolean> whenSneaking;
   public final Setting<Boolean> vanillaOnGround;
   private SpeedMode currentMode;

   public Speed() {
      super(Categories.Movement, "speed", "Modifies your movement speed when moving on the ground.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.speedMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The method of applying speed.")).defaultValue(SpeedModes.Vanilla)).onModuleActivated((speedModesSetting) -> {
         this.onSpeedModeChanged((SpeedModes)speedModesSetting.get());
      })).onChanged(this::onSpeedModeChanged)).build());
      this.vanillaSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vanilla-speed")).description("The speed in blocks per second.")).defaultValue(5.6D).min(0.0D).sliderMax(20.0D).visible(() -> {
         return this.speedMode.get() == SpeedModes.Vanilla;
      })).build());
      this.ncpSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("strafe-speed")).description("The speed.")).visible(() -> {
         return this.speedMode.get() == SpeedModes.Strafe;
      })).defaultValue(1.6D).min(0.0D).sliderMax(3.0D).build());
      this.ncpSpeedLimit = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("speed-limit")).description("Limits your speed on servers with very strict anticheats.")).visible(() -> {
         return this.speedMode.get() == SpeedModes.Strafe;
      })).defaultValue(false)).build());
      this.timer = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("timer")).description("Timer override.")).defaultValue(1.0D).min(0.01D).sliderMin(0.01D).sliderMax(10.0D).build());
      this.inLiquids = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("in-liquids")).description("Uses speed when in lava or water.")).defaultValue(false)).build());
      this.whenSneaking = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("when-sneaking")).description("Uses speed when sneaking.")).defaultValue(false)).build());
      this.vanillaOnGround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Uses speed only when standing on a block.")).visible(() -> {
         return this.speedMode.get() == SpeedModes.Vanilla;
      })).defaultValue(false)).build());
      this.onSpeedModeChanged((SpeedModes)this.speedMode.get());
   }

   public void onActivate() {
      this.currentMode.onActivate();
   }

   public void onDeactivate() {
      ((Timer)Modules.get().get(Timer.class)).setOverride(1.0D);
      this.currentMode.onDeactivate();
   }

   @EventHandler
   private void onPlayerMove(PlayerMoveEvent event) {
      if (event.type == class_1313.field_6308 && !this.mc.field_1724.method_6128() && !this.mc.field_1724.method_6101() && this.mc.field_1724.method_5854() == null) {
         if ((Boolean)this.whenSneaking.get() || !this.mc.field_1724.method_5715()) {
            if (!(Boolean)this.vanillaOnGround.get() || this.mc.field_1724.method_24828() || this.speedMode.get() != SpeedModes.Vanilla) {
               if ((Boolean)this.inLiquids.get() || !this.mc.field_1724.method_5799() && !this.mc.field_1724.method_5771()) {
                  if ((Double)this.timer.get() != 1.0D) {
                     ((Timer)Modules.get().get(Timer.class)).setOverride(PlayerUtils.isMoving() ? (Double)this.timer.get() : 1.0D);
                  }

                  this.currentMode.onMove(event);
               }
            }
         }
      }
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      if (!this.mc.field_1724.method_6128() && !this.mc.field_1724.method_6101() && this.mc.field_1724.method_5854() == null) {
         if ((Boolean)this.whenSneaking.get() || !this.mc.field_1724.method_5715()) {
            if (!(Boolean)this.vanillaOnGround.get() || this.mc.field_1724.method_24828() || this.speedMode.get() != SpeedModes.Vanilla) {
               if ((Boolean)this.inLiquids.get() || !this.mc.field_1724.method_5799() && !this.mc.field_1724.method_5771()) {
                  this.currentMode.onTick();
               }
            }
         }
      }
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (event.packet instanceof class_2708) {
         this.currentMode.onRubberband();
      }

   }

   private void onSpeedModeChanged(SpeedModes mode) {
      switch(mode) {
      case Vanilla:
         this.currentMode = new Vanilla();
         break;
      case Strafe:
         this.currentMode = new Strafe();
      }

   }

   public String getInfoString() {
      return this.currentMode.getHudString();
   }
}
