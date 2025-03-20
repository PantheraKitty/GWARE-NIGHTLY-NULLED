package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.Iterator;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ClientPlayerEntityAccessor;
import meteordevelopment.meteorclient.mixininterface.IHorseBaseEntity;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1496;

public class EntityControl extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> maxJump;

   public EntityControl() {
      super(Categories.Movement, "entity-control", "Lets you control rideable entities without a saddle.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.maxJump = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("max-jump")).description("Sets jump power to maximum.")).defaultValue(true)).build());
   }

   public void onDeactivate() {
      if (Utils.canUpdate() && this.mc.field_1687.method_18112() != null) {
         Iterator var1 = this.mc.field_1687.method_18112().iterator();

         while(var1.hasNext()) {
            class_1297 entity = (class_1297)var1.next();
            if (entity instanceof class_1496) {
               ((IHorseBaseEntity)entity).setSaddled(false);
            }
         }

      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      while(var2.hasNext()) {
         class_1297 entity = (class_1297)var2.next();
         if (entity instanceof class_1496) {
            ((IHorseBaseEntity)entity).setSaddled(true);
         }
      }

      if ((Boolean)this.maxJump.get()) {
         ((ClientPlayerEntityAccessor)this.mc.field_1724).setMountJumpStrength(1.0F);
      }

   }
}
