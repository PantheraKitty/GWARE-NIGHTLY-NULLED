package meteordevelopment.meteorclient.systems.modules.player;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap.Entry;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.StatusEffectInstanceAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StatusEffectAmplifierMapSetting;
import meteordevelopment.meteorclient.settings.StatusEffectListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_7923;

public class PotionSpoof extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Reference2IntMap<class_1291>> spoofPotions;
   private final Setting<Boolean> clearEffects;
   private final Setting<List<class_1291>> antiPotion;
   private final Setting<Integer> effectDuration;

   public PotionSpoof() {
      super(Categories.Player, "potion-spoof", "Spoofs potion statuses for you. SOME effects DO NOT work.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.spoofPotions = this.sgGeneral.add(((StatusEffectAmplifierMapSetting.Builder)((StatusEffectAmplifierMapSetting.Builder)((StatusEffectAmplifierMapSetting.Builder)(new StatusEffectAmplifierMapSetting.Builder()).name("spoofed-potions")).description("Potions to add.")).defaultValue(Utils.createStatusEffectMap())).build());
      this.clearEffects = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("clear-effects")).description("Clears effects on module disable.")).defaultValue(true)).build());
      this.antiPotion = this.sgGeneral.add(((StatusEffectListSetting.Builder)((StatusEffectListSetting.Builder)(new StatusEffectListSetting.Builder()).name("blocked-potions")).description("Potions to block.")).defaultValue((class_1291)class_1294.field_5902.comp_349(), (class_1291)class_1294.field_5913.comp_349(), (class_1291)class_1294.field_5906.comp_349(), (class_1291)class_1294.field_5900.comp_349()).build());
      this.effectDuration = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("effect-duration")).description("How many ticks to spoof the effect for.")).range(1, 32767).sliderRange(20, 500).defaultValue(420)).build());
   }

   public void onDeactivate() {
      if ((Boolean)this.clearEffects.get() && Utils.canUpdate()) {
         ObjectIterator var1 = ((Reference2IntMap)this.spoofPotions.get()).reference2IntEntrySet().iterator();

         while(var1.hasNext()) {
            Entry<class_1291> entry = (Entry)var1.next();
            if (entry.getIntValue() > 0 && this.mc.field_1724.method_6059(class_7923.field_41174.method_47983((class_1291)entry.getKey()))) {
               this.mc.field_1724.method_6016(class_7923.field_41174.method_47983((class_1291)entry.getKey()));
            }
         }

      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      ObjectIterator var2 = ((Reference2IntMap)this.spoofPotions.get()).reference2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Entry<class_1291> entry = (Entry)var2.next();
         int level = entry.getIntValue();
         if (level > 0) {
            if (this.mc.field_1724.method_6059(class_7923.field_41174.method_47983((class_1291)entry.getKey()))) {
               class_1293 instance = this.mc.field_1724.method_6112(class_7923.field_41174.method_47983((class_1291)entry.getKey()));
               ((StatusEffectInstanceAccessor)instance).setAmplifier(level - 1);
               if (instance.method_5584() < (Integer)this.effectDuration.get()) {
                  ((StatusEffectInstanceAccessor)instance).setDuration((Integer)this.effectDuration.get());
               }
            } else {
               this.mc.field_1724.method_6092(new class_1293(class_7923.field_41174.method_47983((class_1291)entry.getKey()), (Integer)this.effectDuration.get(), level - 1));
            }
         }
      }

   }

   public boolean shouldBlock(class_1291 effect) {
      return this.isActive() && ((List)this.antiPotion.get()).contains(effect);
   }
}
