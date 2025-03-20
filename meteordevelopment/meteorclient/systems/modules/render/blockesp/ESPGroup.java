package meteordevelopment.meteorclient.systems.modules.render.blockesp;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.UnorderedArrayList;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import net.minecraft.class_2248;

public class ESPGroup {
   private static final BlockESP blockEsp = (BlockESP)Modules.get().get(BlockESP.class);
   private final class_2248 block;
   public final UnorderedArrayList<ESPBlock> blocks = new UnorderedArrayList();
   private double sumX;
   private double sumY;
   private double sumZ;

   public ESPGroup(class_2248 block) {
      this.block = block;
   }

   public void add(ESPBlock block, boolean removeFromOld, boolean splitGroup) {
      this.blocks.add(block);
      this.sumX += (double)block.x;
      this.sumY += (double)block.y;
      this.sumZ += (double)block.z;
      if (block.group != null && removeFromOld) {
         block.group.remove(block, splitGroup);
      }

      block.group = this;
   }

   public void add(ESPBlock block) {
      this.add(block, true, true);
   }

   public void remove(ESPBlock block, boolean splitGroup) {
      this.blocks.remove(block);
      this.sumX -= (double)block.x;
      this.sumY -= (double)block.y;
      this.sumZ -= (double)block.z;
      if (this.blocks.isEmpty()) {
         blockEsp.removeGroup(block.group);
      } else if (splitGroup) {
         this.trySplit(block);
      }

   }

   public void remove(ESPBlock block) {
      this.remove(block, true);
   }

   private void trySplit(ESPBlock block) {
      Set<ESPBlock> neighbours = new ObjectOpenHashSet(6);
      int[] var3 = ESPBlock.SIDES;
      int var4 = var3.length;

      ESPBlock b;
      for(int var5 = 0; var5 < var4; ++var5) {
         int side = var3[var5];
         if ((block.neighbours & side) == side) {
            b = block.getSideBlock(side);
            if (b != null) {
               neighbours.add(b);
            }
         }
      }

      if (neighbours.size() > 1) {
         Set<ESPBlock> remainingBlocks = new ObjectOpenHashSet(this.blocks);
         Queue<ESPBlock> blocksToCheck = new ArrayDeque();
         blocksToCheck.offer((ESPBlock)this.blocks.getFirst());
         remainingBlocks.remove(this.blocks.getFirst());
         neighbours.remove(this.blocks.getFirst());

         int x;
         int y;
         label86:
         while(!blocksToCheck.isEmpty()) {
            ESPBlock b = (ESPBlock)blocksToCheck.poll();
            int[] var15 = ESPBlock.SIDES;
            int var17 = var15.length;

            for(x = 0; x < var17; ++x) {
               y = var15[x];
               if ((b.neighbours & y) == y) {
                  ESPBlock neighbour = b.getSideBlock(y);
                  if (neighbour != null && remainingBlocks.contains(neighbour)) {
                     blocksToCheck.offer(neighbour);
                     remainingBlocks.remove(neighbour);
                     neighbours.remove(neighbour);
                     if (neighbours.isEmpty()) {
                        break label86;
                     }
                  }
               }
            }
         }

         if (!neighbours.isEmpty()) {
            ESPGroup group = blockEsp.newGroup(this.block);
            group.blocks.ensureCapacity(remainingBlocks.size());
            UnorderedArrayList var10000 = this.blocks;
            Objects.requireNonNull(remainingBlocks);
            var10000.removeIf(remainingBlocks::contains);

            Iterator var16;
            for(var16 = remainingBlocks.iterator(); var16.hasNext(); this.sumZ -= (double)b.z) {
               b = (ESPBlock)var16.next();
               group.add(b, false, false);
               this.sumX -= (double)b.x;
               this.sumY -= (double)b.y;
            }

            if (neighbours.size() > 1) {
               block.neighbours = 0;
               var16 = neighbours.iterator();

               while(var16.hasNext()) {
                  b = (ESPBlock)var16.next();
                  x = b.x - block.x;
                  if (x == 1) {
                     block.neighbours |= 8;
                  } else if (x == -1) {
                     block.neighbours |= 128;
                  }

                  y = b.y - block.y;
                  if (y == 1) {
                     block.neighbours |= 512;
                  } else if (y == -1) {
                     block.neighbours |= 16384;
                  }

                  int z = b.z - block.z;
                  if (z == 1) {
                     block.neighbours |= 2;
                  } else if (z == -1) {
                     block.neighbours |= 32;
                  }
               }

               group.trySplit(block);
            }
         }

      }
   }

   public void merge(ESPGroup group) {
      this.blocks.ensureCapacity(this.blocks.size() + group.blocks.size());
      Iterator var2 = group.blocks.iterator();

      while(var2.hasNext()) {
         ESPBlock block = (ESPBlock)var2.next();
         this.add(block, false, false);
      }

      blockEsp.removeGroup(group);
   }

   public void render(Render3DEvent event) {
      ESPBlockData blockData = blockEsp.getBlockData(this.block);
      if (blockData.tracer) {
         event.renderer.line(RenderUtils.center.field_1352, RenderUtils.center.field_1351, RenderUtils.center.field_1350, this.sumX / (double)this.blocks.size() + 0.5D, this.sumY / (double)this.blocks.size() + 0.5D, this.sumZ / (double)this.blocks.size() + 0.5D, blockData.tracerColor);
      }

   }
}
