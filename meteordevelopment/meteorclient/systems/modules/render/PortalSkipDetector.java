package meteordevelopment.meteorclient.systems.modules.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_2902.class_2903;

public class PortalSkipDetector extends Module {
   private final ExecutorService chunkWorkerThread = Executors.newSingleThreadExecutor();
   private final Object2ObjectMap<class_1923, PortalSkipDetector.PortalSkipChunk> portalSkipChunks = new Object2ObjectOpenHashMap();
   private final Set<class_1923> confirmedFullyFlowedChunks = new HashSet();
   private final Set<class_1923> visitedChunks = new HashSet();
   private final int LAVA_FLOW_TICKS = 12;

   public PortalSkipDetector() {
      super(Categories.Render, "portal-skip-detector", "Detects player activity in a funny way.");
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      synchronized(this.portalSkipChunks) {
         ObjectIterator var3 = this.portalSkipChunks.values().iterator();

         while(var3.hasNext()) {
            PortalSkipDetector.PortalSkipChunk chunk = (PortalSkipDetector.PortalSkipChunk)var3.next();
            if (chunk.tallestExposedLavaBlockPillar >= 10) {
               if (chunk.chunkPos.method_24022(this.mc.field_1724.method_31476()) <= 7) {
                  ++chunk.ticksInFlowRange;
               }

               if (chunk.ticksInFlowRange > 12) {
                  this.confirmedFullyFlowedChunks.add(chunk.chunkPos);
               }
            }
         }

      }
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      class_2596 var4 = event.packet;
      if (var4 instanceof class_2637) {
         class_2637 packet = (class_2637)var4;
         packet.method_30621((pos, state) -> {
            if (!state.method_26227().method_15769() && !state.method_26227().method_15771()) {
               class_1923 chunkPos = new class_1923(pos);
               synchronized(this.portalSkipChunks) {
                  this.portalSkipChunks.remove(chunkPos);
                  this.confirmedFullyFlowedChunks.remove(chunkPos);
               }
            }

         });
      } else {
         var4 = event.packet;
         if (var4 instanceof class_2626) {
            class_2626 packet = (class_2626)var4;
            if (!packet.method_11308().method_26227().method_15769() && !packet.method_11308().method_26227().method_15771()) {
               class_1923 chunkPos = new class_1923(packet.method_11309());
               synchronized(this.portalSkipChunks) {
                  this.portalSkipChunks.remove(chunkPos);
                  this.confirmedFullyFlowedChunks.remove(chunkPos);
               }
            }
         }
      }

   }

   @EventHandler
   private void onChunkData(ChunkDataEvent event) {
      if (PlayerUtils.getDimension() == Dimension.Nether) {
         synchronized(this.portalSkipChunks) {
            this.portalSkipChunks.put(event.chunk().method_12004(), new PortalSkipDetector.PortalSkipChunk(this, event.chunk().method_12004()));
         }

         if (this.visitedChunks.add(event.chunk().method_12004())) {
            this.chunkWorkerThread.submit(() -> {
               if (this.isActive()) {
                  this.searchChunkForLavaPillar(event.chunk());
               }
            });
         }
      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      Color color = Color.GREEN.a(50);
      synchronized(this.portalSkipChunks) {
         Iterator var4 = this.confirmedFullyFlowedChunks.iterator();

         while(var4.hasNext()) {
            class_1923 chunkPos = (class_1923)var4.next();
            if (chunkPos.method_24022(this.mc.field_1724.method_31476()) <= 32) {
               class_243 start = new class_243((double)chunkPos.method_8323().method_10263(), (double)chunkPos.method_8323().method_10264(), (double)chunkPos.method_8323().method_10260());
               class_243 end = new class_243((double)(chunkPos.method_8323().method_10263() + 16), 0.0D, (double)(chunkPos.method_8323().method_10260() + 16));
               event.renderer.quadHorizontal(start.field_1352, start.field_1351, start.field_1350, end.field_1352, end.field_1350, color);
            }
         }

      }
   }

   private void searchChunkForLavaPillar(class_2818 chunk) {
      class_2339 blockPos = new class_2339();
      int tallestPillarHeight = 0;

      for(int x = chunk.method_12004().method_8326(); x <= chunk.method_12004().method_8327(); ++x) {
         for(int z = chunk.method_12004().method_8328(); z <= chunk.method_12004().method_8329(); ++z) {
            int height = chunk.method_12032(class_2903.field_13202).method_12603(x - chunk.method_12004().method_8326(), z - chunk.method_12004().method_8328());
            int pillarThisColumn = 0;

            for(int y = 32; y < height - 1; ++y) {
               blockPos.method_10103(x, y, z);
               class_2680 bs = chunk.method_8320(blockPos);
               if (bs.method_26204() == class_2246.field_10164) {
                  ++pillarThisColumn;
               }
            }

            if (pillarThisColumn > tallestPillarHeight) {
               tallestPillarHeight = pillarThisColumn;
            }
         }
      }

      synchronized(this.portalSkipChunks) {
         if (tallestPillarHeight < 10) {
            this.portalSkipChunks.remove(chunk.method_12004());
         } else {
            ((PortalSkipDetector.PortalSkipChunk)this.portalSkipChunks.get(chunk.method_12004())).tallestExposedLavaBlockPillar = tallestPillarHeight;
         }

      }
   }

   private class PortalSkipChunk {
      public final class_1923 chunkPos;
      public int ticksInFlowRange = 0;
      public int tallestExposedLavaBlockPillar = 0;

      public PortalSkipChunk(final PortalSkipDetector param1, class_1923 chunkPos) {
         this.chunkPos = chunkPos;
      }
   }
}
