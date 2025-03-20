package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1429;

public class AutoBreed extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Double> range;
   private final Setting<class_1268> hand;
   private final Setting<AutoBreed.EntityAge> mobAgeFilter;
   private final List<class_1297> animalsFed;

   public AutoBreed() {
      super(Categories.World, "auto-breed", "Automatically breeds specified animals.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to breed.")).defaultValue(class_1299.field_6139, class_1299.field_6067, class_1299.field_6085, class_1299.field_6143, class_1299.field_6115, class_1299.field_6093, class_1299.field_6132, class_1299.field_6055, class_1299.field_16281, class_1299.field_6081, class_1299.field_6140, class_1299.field_6074, class_1299.field_6113, class_1299.field_6146, class_1299.field_17943, class_1299.field_20346, class_1299.field_23214, class_1299.field_21973).onlyAttackable().build());
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("How far away the animals can be to be bred.")).min(0.0D).defaultValue(4.5D).build());
      this.hand = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("hand-for-breeding")).description("The hand to use for breeding.")).defaultValue(class_1268.field_5808)).build());
      this.mobAgeFilter = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mob-age-filter")).description("Determines the age of the mobs to target (baby, adult, or both).")).defaultValue(AutoBreed.EntityAge.Adult)).build());
      this.animalsFed = new ArrayList();
   }

   public void onActivate() {
      this.animalsFed.clear();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      while(true) {
         class_1297 entity;
         class_1429 animal;
         do {
            do {
               if (!var2.hasNext()) {
                  return;
               }

               entity = (class_1297)var2.next();
            } while(!(entity instanceof class_1429));

            animal = (class_1429)entity;
         } while(!((Set)this.entities.get()).contains(animal.method_5864()));

         switch(((AutoBreed.EntityAge)this.mobAgeFilter.get()).ordinal()) {
         case 0:
            if (!animal.method_6109()) {
               continue;
            }
            break;
         case 1:
            if (animal.method_6109()) {
               continue;
            }
         case 2:
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
         }

         if (!this.animalsFed.contains(animal) && PlayerUtils.isWithin((class_1297)animal, (Double)this.range.get()) && animal.method_6481(this.hand.get() == class_1268.field_5808 ? this.mc.field_1724.method_6047() : this.mc.field_1724.method_6079())) {
            Rotations.rotate(Rotations.getYaw(entity), Rotations.getPitch(entity), -100, () -> {
               this.mc.field_1761.method_2905(this.mc.field_1724, animal, (class_1268)this.hand.get());
               this.mc.field_1724.method_6104((class_1268)this.hand.get());
               this.animalsFed.add(animal);
            });
            return;
         }
      }
   }

   public static enum EntityAge {
      Baby,
      Adult,
      Both;

      // $FF: synthetic method
      private static AutoBreed.EntityAge[] $values() {
         return new AutoBreed.EntityAge[]{Baby, Adult, Both};
      }
   }
}
