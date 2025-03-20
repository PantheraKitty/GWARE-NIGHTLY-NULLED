package meteordevelopment.meteorclient.systems.modules.world;

import java.util.Iterator;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3965;

public class Flamethrower extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> distance;
   private final Setting<Boolean> antiBreak;
   private final Setting<Boolean> putOutFire;
   private final Setting<Boolean> targetBabies;
   private final Setting<Integer> tickInterval;
   private final Setting<Boolean> rotate;
   private final Setting<Set<class_1299<?>>> entities;
   private class_1297 entity;
   private int ticks;
   private class_1268 hand;

   public Flamethrower() {
      super(Categories.World, "flamethrower", "Ignites every alive piece of food.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.distance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("distance")).description("The maximum distance the animal has to be to be roasted.")).min(0.0D).defaultValue(5.0D).build());
      this.antiBreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-break")).description("Prevents flint and steel from being broken.")).defaultValue(false)).build());
      this.putOutFire = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("put-out-fire")).description("Tries to put out the fire when animal is low health, so the items don't burn.")).defaultValue(true)).build());
      this.targetBabies = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("target-babies")).description("If checked babies will also be killed.")).defaultValue(false)).build());
      this.tickInterval = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("tick-interval")).defaultValue(5)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically faces towards the animal roasted.")).defaultValue(true)).build());
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to cook.")).defaultValue(class_1299.field_6093, class_1299.field_6085, class_1299.field_6115, class_1299.field_6132, class_1299.field_6140).build());
      this.ticks = 0;
   }

   public void onDeactivate() {
      this.entity = null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.entity = null;
      ++this.ticks;
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      class_1297 entity;
      do {
         do {
            do {
               do {
                  do {
                     if (!var2.hasNext()) {
                        return;
                     }

                     entity = (class_1297)var2.next();
                  } while(!((Set)this.entities.get()).contains(entity.method_5864()));
               } while(!PlayerUtils.isWithin(entity, (Double)this.distance.get()));
            } while(entity.method_5753());
         } while(entity == this.mc.field_1724);
      } while(!(Boolean)this.targetBabies.get() && entity instanceof class_1309 && ((class_1309)entity).method_6109());

      FindItemResult findFlintAndSteel = InvUtils.findInHotbar((itemStack) -> {
         return itemStack.method_7909() == class_1802.field_8884 && (!(Boolean)this.antiBreak.get() || itemStack.method_7919() < itemStack.method_7936() - 1);
      });
      if (InvUtils.swap(findFlintAndSteel.slot(), true)) {
         this.hand = findFlintAndSteel.getHand();
         this.entity = entity;
         if ((Boolean)this.rotate.get()) {
            Rotations.rotate(Rotations.getYaw(entity.method_24515()), Rotations.getPitch(entity.method_24515()), -100, this::interact);
         } else {
            this.interact();
         }

      }
   }

   private void interact() {
      class_2248 block = this.mc.field_1687.method_8320(this.entity.method_24515()).method_26204();
      class_2248 bottom = this.mc.field_1687.method_8320(this.entity.method_24515().method_10074()).method_26204();
      if (block != class_2246.field_10382 && bottom != class_2246.field_10382 && bottom != class_2246.field_10194) {
         if (block == class_2246.field_10219) {
            this.mc.field_1761.method_2910(this.entity.method_24515(), class_2350.field_11033);
         }

         label25: {
            if ((Boolean)this.putOutFire.get()) {
               class_1297 var4 = this.entity;
               if (var4 instanceof class_1309) {
                  class_1309 animal = (class_1309)var4;
                  if (animal.method_6032() < 1.0F) {
                     this.mc.field_1761.method_2910(this.entity.method_24515(), class_2350.field_11033);
                     this.mc.field_1761.method_2910(this.entity.method_24515().method_10067(), class_2350.field_11033);
                     this.mc.field_1761.method_2910(this.entity.method_24515().method_10078(), class_2350.field_11033);
                     this.mc.field_1761.method_2910(this.entity.method_24515().method_10095(), class_2350.field_11033);
                     this.mc.field_1761.method_2910(this.entity.method_24515().method_10072(), class_2350.field_11033);
                     break label25;
                  }
               }
            }

            if (this.ticks >= (Integer)this.tickInterval.get() && !this.entity.method_5809()) {
               this.mc.field_1761.method_2896(this.mc.field_1724, this.hand, new class_3965(this.entity.method_19538().method_1020(new class_243(0.0D, 1.0D, 0.0D)), class_2350.field_11036, this.entity.method_24515().method_10074(), false));
               this.ticks = 0;
            }
         }

         InvUtils.swapBack();
      }
   }
}
