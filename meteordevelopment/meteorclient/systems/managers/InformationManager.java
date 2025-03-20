package meteordevelopment.meteorclient.systems.managers;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.PlayerDeathEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.PlayerJoinLeaveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_2703;
import net.minecraft.class_640;
import net.minecraft.class_7828;
import net.minecraft.class_2703.class_2705;
import net.minecraft.class_2703.class_5893;

public class InformationManager {
   private final Object2IntMap<UUID> totemPopMap = new Object2IntOpenHashMap();
   private boolean isLoginPacket = true;

   public InformationManager() {
      MeteorClient.EVENT_BUS.subscribe((Object)this);
   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      if (MeteorClient.mc.field_1687 != null && MeteorClient.mc.field_1724 != null) {
         class_2596 var10000 = event.packet;
         Objects.requireNonNull(var10000);
         class_2596 var2 = var10000;
         byte var3 = 0;

         while(true) {
            switch(var2.typeSwitch<invokedynamic>(var2, var3)) {
            case 0:
               class_2663 packet = (class_2663)var2;
               if (packet.method_11470() == 35) {
                  class_1297 var12 = packet.method_11469(MeteorClient.mc.field_1687);
                  if (var12 instanceof class_1657) {
                     class_1657 entity = (class_1657)var12;
                     boolean var13 = false;
                     int pops;
                     synchronized(this.totemPopMap) {
                        pops = this.totemPopMap.getOrDefault(entity.method_5667(), 0);
                        Object2IntMap var21 = this.totemPopMap;
                        UUID var10001 = entity.method_5667();
                        ++pops;
                        var21.put(var10001, pops);
                     }

                     MeteorClient.EVENT_BUS.post((Object)PlayerDeathEvent.TotemPop.get(entity, pops));
                     return;
                  }
               }

               var3 = 1;
               break;
            case 1:
               class_2703 packet = (class_2703)var2;
               if (this.isLoginPacket) {
                  this.isLoginPacket = false;
                  return;
               }

               if (packet.method_46327().contains(class_5893.field_29136)) {
                  Iterator var15 = packet.method_46330().iterator();

                  while(var15.hasNext()) {
                     class_2705 entry = (class_2705)var15.next();
                     MeteorClient.EVENT_BUS.post((Object)PlayerJoinLeaveEvent.Join.get(entry));
                  }
               }

               return;
            case 2:
               class_7828 packet = (class_7828)var2;
               if (MeteorClient.mc.method_1562() == null) {
                  return;
               }

               Iterator var16 = packet.comp_1105().iterator();

               while(var16.hasNext()) {
                  UUID uuid = (UUID)var16.next();
                  class_640 toRemove = MeteorClient.mc.method_1562().method_2871(uuid);
                  if (toRemove != null) {
                     MeteorClient.EVENT_BUS.post((Object)PlayerJoinLeaveEvent.Leave.get(toRemove));
                  }
               }

               return;
            case 3:
               class_2663 packet = (class_2663)var2;
               if (packet.method_11470() == 3) {
                  class_1297 var10 = packet.method_11469(MeteorClient.mc.field_1687);
                  if (var10 instanceof class_1657) {
                     class_1657 entity = (class_1657)var10;
                     int pops = 0;
                     if (this.totemPopMap.containsKey(entity.method_5667())) {
                        pops = this.totemPopMap.removeInt(entity.method_5667());
                     }

                     MeteorClient.EVENT_BUS.post((Object)PlayerDeathEvent.Death.get(entity, pops));
                     return;
                  }
               }

               var3 = 4;
               break;
            default:
               return;
            }
         }
      }
   }

   @EventHandler
   private void onGameLeave(GameLeftEvent event) {
      this.isLoginPacket = true;
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (MeteorClient.mc.field_1687 == null || MeteorClient.mc.field_1724 == null) {
         ;
      }
   }

   public int getPops(class_1297 entity) {
      return this.totemPopMap.getOrDefault(entity.method_5667(), 0);
   }

   public int getPops(UUID uuid) {
      return this.totemPopMap.getOrDefault(uuid, 0);
   }
}
