package meteordevelopment.meteorclient.systems.modules.misc;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1268;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2813;
import net.minecraft.class_2815;
import net.minecraft.class_2828;
import net.minecraft.class_2846;
import net.minecraft.class_2886;
import net.minecraft.class_3545;
import net.minecraft.class_419;
import net.minecraft.class_5250;
import net.minecraft.class_2846.class_2847;

public class TridentDupe extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> delay;
   private final Setting<Boolean> dropTridents;
   private final Setting<Boolean> durabilityManagement;
   private final Queue<class_2596<?>> delayedPackets;
   private boolean cancel;
   private final List<class_3545<Long, Runnable>> scheduledTasks;
   private final List<class_3545<Long, Runnable>> scheduledTasks2;

   public TridentDupe() {
      super(Categories.Misc, "trident-dupe", "Dupes tridents");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.delay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("dupe-delay")).description("Raise this if it isn't working. This is how fast you'll dupe. 5 is good for most.")).defaultValue(5.0D).build());
      this.dropTridents = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dropTridents")).description("Drops tridents in your last hotbar slot.")).defaultValue(true)).build());
      this.durabilityManagement = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("durabilityManagement")).description("(More AFKable) Attempts to dupe the highest durability trident in your hotbar.")).defaultValue(true)).build());
      this.delayedPackets = new LinkedList();
      this.cancel = true;
      this.scheduledTasks = new ArrayList();
      this.scheduledTasks2 = new ArrayList();
   }

   @EventHandler(
      priority = 201
   )
   private void onSendPacket(PacketEvent.Send event) {
      if (!(event.packet instanceof class_2828) && !(event.packet instanceof class_2815)) {
         if (event.packet instanceof class_2813 || event.packet instanceof class_2846) {
            if (this.cancel) {
               class_5250 packetStr = class_2561.method_43470(event.packet.toString()).method_27692(class_124.field_1068);
               event.cancel();
            }
         }
      }
   }

   public void onActivate() {
      if (this.mc.field_1724 != null) {
         for(int i = 0; i < 9; ++i) {
            if (this.mc.field_1724.method_31548().method_5438(i).method_7909() == class_1802.field_8547) {
               Integer var2 = this.mc.field_1724.method_31548().method_5438(i).method_7919();
            }
         }

         new class_2886(class_1268.field_5808, 10, -57.0F, 66.29F);
         Int2ObjectMap<class_1799> modifiedStacks = new Int2ObjectOpenHashMap();
         modifiedStacks.put(3, this.mc.field_1724.method_31548().method_5438(this.mc.field_1724.method_31548().field_7545));
         modifiedStacks.put(36, this.mc.field_1724.method_31548().method_5438(this.mc.field_1724.method_31548().field_7545));
         new class_2813(0, 15, 0, 0, class_1713.field_7791, new class_1799(class_1802.field_8162), modifiedStacks);
         this.scheduledTasks.clear();
         this.dupe();
      }
   }

   private void dupe() {
      int delayInt = ((Double)this.delay.get()).intValue() * 100;
      System.out.println(delayInt);
      int lowestHotbarSlot = 0;
      int lowestHotbarDamage = 1000;

      for(int i = 0; i < 9; ++i) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_7909() == class_1802.field_8547) {
            Integer currentHotbarDamage = this.mc.field_1724.method_31548().method_5438(i).method_7919();
            if (lowestHotbarDamage > currentHotbarDamage) {
               lowestHotbarSlot = i;
               lowestHotbarDamage = currentHotbarDamage;
            }
         }
      }

      this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
      this.cancel = true;
      this.scheduleTask(() -> {
         this.cancel = false;
         if ((Boolean)this.durabilityManagement.get() && lowestHotbarSlot != 0) {
            this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, 44, 0, class_1713.field_7791, this.mc.field_1724);
            if ((Boolean)this.dropTridents.get()) {
               this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, 44, 0, class_1713.field_7795, this.mc.field_1724);
            }

            this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, 36 + lowestHotbarSlot, 0, class_1713.field_7791, this.mc.field_1724);
         }

         this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, 3, 0, class_1713.field_7791, this.mc.field_1724);
         class_2846 packet2 = new class_2846(class_2847.field_12974, class_2338.field_10980, class_2350.field_11033, 0);
         this.mc.method_1562().method_52787(packet2);
         if ((Boolean)this.dropTridents.get()) {
            this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, 44, 0, class_1713.field_7795, this.mc.field_1724);
         }

         this.cancel = true;
         this.scheduleTask2(this::dupe, (long)delayInt);
      }, (long)delayInt);
   }

   public void scheduleTask(Runnable task, long delayMillis) {
      long executeTime = System.currentTimeMillis() + delayMillis;
      this.scheduledTasks.add(new class_3545(executeTime, task));
   }

   public void scheduleTask2(Runnable task, long delayMillis) {
      long executeTime = System.currentTimeMillis() + delayMillis;
      this.scheduledTasks2.add(new class_3545(executeTime, task));
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      long currentTime = System.currentTimeMillis();
      Iterator iterator = this.scheduledTasks.iterator();

      class_3545 entry;
      while(iterator.hasNext()) {
         entry = (class_3545)iterator.next();
         if ((Long)entry.method_15442() <= currentTime) {
            ((Runnable)entry.method_15441()).run();
            iterator.remove();
         }
      }

      iterator = this.scheduledTasks2.iterator();

      while(iterator.hasNext()) {
         entry = (class_3545)iterator.next();
         if ((Long)entry.method_15442() <= currentTime) {
            ((Runnable)entry.method_15441()).run();
            iterator.remove();
         }
      }

   }

   @EventHandler
   private void onGameLeft(GameLeftEvent event) {
      this.toggle();
   }

   @EventHandler
   private void onScreenOpen(OpenScreenEvent event) {
      if (event.screen instanceof class_419) {
         this.toggle();
      }

   }
}
