package meteordevelopment.meteorclient.systems.modules.player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import meteordevelopment.meteorclient.events.entity.player.ItemUseCrosshairTargetEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.AnchorAura;
import meteordevelopment.meteorclient.systems.modules.combat.BedAura;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_4174;
import net.minecraft.class_9334;

public class AutoEat extends Module {
   private static final Class<? extends Module>[] AURAS = new Class[]{KillAura.class, AnchorAura.class, BedAura.class};
   private final SettingGroup sgGeneral;
   private final SettingGroup sgThreshold;
   private final Setting<List<class_1792>> blacklist;
   private final Setting<Boolean> pauseAuras;
   private final Setting<Boolean> pauseBaritone;
   private final Setting<AutoEat.ThresholdMode> thresholdMode;
   private final Setting<Double> healthThreshold;
   private final Setting<Integer> hungerThreshold;
   public boolean eating;
   private int slot;
   private int prevSlot;
   private final List<Class<? extends Module>> wasAura;
   private boolean wasBaritone;

   public AutoEat() {
      super(Categories.Player, "auto-eat", "Automatically eats food.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgThreshold = this.settings.createGroup("Threshold");
      this.blacklist = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("blacklist")).description("Which items to not eat.")).defaultValue(class_1802.field_8367, class_1802.field_8463, class_1802.field_8233, class_1802.field_8635, class_1802.field_8323, class_1802.field_8726, class_1802.field_8511, class_1802.field_8680, class_1802.field_8766).filter((item) -> {
         return item.method_57347().method_57829(class_9334.field_50075) != null;
      }).build());
      this.pauseAuras = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-auras")).description("Pauses all auras when eating.")).defaultValue(true)).build());
      this.pauseBaritone = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-baritone")).description("Pause baritone when eating.")).defaultValue(true)).build());
      this.thresholdMode = this.sgThreshold.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("threshold-mode")).description("The threshold mode to trigger auto eat.")).defaultValue(AutoEat.ThresholdMode.Any)).build());
      this.healthThreshold = this.sgThreshold.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("health-threshold")).description("The level of health you eat at.")).defaultValue(10.0D).range(1.0D, 19.0D).sliderRange(1.0D, 19.0D).visible(() -> {
         return this.thresholdMode.get() != AutoEat.ThresholdMode.Hunger;
      })).build());
      this.hungerThreshold = this.sgThreshold.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("hunger-threshold")).description("The level of hunger you eat at.")).defaultValue(16)).range(1, 19).sliderRange(1, 19).visible(() -> {
         return this.thresholdMode.get() != AutoEat.ThresholdMode.Health;
      })).build());
      this.wasAura = new ArrayList();
      this.wasBaritone = false;
   }

   public void onDeactivate() {
      if (this.eating) {
         this.stopEating();
      }

   }

   @EventHandler(
      priority = -100
   )
   private void onTick(TickEvent.Pre event) {
      if (!((AutoGap)Modules.get().get(AutoGap.class)).isEating()) {
         if (this.eating) {
            if (this.shouldEat()) {
               if (this.mc.field_1724.method_31548().method_5438(this.slot).method_57824(class_9334.field_50075) != null) {
                  int slot = this.findSlot();
                  if (slot == -1) {
                     this.stopEating();
                     return;
                  }

                  this.changeSlot(slot);
               }

               this.eat();
            } else {
               this.stopEating();
            }
         } else if (this.shouldEat()) {
            this.slot = this.findSlot();
            if (this.slot != -1) {
               this.startEating();
            }
         }

      }
   }

   @EventHandler
   private void onItemUseCrosshairTarget(ItemUseCrosshairTargetEvent event) {
      if (this.eating) {
         event.target = null;
      }

   }

   private void startEating() {
      this.prevSlot = this.mc.field_1724.method_31548().field_7545;
      this.eat();
      this.wasAura.clear();
      if ((Boolean)this.pauseAuras.get()) {
         Class[] var1 = AURAS;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Class<? extends Module> klass = var1[var3];
            Module module = Modules.get().get(klass);
            if (module.isActive()) {
               this.wasAura.add(klass);
               module.toggle();
            }
         }
      }

      if ((Boolean)this.pauseBaritone.get() && PathManagers.get().isPathing() && !this.wasBaritone) {
         this.wasBaritone = true;
         PathManagers.get().pause();
      }

   }

   private void eat() {
      this.changeSlot(this.slot);
      this.setPressed(true);
      if (!this.mc.field_1724.method_6115()) {
         Utils.rightClick();
      }

      this.eating = true;
   }

   private void stopEating() {
      this.changeSlot(this.prevSlot);
      this.setPressed(false);
      this.eating = false;
      if ((Boolean)this.pauseAuras.get()) {
         Class[] var1 = AURAS;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Class<? extends Module> klass = var1[var3];
            Module module = Modules.get().get(klass);
            if (this.wasAura.contains(klass) && !module.isActive()) {
               module.toggle();
            }
         }
      }

      if ((Boolean)this.pauseBaritone.get() && this.wasBaritone) {
         this.wasBaritone = false;
         PathManagers.get().resume();
      }

   }

   private void setPressed(boolean pressed) {
      this.mc.field_1690.field_1904.method_23481(pressed);
   }

   private void changeSlot(int slot) {
      InvUtils.swap(slot, false);
      this.slot = slot;
   }

   public boolean shouldEat() {
      boolean health = (double)this.mc.field_1724.method_6032() <= (Double)this.healthThreshold.get();
      boolean hunger = this.mc.field_1724.method_7344().method_7586() <= (Integer)this.hungerThreshold.get();
      return ((AutoEat.ThresholdMode)this.thresholdMode.get()).test(health, hunger);
   }

   private int findSlot() {
      int slot = -1;
      int bestHunger = -1;

      for(int i = 0; i < 9; ++i) {
         class_1792 item = this.mc.field_1724.method_31548().method_5438(i).method_7909();
         class_4174 foodComponent = (class_4174)item.method_57347().method_57829(class_9334.field_50075);
         if (foodComponent != null) {
            int hunger = foodComponent.comp_2491();
            if (hunger > bestHunger && !((List)this.blacklist.get()).contains(item)) {
               slot = i;
               bestHunger = hunger;
            }
         }
      }

      class_1792 offHandItem = this.mc.field_1724.method_6079().method_7909();
      if (offHandItem.method_57347().method_57829(class_9334.field_50075) != null && !((List)this.blacklist.get()).contains(offHandItem) && ((class_4174)offHandItem.method_57347().method_57829(class_9334.field_50075)).comp_2491() > bestHunger) {
         slot = 45;
      }

      return slot;
   }

   public static enum ThresholdMode {
      Health((health, hunger) -> {
         return health;
      }),
      Hunger((health, hunger) -> {
         return hunger;
      }),
      Any((health, hunger) -> {
         return health || hunger;
      }),
      Both((health, hunger) -> {
         return health && hunger;
      });

      private final BiPredicate<Boolean, Boolean> predicate;

      private ThresholdMode(BiPredicate<Boolean, Boolean> predicate) {
         this.predicate = predicate;
      }

      public boolean test(boolean health, boolean hunger) {
         return this.predicate.test(health, hunger);
      }

      // $FF: synthetic method
      private static AutoEat.ThresholdMode[] $values() {
         return new AutoEat.ThresholdMode[]{Health, Hunger, Any, Both};
      }
   }
}
