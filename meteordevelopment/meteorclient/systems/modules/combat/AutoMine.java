package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import meteordevelopment.meteorclient.events.meteor.SilentMineFinishedEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.autocrystal.AutoCrystal;
import meteordevelopment.meteorclient.systems.modules.player.SilentMine;
import meteordevelopment.meteorclient.systems.modules.render.BreakIndicators;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2350.class_2353;
import org.joml.Vector3d;

public class AutoMine extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final double INVALID_SCORE;
   private final Setting<Double> range;
   private final Setting<SortPriority> targetPriority;
   private final Setting<Boolean> ignoreNakeds;
   private final Setting<AutoMine.AntiSwimMode> antiSwim;
   private final Setting<AutoMine.AntiSurroundMode> antiSurroundMode;
   private final Setting<Boolean> antiSurroundInnerSnap;
   private final Setting<Boolean> antiSurroundOuterSnap;
   private final Setting<Double> antiSurroundOuterCooldown;
   private final Setting<Boolean> renderDebugScores;
   private SilentMine silentMine;
   private class_1657 targetPlayer;
   private AutoMine.CityBlock target1;
   private AutoMine.CityBlock target2;
   private class_2338 ignorePos;
   private long lastOuterPlaceTime;

   public AutoMine() {
      super(Categories.Combat, "auto-mine", "Automatically mines blocks. Requires SilentMine to work.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.INVALID_SCORE = -1000.0D;
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("Max range to target")).defaultValue(6.5D).min(0.0D).sliderMax(7.0D).build());
      this.targetPriority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to choose the target")).defaultValue(SortPriority.ClosestAngle)).build());
      this.ignoreNakeds = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-nakeds")).description("Ignore players with no items.")).defaultValue(true)).build());
      this.antiSwim = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("anti-swim-mode")).description("Starts mining your head block when the enemy starts mining your feet")).defaultValue(AutoMine.AntiSwimMode.OnMine)).build());
      this.antiSurroundMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("anti-surround-mode")).description("Places crystals in places to prevent surround")).defaultValue(AutoMine.AntiSurroundMode.Auto)).build());
      this.antiSurroundInnerSnap = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-surround-inner-snap")).description("Instantly snaps the camera when it needs to for inner place")).defaultValue(true)).visible(() -> {
         return this.antiSurroundMode.get() == AutoMine.AntiSurroundMode.Auto || this.antiSurroundMode.get() == AutoMine.AntiSurroundMode.Inner;
      })).build());
      this.antiSurroundOuterSnap = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-surround-outer-snap")).description("Instantly snaps the camera when it needs to for outer place")).defaultValue(true)).visible(() -> {
         return this.antiSurroundMode.get() == AutoMine.AntiSurroundMode.Auto || this.antiSurroundMode.get() == AutoMine.AntiSurroundMode.Outer;
      })).build());
      this.antiSurroundOuterCooldown = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("anti-surround-outer-cooldown")).description("Time to wait between placing crystals")).defaultValue(0.1D).min(0.0D).sliderMax(1.0D).visible(() -> {
         return this.antiSurroundMode.get() == AutoMine.AntiSurroundMode.Auto || this.antiSurroundMode.get() == AutoMine.AntiSurroundMode.Outer;
      })).build());
      this.renderDebugScores = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-debug-scores")).description("Renders scores and their blocks.")).defaultValue(false)).build());
      this.silentMine = null;
      this.targetPlayer = null;
      this.target1 = null;
      this.target2 = null;
      this.ignorePos = null;
      this.lastOuterPlaceTime = 0L;
      this.silentMine = (SilentMine)Modules.get().get(SilentMine.class);
   }

   public void onActivate() {
      super.onActivate();
      if (this.silentMine == null) {
         this.silentMine = (SilentMine)Modules.get().get(SilentMine.class);
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.silentMine == null) {
         this.silentMine = (SilentMine)Modules.get().get(SilentMine.class);
      }

   }

   @EventHandler
   private void onSilentMineFinished(SilentMineFinishedEvent.Pre event) {
      if (this.targetPlayer != null) {
         AutoMine.AntiSurroundMode mode = (AutoMine.AntiSurroundMode)this.antiSurroundMode.get();
         if (mode != AutoMine.AntiSurroundMode.None) {
            class_2350[] var3;
            int var4;
            int var5;
            class_2350 dir;
            class_2338 playerSurroundBlock;
            if (mode == AutoMine.AntiSurroundMode.Auto || mode == AutoMine.AntiSurroundMode.Outer) {
               var3 = class_2350.field_11041;
               var4 = var3.length;

               label79:
               for(var5 = 0; var5 < var4; ++var5) {
                  dir = var3[var5];
                  playerSurroundBlock = this.targetPlayer.method_24515().method_10093(dir);
                  if (event.getBlockPos().equals(playerSurroundBlock)) {
                     class_238 checkBox = class_238.method_30048(playerSurroundBlock.method_46558(), 2.5D, 3.0D, 2.5D);
                     class_238 blockHitbox = new class_238(playerSurroundBlock);
                     boolean outerSpeedCheck = (double)(System.currentTimeMillis() - this.lastOuterPlaceTime) > (Double)this.antiSurroundOuterCooldown.get() * 1000.0D;
                     if (!outerSpeedCheck) {
                        return;
                     }

                     Iterator var11 = BlockUtils.iterate(checkBox).iterator();

                     while(true) {
                        class_2338 blockPos;
                        class_2680 downState;
                        do {
                           do {
                              if (!var11.hasNext()) {
                                 continue label79;
                              }

                              blockPos = (class_2338)var11.next();
                           } while(!this.mc.field_1687.method_22347(blockPos));

                           downState = this.mc.field_1687.method_8320(blockPos.method_10074());
                        } while(!downState.method_27852(class_2246.field_10540) && !downState.method_27852(class_2246.field_9987));

                        class_238 crystalPlaceHitbox = new class_238((double)blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260(), (double)(blockPos.method_10263() + 1), (double)(blockPos.method_10264() + 2), (double)(blockPos.method_10260() + 1));
                        if (!EntityUtils.intersectsWithEntity(crystalPlaceHitbox, (entity) -> {
                           return !entity.method_7325();
                        })) {
                           class_243 crystalPos = new class_243((double)blockPos.method_10263() + 0.5D, (double)blockPos.method_10264(), (double)blockPos.method_10260() + 0.5D);
                           class_238 crystalHitbox = new class_238(crystalPos.field_1352 - 1.0D, crystalPos.field_1351, crystalPos.field_1350 - 1.0D, crystalPos.field_1352 + 1.0D, crystalPos.field_1351 + 2.0D, crystalPos.field_1350 + 1.0D);
                           if (crystalHitbox.method_994(blockHitbox)) {
                              ((AutoCrystal)Modules.get().get(AutoCrystal.class)).preplaceCrystal(blockPos, (Boolean)this.antiSurroundOuterSnap.get());
                              return;
                           }
                        }
                     }
                  }
               }
            }

            if (mode == AutoMine.AntiSurroundMode.Auto || mode == AutoMine.AntiSurroundMode.Inner) {
               var3 = class_2350.field_11041;
               var4 = var3.length;

               for(var5 = 0; var5 < var4; ++var5) {
                  dir = var3[var5];
                  playerSurroundBlock = this.targetPlayer.method_24515().method_10093(dir);
                  if (playerSurroundBlock.equals(event.getBlockPos())) {
                     ((AutoCrystal)Modules.get().get(AutoCrystal.class)).preplaceCrystal(playerSurroundBlock, (Boolean)this.antiSurroundInnerSnap.get());
                  }
               }
            }

         }
      }
   }

   private void update() {
      if (this.silentMine == null) {
         this.silentMine = (SilentMine)Modules.get().get(SilentMine.class);
      }

      class_2680 selfFeetBlock = this.mc.field_1687.method_8320(this.mc.field_1724.method_24515());
      class_2680 selfHeadBlock = this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10084());
      boolean shouldBreakSelfHeadBlock = BlockUtils.canBreak(this.mc.field_1724.method_24515().method_10084(), selfHeadBlock) && (selfHeadBlock.method_27852(class_2246.field_10540) || selfHeadBlock.method_27852(class_2246.field_22423));
      boolean prioHead = false;
      if (this.antiSwim.get() == AutoMine.AntiSwimMode.Always && shouldBreakSelfHeadBlock) {
         this.silentMine.silentBreakBlock(this.mc.field_1724.method_24515().method_10084(), 10.0D);
         prioHead = true;
      }

      if (this.antiSwim.get() == AutoMine.AntiSwimMode.OnMineAndSwim && this.mc.field_1724.method_20448() && shouldBreakSelfHeadBlock) {
         this.silentMine.silentBreakBlock(this.mc.field_1724.method_24515().method_10084(), 30.0D);
         prioHead = true;
      }

      if (this.antiSwim.get() == AutoMine.AntiSwimMode.OnMine || this.antiSwim.get() == AutoMine.AntiSwimMode.OnMineAndSwim) {
         BreakIndicators breakIndicators = (BreakIndicators)Modules.get().get(BreakIndicators.class);
         if (breakIndicators.isBlockBeingBroken(this.mc.field_1724.method_24515()) && shouldBreakSelfHeadBlock) {
            this.silentMine.silentBreakBlock(this.mc.field_1724.method_24515().method_10084(), 20.0D);
            prioHead = true;
         }
      }

      this.targetPlayer = (class_1657)TargetUtils.get((entity) -> {
         if (!entity.equals(this.mc.field_1724) && !entity.equals(this.mc.field_1719)) {
            if (entity instanceof class_1657) {
               class_1657 player = (class_1657)entity;
               if (player.method_5805() && !player.method_29504()) {
                  if (player.method_7337()) {
                     return false;
                  } else if (!Friends.get().shouldAttack(player)) {
                     return false;
                  } else if (entity.method_19538().method_1022(this.mc.field_1724.method_33571()) > (Double)this.range.get()) {
                     return false;
                  } else {
                     return !(Boolean)this.ignoreNakeds.get() || !((class_1799)player.method_31548().field_7548.get(0)).method_7960() || !((class_1799)player.method_31548().field_7548.get(1)).method_7960() || !((class_1799)player.method_31548().field_7548.get(2)).method_7960() || !((class_1799)player.method_31548().field_7548.get(3)).method_7960();
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }, (SortPriority)this.targetPriority.get());
      if (this.targetPlayer != null) {
         if (!this.silentMine.hasDelayedDestroy() || !selfHeadBlock.method_26204().equals(class_2246.field_10540) || !selfFeetBlock.method_26215() || this.silentMine.getRebreakBlockPos() != this.mc.field_1724.method_24515().method_10084()) {
            if (!prioHead) {
               this.findTargetBlocks();
               boolean isTargetingFeetBlock = this.target1 != null && this.target1.isFeetBlock || this.target2 != null && this.target2.isFeetBlock;
               if (isTargetingFeetBlock || (this.target1 == null || !this.target1.blockPos.equals(this.silentMine.getRebreakBlockPos())) && (this.target2 == null || !this.target2.blockPos.equals(this.silentMine.getRebreakBlockPos()))) {
                  boolean hasBothInProgress = this.silentMine.hasDelayedDestroy() && this.silentMine.hasRebreakBlock() && !this.silentMine.canRebreakRebreakBlock();
                  if (!hasBothInProgress) {
                     Queue<class_2338> targetBlocks = new LinkedList();
                     if (this.target1 != null) {
                        targetBlocks.add(this.target1.blockPos);
                     }

                     if (this.target2 != null) {
                        targetBlocks.add(this.target2.blockPos);
                     }

                     if (!targetBlocks.isEmpty() && this.silentMine.hasDelayedDestroy()) {
                        this.silentMine.silentBreakBlock((class_2338)targetBlocks.remove(), 10.0D);
                     }

                     if (!targetBlocks.isEmpty() && (!this.silentMine.hasRebreakBlock() || this.silentMine.canRebreakRebreakBlock())) {
                        this.silentMine.silentBreakBlock((class_2338)targetBlocks.remove(), 10.0D);
                     }

                  }
               }
            }
         }
      }
   }

   private void findTargetBlocks() {
      this.target1 = this.findCityBlock((class_2338)null);
      this.ignorePos = this.target1 != null ? this.target1.blockPos : null;
      this.target2 = this.findCityBlock(this.target1 != null ? this.target1.blockPos : null);
   }

   private AutoMine.CityBlock findCityBlock(class_2338 exclude) {
      if (this.targetPlayer == null) {
         return null;
      } else {
         boolean set = false;
         AutoMine.CityBlock bestBlock = new AutoMine.CityBlock(this);
         Set<AutoMine.CheckPos> checkPos = new HashSet();
         class_238 boundingBox = this.targetPlayer.method_5829().method_1002(0.01D, 0.1D, 0.01D);
         double feetY = this.targetPlayer.method_23318();
         class_238 feetBox = new class_238(boundingBox.field_1323, feetY, boundingBox.field_1321, boundingBox.field_1320, feetY + 0.1D, boundingBox.field_1324);
         boolean inBedrock = class_2338.method_29715(feetBox).anyMatch((blockPosx) -> {
            return this.mc.field_1687.method_8320(blockPosx).method_26204() == class_2246.field_9987;
         });
         if (inBedrock) {
            this.addBedrockCaseCheckPositions(checkPos);
         } else {
            this.addNormalCaseCheckPositions(checkPos);
         }

         Iterator var10 = checkPos.iterator();

         while(true) {
            AutoMine.CheckPos pos;
            class_2338 blockPos;
            class_2680 block;
            boolean isPosGoodRebreak;
            boolean isFeetBlock;
            do {
               do {
                  do {
                     if (!var10.hasNext()) {
                        if (set) {
                           return bestBlock;
                        }

                        return null;
                     }

                     pos = (AutoMine.CheckPos)var10.next();
                     blockPos = pos.blockPos;
                  } while(blockPos.equals(exclude));

                  block = this.mc.field_1687.method_8320(blockPos);
                  isPosGoodRebreak = false;
                  if (this.silentMine.canRebreakRebreakBlock() && blockPos.equals(this.silentMine.getRebreakBlockPos())) {
                     if (inBedrock) {
                        isFeetBlock = false;
                        class_2350[] var16 = class_2350.field_11041;
                        int var17 = var16.length;

                        for(int var18 = 0; var18 < var17; ++var18) {
                           class_2350 dir = var16[var18];
                           if (this.targetPlayer.method_24515().method_10084().method_10093(dir).equals(blockPos)) {
                              isFeetBlock = true;
                              break;
                           }
                        }

                        boolean canFacePlace = this.mc.field_1687.method_8320(this.targetPlayer.method_24515().method_10084()).method_26215();
                        isPosGoodRebreak = class_2338.method_29715(feetBox).count() == 1L && (blockPos.equals(this.targetPlayer.method_24515().method_10086(2)) || isFeetBlock && canFacePlace);
                     } else {
                        isPosGoodRebreak = !blockPos.equals(this.targetPlayer.method_24515()) && !this.isBlockInFeet(blockPos) && Arrays.stream(class_2350.field_11041).anyMatch((dirx) -> {
                           return this.targetPlayer.method_24515().method_10093(dirx).equals(blockPos) && this.isCrystalBlock(this.targetPlayer.method_24515().method_10093(dirx).method_10074());
                        });
                     }
                  }
               } while(block.method_26215() && !isPosGoodRebreak);

               isFeetBlock = this.isBlockInFeet(blockPos);
            } while(!BlockUtils.canBreak(blockPos, block) && !isPosGoodRebreak);

            if (this.silentMine.inBreakRange(blockPos)) {
               double score = 0.0D;
               if (inBedrock) {
                  score = this.scoreBedrockCityBlock(pos);
               } else {
                  score = this.scoreNormalCityBlock(pos);
               }

               if (score != -1000.0D) {
                  if (isPosGoodRebreak) {
                     score += 40.0D;
                  }

                  if (score > bestBlock.score) {
                     bestBlock.score = score;
                     bestBlock.blockPos = blockPos;
                     bestBlock.isFeetBlock = isFeetBlock;
                     set = true;
                  }
               }
            }
         }
      }
   }

   private void addNormalCaseCheckPositions(Set<AutoMine.CheckPos> checkPos) {
      class_238 boundingBox = this.targetPlayer.method_5829().method_1002(0.01D, 0.1D, 0.01D);
      double feetY = this.targetPlayer.method_23318();
      class_238 feetBox = new class_238(boundingBox.field_1323, feetY, boundingBox.field_1321, boundingBox.field_1320, feetY + 0.1D, boundingBox.field_1324);
      Iterator var6 = BlockUtils.iterate(feetBox).iterator();

      class_2338 pos;
      while(var6.hasNext()) {
         pos = (class_2338)var6.next();
         checkPos.add(new AutoMine.CheckPos(this, pos, AutoMine.CheckPosType.Feet));
      }

      var6 = BlockUtils.iterate(feetBox).iterator();

      while(var6.hasNext()) {
         pos = (class_2338)var6.next();
         Iterator var8 = class_2353.field_11062.iterator();

         while(var8.hasNext()) {
            class_2350 dir = (class_2350)var8.next();
            checkPos.add(new AutoMine.CheckPos(this, pos.method_10093(dir), AutoMine.CheckPosType.Surround));
         }
      }

      checkPos.add(new AutoMine.CheckPos(this, this.targetPlayer.method_24515(), AutoMine.CheckPosType.Feet));
      boolean inMultipleBlocks = class_2338.method_29715(feetBox).count() > 1L;
      if (!inMultipleBlocks) {
         Iterator var11 = class_2353.field_11062.iterator();

         while(var11.hasNext()) {
            class_2350 dir = (class_2350)var11.next();
            checkPos.add(new AutoMine.CheckPos(this, this.targetPlayer.method_24515().method_10079(dir, 2), AutoMine.CheckPosType.Extend));
         }
      }

   }

   private void addBedrockCaseCheckPositions(Set<AutoMine.CheckPos> checkPos) {
      class_238 boundingBox = this.targetPlayer.method_5829().method_1002(0.01D, 0.1D, 0.01D);
      double feetY = this.targetPlayer.method_23318();
      class_238 feetBox = new class_238(boundingBox.field_1323, feetY, boundingBox.field_1321, boundingBox.field_1320, feetY + 0.1D, boundingBox.field_1324);
      boolean canFallDown = class_2338.method_29715(feetBox).allMatch((blockPos) -> {
         return this.mc.field_1687.method_8320(blockPos.method_10074()).method_26204() != class_2246.field_9987;
      });
      boolean canBeHitUp = class_2338.method_29715(feetBox).allMatch((blockPos) -> {
         return this.mc.field_1687.method_8320(blockPos.method_10086(2)).method_26204() != class_2246.field_9987;
      });
      Iterator var8 = BlockUtils.iterate(feetBox).iterator();

      while(var8.hasNext()) {
         class_2338 pos = (class_2338)var8.next();
         if (canFallDown) {
            checkPos.add(new AutoMine.CheckPos(this, pos.method_10074(), AutoMine.CheckPosType.Below));
         }

         if (canBeHitUp) {
            checkPos.add(new AutoMine.CheckPos(this, pos.method_10086(2), AutoMine.CheckPosType.Head));
         }

         checkPos.add(new AutoMine.CheckPos(this, pos.method_10084(), AutoMine.CheckPosType.FacePlace));
         Iterator var10 = class_2353.field_11062.iterator();

         class_2350 dir;
         while(var10.hasNext()) {
            dir = (class_2350)var10.next();
            checkPos.add(new AutoMine.CheckPos(this, pos.method_10084().method_10093(dir), AutoMine.CheckPosType.FacePlace));
         }

         checkPos.add(new AutoMine.CheckPos(this, pos, AutoMine.CheckPosType.Surround));
         var10 = class_2353.field_11062.iterator();

         while(var10.hasNext()) {
            dir = (class_2350)var10.next();
            checkPos.add(new AutoMine.CheckPos(this, pos.method_10093(dir), AutoMine.CheckPosType.Surround));
         }
      }

   }

   private double scoreNormalCityBlock(AutoMine.CheckPos pos) {
      class_2338 blockPos = pos.blockPos;
      double score = 0.0D;
      class_2680 block = this.mc.field_1687.method_8320(blockPos);
      class_2680 selfHeadState;
      if (blockPos.equals(this.targetPlayer.method_24515())) {
         selfHeadState = this.mc.field_1687.method_8320(blockPos.method_10084());
         if (selfHeadState.method_26204().equals(class_2246.field_10540)) {
            score += 100.0D;
         } else {
            if (block.method_26204() == class_2246.field_10343) {
               return -1000.0D;
            }

            score += 50.0D;
         }
      } else {
         selfHeadState = this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10084());
         if (blockPos.equals(this.mc.field_1724.method_24515()) && (selfHeadState.method_26204().equals(class_2246.field_10540) || selfHeadState.method_26204().equals(class_2246.field_9987))) {
            return -1000.0D;
         }

         if (pos.type == AutoMine.CheckPosType.Surround) {
            score += 3.0D;
            boolean isPosAntiSurround = false;
            Iterator var8 = class_2353.field_11062.iterator();

            while(var8.hasNext()) {
               class_2350 dir = (class_2350)var8.next();
               if (this.targetPlayer.method_24515().method_10093(dir).equals(blockPos)) {
                  class_2338 antiSurroundBlockPos = this.targetPlayer.method_24515().method_10079(dir, 2);
                  if (this.getBlockStateIgnore(antiSurroundBlockPos).method_26215() && this.isCrystalBlock(antiSurroundBlockPos.method_10074())) {
                     isPosAntiSurround = true;
                     break;
                  }
               }
            }

            if (isPosAntiSurround) {
               score += 25.0D;
            }
         }

         if (pos.type == AutoMine.CheckPosType.Extend) {
            score += 20.0D;
         }
      }

      double d = this.targetPlayer.method_19538().method_1022(class_243.method_24953(blockPos));
      score += 10.0D / d;
      return score;
   }

   private double scoreBedrockCityBlock(AutoMine.CheckPos pos) {
      class_2338 blockPos = pos.blockPos;
      double score = 0.0D;
      if (blockPos.method_10264() == this.targetPlayer.method_31478() + 2 || blockPos.method_10264() == this.targetPlayer.method_31478() - 1) {
         score += 10.0D;
      }

      class_238 boundingBox = this.targetPlayer.method_5829().method_1002(0.01D, 0.1D, 0.01D);
      double feetY = this.targetPlayer.method_23318();
      class_238 feetBox = new class_238(boundingBox.field_1323, feetY, boundingBox.field_1321, boundingBox.field_1320, feetY + 0.1D, boundingBox.field_1324);
      if (class_2338.method_29715(feetBox).count() == 1L) {
         boolean canMineFaceBlock = this.mc.field_1687.method_8320(this.targetPlayer.method_24515().method_10084()).method_26204() != class_2246.field_9987;
         if (canMineFaceBlock) {
            if (blockPos.equals(this.targetPlayer.method_24515().method_10084())) {
               score += 20.0D;
            } else {
               boolean isSelfTrapBlock = false;
               class_2350[] var11 = class_2350.field_11041;
               int var12 = var11.length;

               for(int var13 = 0; var13 < var12; ++var13) {
                  class_2350 dir = var11[var13];
                  if (this.targetPlayer.method_24515().method_10084().method_10093(dir).equals(blockPos)) {
                     isSelfTrapBlock = true;
                     break;
                  }
               }

               if (isSelfTrapBlock) {
                  score += 7.5D;
               }
            }
         }
      }

      double d = this.targetPlayer.method_19538().method_1022(class_243.method_24953(blockPos));
      score += 10.0D / d;
      return score;
   }

   private boolean isBlockInFeet(class_2338 blockPos) {
      class_238 boundingBox = this.targetPlayer.method_5829().method_1002(0.01D, 0.1D, 0.01D);
      double feetY = this.targetPlayer.method_23318();
      class_238 feetBox = new class_238(boundingBox.field_1323, feetY, boundingBox.field_1321, boundingBox.field_1320, feetY + 0.1D, boundingBox.field_1324);
      Iterator var6 = class_2338.method_10094((int)Math.floor(feetBox.field_1323), (int)Math.floor(feetBox.field_1322), (int)Math.floor(feetBox.field_1321), (int)Math.floor(feetBox.field_1320), (int)Math.floor(feetBox.field_1325), (int)Math.floor(feetBox.field_1324)).iterator();

      class_2338 pos;
      do {
         if (!var6.hasNext()) {
            return false;
         }

         pos = (class_2338)var6.next();
      } while(!blockPos.equals(pos));

      return true;
   }

   private boolean isCrystalBlock(class_2338 blockPos) {
      class_2680 blockState = this.mc.field_1687.method_8320(blockPos);
      return blockState.method_27852(class_2246.field_10540) || blockState.method_27852(class_2246.field_9987);
   }

   public boolean isTargetedPos(class_2338 blockPos) {
      return this.target1 != null && this.target1.blockPos.equals(blockPos) || this.target2 != null && this.target2.blockPos.equals(blockPos);
   }

   private class_2680 getBlockStateIgnore(class_2338 blockPos) {
      if (blockPos == null) {
         return null;
      } else {
         return blockPos.equals(this.ignorePos) ? class_2246.field_10124.method_9564() : this.mc.field_1687.method_8320(blockPos);
      }
   }

   public boolean isTargetingAnything() {
      return this.target1 != null && this.target2 != null;
   }

   private void render3d(Render3DEvent event) {
      if (this.targetPlayer != null) {
         if ((Boolean)this.renderDebugScores.get()) {
            double bestScore = 0.0D;
            Set<AutoMine.CheckPos> checkPos = new HashSet();
            class_238 boundingBox = this.targetPlayer.method_5829().method_1002(0.01D, 0.1D, 0.01D);
            double feetY = this.targetPlayer.method_23318();
            class_238 feetBox = new class_238(boundingBox.field_1323, feetY, boundingBox.field_1321, boundingBox.field_1320, feetY + 0.1D, boundingBox.field_1324);
            boolean inBedrock = class_2338.method_29715(feetBox).anyMatch((blockPosx) -> {
               return this.mc.field_1687.method_8320(blockPosx).method_26204() == class_2246.field_9987;
            });
            if (inBedrock) {
               this.addBedrockCaseCheckPositions(checkPos);
            } else {
               this.addNormalCaseCheckPositions(checkPos);
            }

            Iterator var10 = checkPos.iterator();

            while(true) {
               AutoMine.CheckPos pos;
               class_2338 blockPos;
               class_2680 block;
               boolean isPosGoodRebreak;
               do {
                  do {
                     boolean isPosGoodRebreak;
                     int var18;
                     boolean isSelfTrapBlock;
                     if (!var10.hasNext()) {
                        Color color = Color.RED;
                        Iterator var22 = checkPos.iterator();

                        while(true) {
                           AutoMine.CheckPos pos;
                           class_2338 blockPos;
                           class_2680 block;
                           do {
                              do {
                                 if (!var22.hasNext()) {
                                    return;
                                 }

                                 pos = (AutoMine.CheckPos)var22.next();
                                 blockPos = pos.blockPos;
                                 block = this.mc.field_1687.method_8320(blockPos);
                                 isPosGoodRebreak = false;
                                 if (this.silentMine.canRebreakRebreakBlock() && blockPos.equals(this.silentMine.getRebreakBlockPos())) {
                                    if (inBedrock) {
                                       isSelfTrapBlock = false;
                                       class_2350[] var28 = class_2350.field_11041;
                                       var18 = var28.length;

                                       for(int var32 = 0; var32 < var18; ++var32) {
                                          class_2350 dir = var28[var32];
                                          if (this.targetPlayer.method_24515().method_10084().method_10093(dir).equals(blockPos)) {
                                             isSelfTrapBlock = true;
                                             break;
                                          }
                                       }

                                       boolean canFacePlace = this.mc.field_1687.method_8320(this.targetPlayer.method_24515().method_10084()).method_26215();
                                       isPosGoodRebreak = class_2338.method_29715(feetBox).count() == 1L && (blockPos.equals(this.targetPlayer.method_24515().method_10086(2)) || isSelfTrapBlock && canFacePlace);
                                    } else {
                                       isPosGoodRebreak = !blockPos.equals(this.targetPlayer.method_24515()) && !this.isBlockInFeet(blockPos) && Arrays.stream(class_2350.field_11041).anyMatch((dirx) -> {
                                          return this.targetPlayer.method_24515().method_10093(dirx).equals(blockPos) && this.isCrystalBlock(this.targetPlayer.method_24515().method_10093(dirx).method_10074());
                                       });
                                    }
                                 }
                              } while(block.method_26215() && !isPosGoodRebreak);
                           } while(!BlockUtils.canBreak(blockPos, block) && !isPosGoodRebreak);

                           if (this.silentMine.inBreakRange(blockPos)) {
                              double score = 0.0D;
                              if (inBedrock) {
                                 score = this.scoreBedrockCityBlock(pos);
                              } else {
                                 score = this.scoreNormalCityBlock(pos);
                              }

                              if (score != -1000.0D) {
                                 if (isPosGoodRebreak) {
                                    score += 40.0D;
                                 }

                                 double alpha = score / bestScore / 4.0D;
                                 event.renderer.box((class_2338)blockPos, color.a((int)(255.0D * alpha)), Color.WHITE, ShapeMode.Sides, 0);
                              }
                           }
                        }
                     }

                     pos = (AutoMine.CheckPos)var10.next();
                     blockPos = pos.blockPos;
                     block = this.mc.field_1687.method_8320(blockPos);
                     isPosGoodRebreak = false;
                     if (this.silentMine.canRebreakRebreakBlock() && blockPos.equals(this.silentMine.getRebreakBlockPos())) {
                        if (inBedrock) {
                           isPosGoodRebreak = false;
                           class_2350[] var16 = class_2350.field_11041;
                           int var17 = var16.length;

                           for(var18 = 0; var18 < var17; ++var18) {
                              class_2350 dir = var16[var18];
                              if (this.targetPlayer.method_24515().method_10084().method_10093(dir).equals(blockPos)) {
                                 isPosGoodRebreak = true;
                                 break;
                              }
                           }

                           isSelfTrapBlock = this.mc.field_1687.method_8320(this.targetPlayer.method_24515().method_10084()).method_26215();
                           isPosGoodRebreak = class_2338.method_29715(feetBox).count() == 1L && (blockPos.equals(this.targetPlayer.method_24515().method_10086(2)) || isPosGoodRebreak && isSelfTrapBlock);
                        } else {
                           isPosGoodRebreak = !blockPos.equals(this.targetPlayer.method_24515()) && !this.isBlockInFeet(blockPos) && Arrays.stream(class_2350.field_11041).anyMatch((dirx) -> {
                              return this.targetPlayer.method_24515().method_10093(dirx).equals(blockPos) && this.isCrystalBlock(this.targetPlayer.method_24515().method_10093(dirx).method_10074());
                           });
                        }
                     }
                  } while(block.method_26215() && !isPosGoodRebreak);
               } while(!BlockUtils.canBreak(blockPos, block) && !isPosGoodRebreak);

               if (this.silentMine.inBreakRange(blockPos)) {
                  double score = 0.0D;
                  if (inBedrock) {
                     score = this.scoreBedrockCityBlock(pos);
                  } else {
                     score = this.scoreNormalCityBlock(pos);
                  }

                  if (score != -1000.0D) {
                     if (isPosGoodRebreak) {
                        score += 40.0D;
                     }

                     if (score > bestScore) {
                        bestScore = score;
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onRender2d(Render2DEvent event) {
      if (this.targetPlayer != null) {
         if ((Boolean)this.renderDebugScores.get()) {
            Vector3d vec3 = new Vector3d();
            Set<AutoMine.CheckPos> checkPos = new HashSet();
            class_238 boundingBox = this.targetPlayer.method_5829().method_1002(0.01D, 0.1D, 0.01D);
            double feetY = this.targetPlayer.method_23318();
            class_238 feetBox = new class_238(boundingBox.field_1323, feetY, boundingBox.field_1321, boundingBox.field_1320, feetY + 0.1D, boundingBox.field_1324);
            boolean inBedrock = class_2338.method_29715(feetBox).anyMatch((blockPosx) -> {
               return this.mc.field_1687.method_8320(blockPosx).method_26204() == class_2246.field_9987;
            });
            if (inBedrock) {
               this.addBedrockCaseCheckPositions(checkPos);
            } else {
               this.addNormalCaseCheckPositions(checkPos);
            }

            Iterator var9 = checkPos.iterator();

            while(true) {
               AutoMine.CheckPos pos;
               class_2338 blockPos;
               class_2680 block;
               boolean isPosGoodRebreak;
               do {
                  do {
                     if (!var9.hasNext()) {
                        return;
                     }

                     pos = (AutoMine.CheckPos)var9.next();
                     blockPos = pos.blockPos;
                     block = this.mc.field_1687.method_8320(blockPos);
                     isPosGoodRebreak = false;
                     if (this.silentMine.canRebreakRebreakBlock() && blockPos.equals(this.silentMine.getRebreakBlockPos())) {
                        if (inBedrock) {
                           boolean isSelfTrapBlock = false;
                           class_2350[] var15 = class_2350.field_11041;
                           int var16 = var15.length;

                           for(int var17 = 0; var17 < var16; ++var17) {
                              class_2350 dir = var15[var17];
                              if (this.targetPlayer.method_24515().method_10084().method_10093(dir).equals(blockPos)) {
                                 isSelfTrapBlock = true;
                                 break;
                              }
                           }

                           boolean canFacePlace = this.mc.field_1687.method_8320(this.targetPlayer.method_24515().method_10084()).method_26215();
                           isPosGoodRebreak = class_2338.method_29715(feetBox).count() == 1L && (blockPos.equals(this.targetPlayer.method_24515().method_10086(2)) || isSelfTrapBlock && canFacePlace);
                        } else {
                           isPosGoodRebreak = !blockPos.equals(this.targetPlayer.method_24515()) && !this.isBlockInFeet(blockPos) && Arrays.stream(class_2350.field_11041).anyMatch((dirx) -> {
                              return this.targetPlayer.method_24515().method_10093(dirx).equals(blockPos) && this.isCrystalBlock(this.targetPlayer.method_24515().method_10093(dirx).method_10074());
                           });
                        }
                     }
                  } while(block.method_26215() && !isPosGoodRebreak);
               } while(!BlockUtils.canBreak(blockPos, block) && !isPosGoodRebreak);

               if (this.silentMine.inBreakRange(blockPos)) {
                  double score = 0.0D;
                  if (inBedrock) {
                     score = this.scoreBedrockCityBlock(pos);
                  } else {
                     score = this.scoreNormalCityBlock(pos);
                  }

                  if (score != -1000.0D) {
                     if (isPosGoodRebreak) {
                        score += 40.0D;
                     }

                     vec3.set(blockPos.method_46558().field_1352, blockPos.method_46558().field_1351, blockPos.method_46558().field_1350);
                     if (NametagUtils.to2D(vec3, 1.25D)) {
                        NametagUtils.begin(vec3);
                        TextRenderer.get().begin(1.0D, false, true);
                        String text = String.format("%.1f", score);
                        double w = TextRenderer.get().getWidth(text) / 2.0D;
                        TextRenderer.get().render(text, -w, 0.0D, Color.WHITE, true);
                        TextRenderer.get().end();
                        NametagUtils.end();
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.update();
         this.render3d(event);
      }
   }

   public String getInfoString() {
      return this.targetPlayer == null ? null : String.format("%s", EntityUtils.getName(this.targetPlayer));
   }

   private static enum AntiSwimMode {
      None,
      Always,
      OnMine,
      OnMineAndSwim;

      // $FF: synthetic method
      private static AutoMine.AntiSwimMode[] $values() {
         return new AutoMine.AntiSwimMode[]{None, Always, OnMine, OnMineAndSwim};
      }
   }

   private static enum AntiSurroundMode {
      None,
      Inner,
      Outer,
      Auto;

      // $FF: synthetic method
      private static AutoMine.AntiSurroundMode[] $values() {
         return new AutoMine.AntiSurroundMode[]{None, Inner, Outer, Auto};
      }
   }

   private class CityBlock {
      public class_2338 blockPos;
      public double score;
      public boolean isFeetBlock = false;

      private CityBlock(final AutoMine param1) {
      }
   }

   private class CheckPos {
      public final class_2338 blockPos;
      public final AutoMine.CheckPosType type;

      public CheckPos(final AutoMine param1, class_2338 blockPos, AutoMine.CheckPosType type) {
         this.blockPos = blockPos;
         this.type = type;
      }

      public int hashCode() {
         return this.blockPos.hashCode();
      }
   }

   public static enum CheckPosType {
      Feet,
      Surround,
      Extend,
      FacePlace,
      Head,
      Below;

      // $FF: synthetic method
      private static AutoMine.CheckPosType[] $values() {
         return new AutoMine.CheckPosType[]{Feet, Surround, Extend, FacePlace, Head, Below};
      }
   }
}
