package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.entity.LivingEntityMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1309;
import net.minecraft.class_243;

public class EntitySpeed extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> speed;
   private final Setting<Boolean> onlyOnGround;
   private final Setting<Boolean> inWater;

   public EntitySpeed() {
      super(Categories.Movement, "entity-speed", "Makes you go faster when riding entities.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.speed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("speed")).description("Horizontal speed in blocks per second.")).defaultValue(10.0D).min(0.0D).sliderMax(50.0D).build());
      this.onlyOnGround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Use speed only when standing on a block.")).defaultValue(false)).build());
      this.inWater = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("in-water")).description("Use speed when in water.")).defaultValue(false)).build());
   }

   @EventHandler
   private void onLivingEntityMove(LivingEntityMoveEvent event) {
      if (event.entity.method_5642() == this.mc.field_1724) {
         class_1309 entity = event.entity;
         if (!(Boolean)this.onlyOnGround.get() || entity.method_24828()) {
            if ((Boolean)this.inWater.get() || !entity.method_5799()) {
               class_243 vel = PlayerUtils.getHorizontalVelocity((Double)this.speed.get());
               ((IVec3d)event.movement).setXZ(vel.field_1352, vel.field_1350);
            }
         }
      }
   }
}
