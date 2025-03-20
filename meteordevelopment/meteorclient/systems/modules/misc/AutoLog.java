package meteordevelopment.meteorclient.systems.modules.misc;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Iterator;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2661;
import net.minecraft.class_2663;
import net.minecraft.class_5250;

public class AutoLog extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgEntities;
   private final SettingGroup sgAntiChainPop;
   private final Setting<Integer> health;
   private final Setting<Boolean> smart;
   private final Setting<Boolean> onlyTrusted;
   private final Setting<Boolean> instantDeath;
   private final Setting<Boolean> smartToggle;
   private final Setting<Boolean> toggleOff;
   private final Setting<Boolean> toggleAutoRecconect;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Boolean> useTotalCount;
   private final Setting<Integer> combinedEntityThreshold;
   private final Setting<Integer> individualEntityThreshold;
   private final Setting<Integer> range;
   private final Setting<Boolean> antiChainPop;
   private final Setting<Integer> chainPopLogCount;
   private final Object2IntMap<class_1299<?>> entityCounts;
   private LongSet chainPops;
   private final AutoLog.StaticListener staticListener;

   public AutoLog() {
      super(Categories.Combat, "auto-log", "Automatically disconnects you when certain requirements are met.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgEntities = this.settings.createGroup("Entities");
      this.sgAntiChainPop = this.settings.createGroup("Chain Pop");
      this.health = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("health")).description("Automatically disconnects when health is lower or equal to this value. Set to 0 to disable.")).defaultValue(6)).range(0, 19).sliderMax(19).build());
      this.smart = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smart")).description("Disconnects when it detects you're about to take enough damage to set you under the 'health' setting.")).defaultValue(true)).build());
      this.onlyTrusted = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-trusted")).description("Disconnects when a player not on your friends list appears in render distance.")).defaultValue(false)).build());
      this.instantDeath = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("32K")).description("Disconnects when a player near you can instantly kill you.")).defaultValue(false)).build());
      this.smartToggle = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smart-toggle")).description("Disables Auto Log after a low-health logout. WILL re-enable once you heal.")).defaultValue(false)).build());
      this.toggleOff = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-off")).description("Disables Auto Log after usage.")).defaultValue(true)).build());
      this.toggleAutoRecconect = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-auto-reconnect")).description("Disables Auto Reconnect after usage.")).defaultValue(true)).build());
      this.entities = this.sgEntities.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Disconnects when a specified entity is present within a specified range.")).defaultValue(class_1299.field_6110).build());
      this.useTotalCount = this.sgEntities.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("use-total-count")).description("Toggle between counting the total number of all selected entities or each entity individually.")).defaultValue(true)).visible(() -> {
         return !((Set)this.entities.get()).isEmpty();
      })).build());
      this.combinedEntityThreshold = this.sgEntities.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("combined-entity-threshold")).description("The minimum total number of selected entities that must be near you before disconnection occurs.")).defaultValue(10)).min(1).sliderMax(32).visible(() -> {
         return (Boolean)this.useTotalCount.get() && !((Set)this.entities.get()).isEmpty();
      })).build());
      this.individualEntityThreshold = this.sgEntities.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("individual-entity-threshold")).description("The minimum number of entities individually that must be near you before disconnection occurs.")).defaultValue(2)).min(1).sliderMax(16).visible(() -> {
         return !(Boolean)this.useTotalCount.get() && !((Set)this.entities.get()).isEmpty();
      })).build());
      this.range = this.sgEntities.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("range")).description("How close an entity has to be to you before you disconnect.")).defaultValue(5)).min(1).sliderMax(16).visible(() -> {
         return !((Set)this.entities.get()).isEmpty();
      })).build());
      this.antiChainPop = this.sgAntiChainPop.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-chain-pop")).description("Toggle between counting the total number of all selected entities or each entity individually.")).defaultValue(true)).build());
      this.chainPopLogCount = this.sgAntiChainPop.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("consecutive-pops-to-log")).description("Number of pops to take before disconnecting.")).defaultValue(3)).min(1).sliderMax(4).visible(() -> {
         return (Boolean)this.antiChainPop.get();
      })).build());
      this.entityCounts = new Object2IntOpenHashMap();
      this.chainPops = new LongOpenHashSet();
      this.staticListener = new AutoLog.StaticListener();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      long currentTime = System.currentTimeMillis();
      int popsWithinLastSeconds = 0;
      LongIterator var5 = this.chainPops.iterator();

      while(var5.hasNext()) {
         Long time = (Long)var5.next();
         double difference = (double)(currentTime - time) / 1000.0D;
         if (difference < 2.0D) {
            ++popsWithinLastSeconds;
         }
      }

      if (popsWithinLastSeconds > (Integer)this.chainPopLogCount.get()) {
         this.disconnect("Popped " + popsWithinLastSeconds + " totems within 1.5 seconds");
      } else {
         float playerHealth = this.mc.field_1724.method_6032();
         if (playerHealth <= 0.0F) {
            this.toggle();
         } else if (playerHealth <= (float)(Integer)this.health.get()) {
            this.disconnect("Health was lower than " + String.valueOf(this.health.get()) + ".");
            if ((Boolean)this.smartToggle.get()) {
               if (this.isActive()) {
                  this.toggle();
               }

               this.enableHealthListener();
            } else if ((Boolean)this.toggleOff.get()) {
               this.toggle();
            }

         } else if ((Boolean)this.smart.get() && playerHealth + this.mc.field_1724.method_6067() - PlayerUtils.possibleHealthReductions() < (float)(Integer)this.health.get()) {
            this.disconnect("Health was going to be lower than " + String.valueOf(this.health.get()) + ".");
            if ((Boolean)this.toggleOff.get()) {
               this.toggle();
            }

         } else if ((Boolean)this.onlyTrusted.get() || (Boolean)this.instantDeath.get() || !((Set)this.entities.get()).isEmpty()) {
            Iterator var10 = this.mc.field_1687.method_18112().iterator();

            while(var10.hasNext()) {
               class_1297 entity = (class_1297)var10.next();
               if (entity instanceof class_1657) {
                  class_1657 player = (class_1657)entity;
                  if (player.method_5667() != this.mc.field_1724.method_5667()) {
                     if ((Boolean)this.onlyTrusted.get() && player != this.mc.field_1724 && !Friends.get().isFriend(player)) {
                        String var10001 = String.valueOf(class_124.field_1061);
                        this.disconnect((class_2561)class_2561.method_43470("Non-trusted player '" + var10001 + player.method_5477().getString() + String.valueOf(class_124.field_1068) + "' appeared in your render distance."));
                        if ((Boolean)this.toggleOff.get()) {
                           this.toggle();
                        }

                        return;
                     }

                     if ((Boolean)this.instantDeath.get() && PlayerUtils.isWithin(entity, 8.0D) && DamageUtils.getAttackDamage(player, this.mc.field_1724) > playerHealth + this.mc.field_1724.method_6067()) {
                        this.disconnect("Anti-32k measures.");
                        if ((Boolean)this.toggleOff.get()) {
                           this.toggle();
                        }

                        return;
                     }
                  }
               }
            }

            if (!((Set)this.entities.get()).isEmpty()) {
               int totalEntities = 0;
               this.entityCounts.clear();
               Iterator var13 = this.mc.field_1687.method_18112().iterator();

               while(var13.hasNext()) {
                  class_1297 entity = (class_1297)var13.next();
                  if (PlayerUtils.isWithin(entity, (double)(Integer)this.range.get()) && ((Set)this.entities.get()).contains(entity.method_5864())) {
                     ++totalEntities;
                     if (!(Boolean)this.useTotalCount.get()) {
                        this.entityCounts.put(entity.method_5864(), this.entityCounts.getOrDefault(entity.method_5864(), 0) + 1);
                     }
                  }
               }

               if ((Boolean)this.useTotalCount.get() && totalEntities >= (Integer)this.combinedEntityThreshold.get()) {
                  this.disconnect("Total number of selected entities within range exceeded the limit.");
                  if ((Boolean)this.toggleOff.get()) {
                     this.toggle();
                  }
               } else if (!(Boolean)this.useTotalCount.get()) {
                  ObjectIterator var14 = this.entityCounts.object2IntEntrySet().iterator();

                  while(var14.hasNext()) {
                     Entry<class_1299<?>> entry = (Entry)var14.next();
                     if (entry.getIntValue() >= (Integer)this.individualEntityThreshold.get()) {
                        this.disconnect("Number of " + ((class_1299)entry.getKey()).method_5897().getString() + " within range exceeded the limit.");
                        if ((Boolean)this.toggleOff.get()) {
                           this.toggle();
                        }

                        return;
                     }
                  }
               }
            }

         }
      }
   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2663) {
         class_2663 p = (class_2663)var3;
         if (p.method_11470() == 35) {
            class_1297 entity = p.method_11469(this.mc.field_1687);
            if (entity != null && entity.equals(this.mc.field_1724)) {
               this.chainPops.add(System.currentTimeMillis());
            }
         }
      }
   }

   private void disconnect(String reason) {
      this.disconnect((class_2561)class_2561.method_43470(reason));
   }

   private void disconnect(class_2561 reason) {
      class_5250 text = class_2561.method_43470("[AutoLog] ");
      text.method_10852(reason);
      if ((Boolean)this.toggleAutoRecconect.get()) {
         AutoReconnect autoReconnect = (AutoReconnect)Modules.get().get(AutoReconnect.class);
         if (autoReconnect.isActive()) {
            text.method_10852(class_2561.method_43470("\n\nINFO - AutoReconnect was disabled").method_54663(-8355712));
            autoReconnect.toggle();
         }
      }

      this.mc.field_1724.field_3944.method_52781(new class_2661(text));
   }

   private void enableHealthListener() {
      MeteorClient.EVENT_BUS.subscribe((Object)this.staticListener);
   }

   private void disableHealthListener() {
      MeteorClient.EVENT_BUS.unsubscribe((Object)this.staticListener);
   }

   private class StaticListener {
      @EventHandler
      private void healthListener(TickEvent.Post event) {
         if (AutoLog.this.isActive()) {
            AutoLog.this.disableHealthListener();
         } else if (Utils.canUpdate() && !AutoLog.this.mc.field_1724.method_29504() && AutoLog.this.mc.field_1724.method_6032() > (float)(Integer)AutoLog.this.health.get()) {
            AutoLog.this.info("Player health greater than minimum, re-enabling module.", new Object[0]);
            AutoLog.this.toggle();
            AutoLog.this.disableHealthListener();
         }

      }
   }
}
