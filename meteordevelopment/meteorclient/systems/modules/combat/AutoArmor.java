package meteordevelopment.meteorclient.systems.modules.combat;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnchantmentListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.ChestSwap;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1738;
import net.minecraft.class_1770;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_1893;
import net.minecraft.class_5321;
import net.minecraft.class_6880;

public class AutoArmor extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<AutoArmor.Protection> preferredProtection;
   private final Setting<Integer> delay;
   private final Setting<Set<class_5321<class_1887>>> avoidedEnchantments;
   private final Setting<Boolean> blastLeggings;
   private final Setting<Boolean> antiBreak;
   private final Setting<Boolean> ignoreElytra;
   private final Object2IntMap<class_6880<class_1887>> enchantments;
   private final AutoArmor.ArmorPiece[] armorPieces;
   private final AutoArmor.ArmorPiece helmet;
   private final AutoArmor.ArmorPiece chestplate;
   private final AutoArmor.ArmorPiece leggings;
   private final AutoArmor.ArmorPiece boots;
   private int timer;

   public AutoArmor() {
      super(Categories.Combat, "auto-armor", "Automatically equips armor.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.preferredProtection = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("preferred-protection")).description("Which type of protection to prefer.")).defaultValue(AutoArmor.Protection.Protection)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("swap-delay")).description("The delay between equipping armor pieces.")).defaultValue(1)).min(0).sliderMax(5).build());
      this.avoidedEnchantments = this.sgGeneral.add(((EnchantmentListSetting.Builder)((EnchantmentListSetting.Builder)(new EnchantmentListSetting.Builder()).name("avoided-enchantments")).description("Enchantments that should be avoided.")).defaultValue(class_1893.field_9113, class_1893.field_9122).build());
      this.blastLeggings = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("blast-prot-leggings")).description("Uses blast protection for leggings regardless of preferred protection.")).defaultValue(true)).build());
      this.antiBreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-break")).description("Takes off armor if it is about to break.")).defaultValue(false)).build());
      this.ignoreElytra = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-elytra")).description("Will not replace your elytra if you have it equipped.")).defaultValue(true)).build());
      this.enchantments = new Object2IntOpenHashMap();
      this.armorPieces = new AutoArmor.ArmorPiece[4];
      this.helmet = new AutoArmor.ArmorPiece(3);
      this.chestplate = new AutoArmor.ArmorPiece(2);
      this.leggings = new AutoArmor.ArmorPiece(1);
      this.boots = new AutoArmor.ArmorPiece(0);
      this.armorPieces[0] = this.helmet;
      this.armorPieces[1] = this.chestplate;
      this.armorPieces[2] = this.leggings;
      this.armorPieces[3] = this.boots;
   }

   public void onActivate() {
      this.timer = 0;
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      if (this.timer > 0) {
         --this.timer;
      } else {
         AutoArmor.ArmorPiece[] var2 = this.armorPieces;
         int var3 = var2.length;

         int var4;
         AutoArmor.ArmorPiece armorPiece;
         for(var4 = 0; var4 < var3; ++var4) {
            armorPiece = var2[var4];
            armorPiece.reset();
         }

         for(int i = 0; i < this.mc.field_1724.method_31548().field_7547.size(); ++i) {
            class_1799 itemStack = this.mc.field_1724.method_31548().method_5438(i);
            if (!itemStack.method_7960() && itemStack.method_7909() instanceof class_1738 && (!(Boolean)this.antiBreak.get() || !itemStack.method_7963() || itemStack.method_7936() - itemStack.method_7919() > 10)) {
               Utils.getEnchantments(itemStack, this.enchantments);
               if (!this.hasAvoidedEnchantment()) {
                  switch(this.getItemSlotId(itemStack)) {
                  case 0:
                     this.boots.add(itemStack, i);
                     break;
                  case 1:
                     this.leggings.add(itemStack, i);
                     break;
                  case 2:
                     this.chestplate.add(itemStack, i);
                     break;
                  case 3:
                     this.helmet.add(itemStack, i);
                  }
               }
            }
         }

         var2 = this.armorPieces;
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            armorPiece = var2[var4];
            armorPiece.calculate();
         }

         Arrays.sort(this.armorPieces, Comparator.comparingInt(AutoArmor.ArmorPiece::getSortScore));
         var2 = this.armorPieces;
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            armorPiece = var2[var4];
            armorPiece.apply();
         }

      }
   }

   private boolean hasAvoidedEnchantment() {
      ObjectIterator var1 = this.enchantments.keySet().iterator();

      Set var10001;
      class_6880 enchantment;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         enchantment = (class_6880)var1.next();
         var10001 = (Set)this.avoidedEnchantments.get();
         Objects.requireNonNull(var10001);
      } while(!enchantment.method_40224(var10001::contains));

      return true;
   }

   private int getItemSlotId(class_1799 itemStack) {
      return itemStack.method_7909() instanceof class_1770 ? 2 : ((class_1738)itemStack.method_7909()).method_7685().method_5927();
   }

   private int getScore(class_1799 itemStack) {
      if (itemStack.method_7960()) {
         return 0;
      } else {
         int score = 0;
         class_5321<class_1887> protection = ((AutoArmor.Protection)this.preferredProtection.get()).enchantment;
         if (itemStack.method_7909() instanceof class_1738 && (Boolean)this.blastLeggings.get() && this.getItemSlotId(itemStack) == 1) {
            protection = class_1893.field_9107;
         }

         int score = score + 3 * Utils.getEnchantmentLevel(this.enchantments, protection);
         score += Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9111);
         score += Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9107);
         score += Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9095);
         score += Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9096);
         score += Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9119);
         score += 2 * Utils.getEnchantmentLevel(this.enchantments, class_1893.field_9101);
         class_1792 var5 = itemStack.method_7909();
         int var10001;
         class_1738 armorItem;
         if (var5 instanceof class_1738) {
            armorItem = (class_1738)var5;
            var10001 = armorItem.method_7687();
         } else {
            var10001 = 0;
         }

         score += var10001;
         var5 = itemStack.method_7909();
         if (var5 instanceof class_1738) {
            armorItem = (class_1738)var5;
            var10001 = (int)armorItem.method_26353();
         } else {
            var10001 = 0;
         }

         score += var10001;
         return score;
      }
   }

   private boolean cannotSwap() {
      return this.timer > 0;
   }

   private void swap(int from, int armorSlotId) {
      InvUtils.move().from(from).toArmor(armorSlotId);
      this.timer = (Integer)this.delay.get();
   }

   private void moveToEmpty(int armorSlotId) {
      for(int i = 0; i < this.mc.field_1724.method_31548().field_7547.size(); ++i) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_7960()) {
            InvUtils.move().fromArmor(armorSlotId).to(i);
            this.timer = (Integer)this.delay.get();
            break;
         }
      }

   }

   public static enum Protection {
      Protection(class_1893.field_9111),
      BlastProtection(class_1893.field_9107),
      FireProtection(class_1893.field_9095),
      ProjectileProtection(class_1893.field_9096);

      private final class_5321<class_1887> enchantment;

      private Protection(class_5321<class_1887> enchantment) {
         this.enchantment = enchantment;
      }

      // $FF: synthetic method
      private static AutoArmor.Protection[] $values() {
         return new AutoArmor.Protection[]{Protection, BlastProtection, FireProtection, ProjectileProtection};
      }
   }

   private class ArmorPiece {
      private final int id;
      private int bestSlot;
      private int bestScore;
      private int score;
      private int durability;

      public ArmorPiece(int id) {
         this.id = id;
      }

      public void reset() {
         this.bestSlot = -1;
         this.bestScore = -1;
         this.score = -1;
         this.durability = Integer.MAX_VALUE;
      }

      public void add(class_1799 itemStack, int slot) {
         int score = AutoArmor.this.getScore(itemStack);
         if (score > this.bestScore) {
            this.bestScore = score;
            this.bestSlot = slot;
         }

      }

      public void calculate() {
         if (!AutoArmor.this.cannotSwap()) {
            class_1799 itemStack = AutoArmor.this.mc.field_1724.method_31548().method_7372(this.id);
            if (((Boolean)AutoArmor.this.ignoreElytra.get() || Modules.get().isActive(ChestSwap.class)) && itemStack.method_7909() == class_1802.field_8833) {
               this.score = Integer.MAX_VALUE;
            } else {
               Utils.getEnchantments(itemStack, AutoArmor.this.enchantments);
               if (AutoArmor.this.enchantments.containsKey(class_1893.field_9113)) {
                  this.score = Integer.MAX_VALUE;
               } else {
                  this.score = AutoArmor.this.getScore(itemStack);
                  this.score = this.decreaseScoreByAvoidedEnchantments(this.score);
                  this.score = this.applyAntiBreakScore(this.score, itemStack);
                  if (!itemStack.method_7960()) {
                     this.durability = itemStack.method_7936() - itemStack.method_7919();
                  }

               }
            }
         }
      }

      public int getSortScore() {
         return (Boolean)AutoArmor.this.antiBreak.get() && this.durability <= 10 ? -1 : this.bestScore;
      }

      public void apply() {
         if (!AutoArmor.this.cannotSwap() && this.score != Integer.MAX_VALUE) {
            if (this.bestScore > this.score) {
               AutoArmor.this.swap(this.bestSlot, this.id);
            } else if ((Boolean)AutoArmor.this.antiBreak.get() && this.durability <= 10) {
               AutoArmor.this.moveToEmpty(this.id);
            }

         }
      }

      private int decreaseScoreByAvoidedEnchantments(int score) {
         class_5321 enchantment;
         for(Iterator var2 = ((Set)AutoArmor.this.avoidedEnchantments.get()).iterator(); var2.hasNext(); score -= 2 * AutoArmor.this.enchantments.getInt(enchantment)) {
            enchantment = (class_5321)var2.next();
         }

         return score;
      }

      private int applyAntiBreakScore(int score, class_1799 itemStack) {
         return (Boolean)AutoArmor.this.antiBreak.get() && itemStack.method_7963() && itemStack.method_7936() - itemStack.method_7919() <= 10 ? -1 : score;
      }
   }
}
