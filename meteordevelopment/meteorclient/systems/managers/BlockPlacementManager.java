package meteordevelopment.meteorclient.systems.managers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.config.AntiCheatConfig;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2626;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_2885;
import net.minecraft.class_3726;
import net.minecraft.class_3965;
import net.minecraft.class_2846.class_2847;

public class BlockPlacementManager {
   private final AntiCheatConfig antiCheatConfig = AntiCheatConfig.get();
   private final Map<class_2338, Long> placeCooldowns = new ConcurrentHashMap();
   private boolean locked = false;
   private boolean didSwap = false;
   private int packetsSent;
   private long lastSentPacketTimestamp = -1L;

   public BlockPlacementManager() {
      MeteorClient.EVENT_BUS.subscribe((Object)this);
   }

   public boolean beginPlacement(class_2338 position, class_2680 state, class_1792 item) {
      if (!this.checkLimit(System.currentTimeMillis(), false)) {
         return false;
      } else if (this.locked) {
         return false;
      } else if (!this.checkPlacement(item, position, state)) {
         return false;
      } else {
         if (MeteorClient.mc.field_1724.method_6047().method_7909() != item) {
            if (!MeteorClient.SWAP.beginSwap(item, true)) {
               return false;
            }

            this.didSwap = true;
         }

         this.locked = true;
         return true;
      }
   }

   public boolean beginPlacement(List<class_2338> positions, class_1792 item) {
      if (!this.checkLimit(System.currentTimeMillis(), false)) {
         return false;
      } else if (this.locked) {
         return false;
      } else if (positions.stream().filter((x) -> {
         return this.checkPlacement(item, x);
      }).findAny().isEmpty()) {
         return false;
      } else {
         if (MeteorClient.mc.field_1724.method_6047().method_7909() != item) {
            if (!MeteorClient.SWAP.beginSwap(item, true)) {
               return false;
            }

            this.didSwap = true;
         }

         this.locked = true;
         return true;
      }
   }

   public boolean placeBlock(class_1792 item, class_2338 blockPos) {
      return this.placeBlock(item, blockPos, MeteorClient.mc.field_1687.method_8320(blockPos));
   }

   public boolean placeBlock(class_1792 item, class_2338 blockPos, class_2680 state) {
      long currentTime = System.currentTimeMillis();
      if ((double)this.placeCooldowns.values().stream().filter((x) -> {
         return currentTime - x <= 1000L;
      }).count() >= (Double)this.antiCheatConfig.blocksPerSecondCap.get()) {
         return false;
      } else if (!this.checkPlacement(item, blockPos, state)) {
         return false;
      } else {
         class_2350 dir = BlockUtils.getPlaceSide(blockPos);
         class_243 hitPos = blockPos.method_46558();
         class_2338 neighbour;
         if (dir == null) {
            neighbour = blockPos;
         } else {
            neighbour = blockPos.method_10093(dir);
            hitPos = hitPos.method_1031((double)dir.method_10148() * 0.5D, (double)dir.method_10164() * 0.5D, (double)dir.method_10165() * 0.5D);
            if ((Boolean)this.antiCheatConfig.blockRotatePlace.get()) {
               MeteorClient.ROTATION.snapAt(hitPos);
            }
         }

         Long lastPlaceTime = (Long)this.placeCooldowns.get(blockPos);
         if (lastPlaceTime != null && (double)(currentTime - lastPlaceTime) < (Double)this.antiCheatConfig.blockPlacePerBlockCooldown.get() * 1000.0D) {
            return false;
         } else if (!this.checkLimit(currentTime, true)) {
            return false;
         } else {
            this.placeCooldowns.put(blockPos, currentTime);
            boolean grimAirPlaceSwap = (Boolean)this.antiCheatConfig.blockPlaceAirPlace.get() && (dir == null || (Boolean)this.antiCheatConfig.forceAirPlace.get());
            class_1268 placeHand = class_1268.field_5808;
            if (grimAirPlaceSwap) {
               MeteorClient.mc.method_1562().method_52787(new class_2846(class_2847.field_12969, class_2338.field_10980, class_2350.field_11033));
               placeHand = class_1268.field_5810;
            }

            MeteorClient.mc.method_1562().method_52787(new class_2885(placeHand, new class_3965(hitPos, dir == null ? class_2350.field_11033 : dir.method_10153(), neighbour, false), MeteorClient.mc.field_1687.method_41925().method_41937().method_41942()));
            if (grimAirPlaceSwap) {
               MeteorClient.mc.method_1562().method_52787(new class_2846(class_2847.field_12969, class_2338.field_10980, class_2350.field_11033));
            }

            return true;
         }
      }
   }

