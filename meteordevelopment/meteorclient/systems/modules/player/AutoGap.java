package meteordevelopment.meteorclient.systems.modules.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.ItemUseCrosshairTargetEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
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
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_6880;

public class AutoGap extends Module {
   private static final Class<? extends Module>[] AURAS = new Class[]{KillAura.class, AnchorAura.class, BedAura.class};
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPotions;
   private final SettingGroup sgHealth;
   private final Setting<Boolean> allowEgap;
   private final Setting<Boolean> always;
   private final Setting<Boolean> pauseAuras;
   private final Setting<Boolean> pauseBaritone;
   private final Setting<Boolean> potionsRegeneration;
   private final Setting<Boolean> potionsFireResistance;
   private final Setting<Boolean> potionsResistance;
   private final Setting<Boolean> healthEnabled;
   private final Setting<Integer> healthThreshold;
   private boolean requiresEGap;
   private boolean eating;
   private int slot;
   private int prevSlot;
   private final List<Class<? extends Module>> wasAura;
   private boolean wasBaritone;

   public AutoGap() {
      super(Categories.Player, "auto-gap", "Automatically eats Gaps or E-Gaps.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPotions = this.settings.createGroup("Potions");
      this.sgHealth = this.settings.createGroup("Health");
      this.allowEgap = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("allow-egap")).description("Allow eating E-Gaps over Gaps if found.")).defaultValue(true)).build());
      this.always = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("always")).description("If it should always eat.")).defaultValue(false)).build());
      this.pauseAuras = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-auras")).description("Pauses all auras when eating.")).defaultValue(true)).build());
      this.pauseBaritone = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-baritone")).description("Pause baritone when eating.")).defaultValue(true)).build());
      this.potionsRegeneration = this.sgPotions.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("potions-regeneration")).description("If it should eat when Regeneration runs out.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgPotions;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("potions-fire-resistance")).description("If it should eat when Fire Resistance runs out. Requires E-Gaps.")).defaultValue(true);
      Setting var10003 = this.allowEgap;
      Objects.requireNonNull(var10003);
      this.potionsFireResistance = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgPotions;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("potions-absorption")).description("If it should eat when Resistance runs out. Requires E-Gaps.")).defaultValue(false);
      var10003 = this.allowEgap;
      Objects.requireNonNull(var10003);
      this.potionsResistance = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.healthEnabled = this.sgHealth.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("health-enabled")).description("If it should eat when health drops below threshold.")).defaultValue(true)).build());
      this.healthThreshold = this.sgHealth.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("health-threshold")).description("Health threshold to eat at. Includes absorption.")).defaultValue(20)).min(0).sliderMax(40).build());
      this.wasAura = new ArrayList();
   }

   public void onDeactivate() {
      if (this.eating) {
         this.stopEating();
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.eating) {
         if (this.shouldEat()) {
            if (this.isNotGapOrEGap(this.mc.field_1724.method_31548().method_5438(this.slot))) {
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

      this.wasBaritone = false;
      if ((Boolean)this.pauseBaritone.get() && PathManagers.get().isPathing()) {
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

   private boolean shouldEat() {
      this.requiresEGap = false;
      if ((Boolean)this.always.get()) {
         return true;
      } else {
         return this.shouldEatPotions() ? true : this.shouldEatHealth();
      }
   }

   private boolean shouldEatPotions() {
      Map<class_6880<class_1291>, class_1293> effects = this.mc.field_1724.method_6088();
      if ((Boolean)this.potionsRegeneration.get() && !effects.containsKey(class_1294.field_5924)) {
         return true;
      } else if ((Boolean)this.potionsFireResistance.get() && !effects.containsKey(class_1294.field_5918)) {
         this.requiresEGap = true;
         return true;
      } else if ((Boolean)this.potionsResistance.get() && !effects.containsKey(class_1294.field_5907)) {
         this.requiresEGap = true;
         return true;
      } else {
         return false;
      }
   }

   private boolean shouldEatHealth() {
      if (!(Boolean)this.healthEnabled.get()) {
         return false;
      } else {
         int health = Math.round(this.mc.field_1724.method_6032() + this.mc.field_1724.method_6067());
         return health < (Integer)this.healthThreshold.get();
      }
   }

   private int findSlot() {
      boolean preferEGap = (Boolean)this.allowEgap.get() || this.requiresEGap;
      int slot = -1;

      for(int i = 0; i < 9; ++i) {
         class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
         if (!stack.method_7960() && !this.isNotGapOrEGap(stack)) {
            class_1792 item = stack.method_7909();
            if (item == class_1802.field_8367 && preferEGap) {
               slot = i;
               break;
            }

            if (item == class_1802.field_8463 && !this.requiresEGap) {
               slot = i;
               if (!preferEGap) {
                  break;
               }
            }
         }
      }

      return slot;
   }

   private boolean isNotGapOrEGap(class_1799 stack) {
      class_1792 item = stack.method_7909();
      return item != class_1802.field_8463 && item != class_1802.field_8367;
   }

   public boolean isEating() {
      return this.isActive() && this.eating;
   }
}
