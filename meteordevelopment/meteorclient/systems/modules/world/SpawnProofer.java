package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2231;
import net.minecraft.class_2241;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2269;
import net.minecraft.class_2312;
import net.minecraft.class_2338;
import net.minecraft.class_2401;
import net.minecraft.class_2482;
import net.minecraft.class_2538;
import net.minecraft.class_2577;
import net.minecraft.class_8923;
import net.minecraft.class_2338.class_2339;

public class SpawnProofer extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> range;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Integer> delay;
   private final Setting<Boolean> rotate;
   private final Setting<SpawnProofer.Mode> mode;
   private final Setting<Boolean> newMobSpawnLightLevel;
   private final Pool<class_2339> spawnPool;
   private final List<class_2339> spawns;
   private int ticksWaited;

   public SpawnProofer() {
      super(Categories.World, "spawn-proofer", "Automatically spawnproofs unlit areas.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.range = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("range")).description("Range for block placement and rendering")).defaultValue(3)).min(0).build());
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("Block to use for spawn proofing")).defaultValue(class_2246.field_10336, class_2246.field_10494, class_2246.field_10454).filter(this::filterBlocks).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("Delay in ticks between placing blocks")).defaultValue(0)).min(0).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates towards the blocks being placed.")).defaultValue(true)).build());
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Which spawn types should be spawn proofed.")).defaultValue(SpawnProofer.Mode.Both)).build());
      this.newMobSpawnLightLevel = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("new-mob-spawn-light-level")).description("Use the new (1.18+) mob spawn behavior")).defaultValue(true)).build());
      this.spawnPool = new Pool(class_2339::new);
      this.spawns = new ArrayList();
   }

   @EventHandler
   private void onTickPre(TickEvent.Pre event) {
      if ((Integer)this.delay.get() == 0 || this.ticksWaited >= (Integer)this.delay.get() - 1) {
         boolean foundBlock = InvUtils.testInHotbar((itemStack) -> {
            return ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()));
         });
         if (!foundBlock) {
            this.error("Found none of the chosen blocks in hotbar", new Object[0]);
            this.toggle();
         } else {
            Iterator var3 = this.spawns.iterator();

            while(var3.hasNext()) {
               class_2339 blockPos = (class_2339)var3.next();
               this.spawnPool.free(blockPos);
            }

            this.spawns.clear();
            int lightLevel = (Boolean)this.newMobSpawnLightLevel.get() ? 0 : 7;
            BlockIterator.register((Integer)this.range.get(), (Integer)this.range.get(), (blockPosx, blockState) -> {
               BlockUtils.MobSpawn spawn = BlockUtils.isValidMobSpawn(blockPosx, blockState, lightLevel);
               if (spawn == BlockUtils.MobSpawn.Always && (this.mode.get() == SpawnProofer.Mode.Always || this.mode.get() == SpawnProofer.Mode.Both) || spawn == BlockUtils.MobSpawn.Potential && (this.mode.get() == SpawnProofer.Mode.Potential || this.mode.get() == SpawnProofer.Mode.Both)) {
                  this.spawns.add(((class_2339)this.spawnPool.get()).method_10101(blockPosx));
               }

            });
         }
      }
   }

   @EventHandler
   private void onTickPost(TickEvent.Post event) {
      if ((Integer)this.delay.get() != 0 && this.ticksWaited < (Integer)this.delay.get() - 1) {
         ++this.ticksWaited;
      } else if (!this.spawns.isEmpty()) {
         FindItemResult block = InvUtils.findInHotbar((itemStack) -> {
            return ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()));
         });
         if (!block.found()) {
            this.error("Found none of the chosen blocks in hotbar", new Object[0]);
            this.toggle();
         } else {
            if ((Integer)this.delay.get() == 0) {
               Iterator var3 = this.spawns.iterator();

               while(var3.hasNext()) {
                  class_2338 blockPos = (class_2338)var3.next();
                  BlockUtils.place(blockPos, block, (Boolean)this.rotate.get(), -50, false);
               }
            } else if (this.isLightSource(class_2248.method_9503(this.mc.field_1724.method_31548().method_5438(block.slot()).method_7909()))) {
               int lowestLightLevel = 16;
               class_2339 selectedBlockPos = (class_2339)this.spawns.getFirst();
               Iterator var5 = this.spawns.iterator();

               while(var5.hasNext()) {
                  class_2338 blockPos = (class_2338)var5.next();
                  int lightLevel = this.mc.field_1687.method_22339(blockPos);
                  if (lightLevel < lowestLightLevel) {
                     lowestLightLevel = lightLevel;
                     selectedBlockPos.method_10101(blockPos);
                  }
               }

               BlockUtils.place(selectedBlockPos, block, (Boolean)this.rotate.get(), -50, false);
            } else {
               BlockUtils.place((class_2338)this.spawns.getFirst(), block, (Boolean)this.rotate.get(), -50, false);
            }

            this.ticksWaited = 0;
         }
      }
   }

   private boolean filterBlocks(class_2248 block) {
      return this.isNonOpaqueBlock(block) || this.isLightSource(block);
   }

   private boolean isNonOpaqueBlock(class_2248 block) {
      return block instanceof class_2269 || block instanceof class_2482 || block instanceof class_2231 || block instanceof class_8923 || block instanceof class_2538 || block instanceof class_2577 || block instanceof class_2401 || block instanceof class_2312 || block instanceof class_2241;
   }

   private boolean isLightSource(class_2248 block) {
      return block.method_9564().method_26213() > 0;
   }

   public static enum Mode {
      Always,
      Potential,
      Both,
      None;

      // $FF: synthetic method
      private static SpawnProofer.Mode[] $values() {
         return new SpawnProofer.Mode[]{Always, Potential, Both, None};
      }
   }
}