   public boolean checkPlacement(class_1792 item, class_2338 blockPos) {
      return this.checkPlacement(item, blockPos, MeteorClient.mc.field_1687.method_8320(blockPos));
   }

   public boolean checkPlacement(class_1792 item, class_2338 blockPos, class_2680 state) {
      if (!(Boolean)this.antiCheatConfig.blockPlaceAirPlace.get() && getPlaceOnDirection(blockPos) == null) {
         return false;
      } else if (!state.method_45474()) {
         return false;
      } else if (!class_1937.method_25953(blockPos)) {
         return false;
      } else {
         return MeteorClient.mc.field_1687.method_8628(class_2248.method_9503(item).method_9564(), blockPos, class_3726.method_16194()) || item == class_1802.field_16482;
      }
   }

   public void endPlacement() {
      if (this.locked) {
         this.locked = false;
         if (this.didSwap) {
            MeteorClient.SWAP.endSwap(true);
         }

      }
   }

   public void forceResetPlaceCooldown(class_2338 blockPos) {
      this.placeCooldowns.remove(blockPos);
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2626) {
         class_2626 packet = (class_2626)var3;
         if (!packet.method_11308().method_26215()) {
            this.placeCooldowns.remove(packet.method_11309());
         }
      }

   }

   public static class_2350 getPlaceOnDirection(class_2338 pos) {
      if (pos == null) {
         return null;
      } else {
         class_2350 best = null;
         if (MeteorClient.mc.field_1687 != null && MeteorClient.mc.field_1724 != null) {
            double cDist = -1.0D;
            class_2350[] var4 = class_2350.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               class_2350 dir = var4[var6];
               if (!MeteorClient.mc.field_1687.method_8320(pos.method_10093(dir)).method_26215()) {
                  double dist = getDistanceForDir(pos, dir);
                  if (dist >= 0.0D && (cDist < 0.0D || dist < cDist)) {
                     best = dir;
                     cDist = dist;
                  }
               }
            }
         }

         return best;
      }
   }

   private static double getDistanceForDir(class_2338 pos, class_2350 dir) {
      if (MeteorClient.mc.field_1724 == null) {
         return 0.0D;
      } else {
         class_243 vec = new class_243((double)((float)pos.method_10263() + (float)dir.method_10148() / 2.0F), (double)((float)pos.method_10264() + (float)dir.method_10164() / 2.0F), (double)((float)pos.method_10260() + (float)dir.method_10165() / 2.0F));
         class_243 dist = MeteorClient.mc.field_1724.method_33571().method_1031(-vec.field_1352, -vec.field_1351, -vec.field_1350);
         return dist.method_1027();
      }
   }

   private boolean checkLimit(long timestamp, boolean incrementLimit) {
      if (this.lastSentPacketTimestamp != -1L && timestamp - this.lastSentPacketTimestamp < (long)(Integer)this.antiCheatConfig.blockPacketLimit.get() && this.packetsSent >= 8) {
         return false;
      } else {
         if (incrementLimit) {
            ++this.packetsSent;
         }

         if (this.lastSentPacketTimestamp != -1L && timestamp - this.lastSentPacketTimestamp < (long)(Integer)this.antiCheatConfig.blockPacketLimit.get()) {
            return true;
         } else {
            this.lastSentPacketTimestamp = timestamp;
            this.packetsSent = 0;
            return true;
         }
      }
   }
}
