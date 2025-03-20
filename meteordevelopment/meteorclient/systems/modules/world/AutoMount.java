package meteordevelopment.meteorclient.systems.modules.world;

import java.util.Iterator;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1452;
import net.minecraft.class_1501;
import net.minecraft.class_1506;
import net.minecraft.class_1507;
import net.minecraft.class_1826;
import net.minecraft.class_4985;
import net.minecraft.class_5146;

public class AutoMount extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> checkSaddle;
   private final Setting<Boolean> rotate;
   private final Setting<Set<class_1299<?>>> entities;

   public AutoMount() {
      super(Categories.World, "auto-mount", "Automatically mounts entities.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.checkSaddle = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("check-saddle")).description("Checks if the entity contains a saddle before mounting.")).defaultValue(false)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Faces the entity you mount.")).defaultValue(true)).build());
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Rideable entities.")).filter(EntityUtils::isRideable).build());
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (!this.mc.field_1724.method_5765()) {
         if (!this.mc.field_1724.method_5715()) {
            if (!(this.mc.field_1724.method_6047().method_7909() instanceof class_1826)) {
               Iterator var2 = this.mc.field_1687.method_18112().iterator();

               class_1297 entity;
               class_5146 saddleable;
               do {
                  do {
                     do {
                        do {
                           if (!var2.hasNext()) {
                              return;
                           }

                           entity = (class_1297)var2.next();
                        } while(!((Set)this.entities.get()).contains(entity.method_5864()));
                     } while(!PlayerUtils.isWithin(entity, 4.0D));
                  } while((entity instanceof class_1452 || entity instanceof class_1506 || entity instanceof class_4985 || entity instanceof class_1507) && !((class_5146)entity).method_6725());

                  if (entity instanceof class_1501 || !(entity instanceof class_5146)) {
                     break;
                  }

                  saddleable = (class_5146)entity;
               } while((Boolean)this.checkSaddle.get() && !saddleable.method_6725());

               this.interact(entity);
            }
         }
      }
   }

   private void interact(class_1297 entity) {
      if ((Boolean)this.rotate.get()) {
         Rotations.rotate(Rotations.getYaw(entity), Rotations.getPitch(entity), -100, () -> {
            this.mc.field_1761.method_2905(this.mc.field_1724, entity, class_1268.field_5808);
         });
      } else {
         this.mc.field_1761.method_2905(this.mc.field_1724, entity, class_1268.field_5808);
      }

   }
}
