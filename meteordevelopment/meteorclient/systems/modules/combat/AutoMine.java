package meteordevelopment.meteorclient.systems.modules.combat;

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
   private final Setting<Double> maxTargetMoveDistance;
   private final Setting<SortPriority> targetPriority;
   private final Setting<Boolean> ignoreNakeds;
   private final Setting<AutoMine.ExtendBreakMode> extendBreakMode;
   private final Setting<AutoMine.AntiSwimMode> antiSwim;
   private final Setting<AutoMine.AntiSurroundMode> antiSurroundMode;
   private final Setting<Boolean> antiSurroundInnerSnap;
   private final Setting<Boolean> antiSurroundOuterSnap;
   private final Setting<Double> antiSurroundOuterCooldown;
   private final Setting<Boolean> breakIndicatorsSync;
   private final Setting<Boolean> breakIndicatorsSyncOnlyFriends;
   private final Setting<Double> breakIndicatorSyncPenalty;
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
      this.maxTargetMoveDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-target-move-distance")).description("The maximum distance the target can move before canceling and updating.")).defaultValue(1.7D).min(1.0D).sliderMax(10.0D).build());
      this.targetPriority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to choose the target")).defaultValue(SortPriority.ClosestAngle)).build());
      this.ignoreNakeds = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-nakeds")).description("Ignore players with no items.")).defaultValue(true)).build());
      this.extendBreakMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("extend-break-mode")).description("How to mine outside of their surround to place crystals better")).defaultValue(AutoMine.ExtendBreakMode.None)).build());
      this.antiSwim = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("anti-swim-mode")).description("Starts mining your head block when the enemy starts mining your feet")).defaultValue(AutoMine.AntiSwimMode.OnMineAndSwim)).build());
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
      this.breakIndicatorsSync = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break-indicators-sync")).description("Syncs auto-mine scoring with break indicators. Basically leads to quad mine :)")).defaultValue(true)).build());
      this.breakIndicatorsSyncOnlyFriends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break-indicators-sync-only-friends")).description("Only penalizes blocks friends are mining")).defaultValue(false)).build());
      this.breakIndicatorSyncPenalty = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-indicators-sync-penalty")).description("Amount to penalize block for being broken by someone else")).defaultValue(8.5D).min(0.0D).sliderMax(25.0D).visible(() -> {
         return (Boolean)this.breakIndicatorsSync.get();
      })).build());
      this.renderDebugScores = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-debug-scores")).description("Renders scores and their blocks.")).defaultValue(false)).build());
      this.silentMine = null;
      this.targetPlayer = null;
      this.target1 = null;
      this.target2 = null;
      this.ignorePos = null;
      this.lastOuterPlaceTime = 0L;
   }

   public void onActivate() {
      super.onActivate();
      if (this.silentMine == null) {
         this.silentMine = (SilentMine)Modules.get().get(SilentMine.class);
      }

   }

   public void onDeactivate() {
      if (this.silentMine != null) {
         this.silentMine.cancelBreaking();
      }

      this.target1 = null;
      this.target2 = null;
      this.targetPlayer = null;
      super.onDeactivate();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.silentMine == null) {
         this.silentMine = (SilentMine)Modules.get().get(SilentMine.class);
      }

      if (this.targetPlayer != null && (this.target1 != null || this.target2 != null)) {
         class_2338 currentTargetPos = this.target1 != null ? this.target1.blockPos : this.target2.blockPos;
         double distance = this.targetPlayer.method_19538().method_1022(class_243.method_24953(currentTargetPos));
         if (distance > (Double)this.maxTargetMoveDistance.get()) {
            this.silentMine.cancelBreaking();
            this.target1 = null;
            this.target2 = null;
         }
      }

      this.update();
   }

   @EventHandler
   private void onSilentMineFinished(SilentMineFinishedEvent.Pre event) {
      if (this.targetPlayer != null && this.antiSurroundMode.get() != AutoMine.AntiSurroundMode.None) {
         class_2350[] var2;
         int var3;
         int var4;
         class_2350 dir;
         class_2338 playerSurroundBlock;
         if (this.antiSurroundMode.get() == AutoMine.AntiSurroundMode.Auto || this.antiSurroundMode.get() == AutoMine.AntiSurroundMode.Outer) {
            var2 = class_2350.field_11041;
            var3 = var2.length;

            label77:
            for(var4 = 0; var4 < var3; ++var4) {
               dir = var2[var4];
               playerSurroundBlock = this.targetPlayer.method_24515().method_10093(dir);
               if (event.getBlockPos().equals(playerSurroundBlock)) {
                  class_238 checkBox = class_238.method_30048(playerSurroundBlock.method_46558(), 2.5D, 3.0D, 2.5D);
                  class_238 blockHitbox = new class_238(playerSurroundBlock);
                  boolean outerSpeedCheck = (double)(System.currentTimeMillis() - this.lastOuterPlaceTime) > (Double)this.antiSurroundOuterCooldown.get() * 1000.0D;
                  if (!outerSpeedCheck) {
                     return;
                  }

                  Iterator var10 = BlockUtils.iterate(checkBox).iterator();

                  while(true) {
                     class_2338 blockPos;
                     class_2680 downState;
                     do {
                        do {
                           if (!var10.hasNext()) {
                              continue label77;
                           }

                           blockPos = (class_2338)var10.next();
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
                           this.lastOuterPlaceTime = System.currentTimeMillis();
                           return;
                        }
                     }
                  }
               }
            }
         }

         if (this.antiSurroundMode.get() == AutoMine.AntiSurroundMode.Auto || this.antiSurroundMode.get() == AutoMine.AntiSurroundMode.Inner) {
            var2 = class_2350.field_11041;
            var3 = var2.length;

            for(var4 = 0; var4 < var3; ++var4) {
               dir = var2[var4];
               playerSurroundBlock = this.targetPlayer.method_24515().method_10093(dir);
               if (playerSurroundBlock.equals(event.getBlockPos())) {
                  ((AutoCrystal)Modules.get().get(AutoCrystal.class)).preplaceCrystal(playerSurroundBlock, (Boolean)this.antiSurroundInnerSnap.get());
               }
            }
         }

      }
   }

   private void update() {
      if (this.silentMine == null) {
         this.silentMine = (SilentMine)Modules.get().get(SilentMine.class);
      }

      class_2338 selfHeadPos = this.mc.field_1724.method_24515().method_10084();
      class_2680 selfHeadBlock = this.mc.field_1687.method_8320(selfHeadPos);
      class_2680 selfFeetBlock = this.mc.field_1687.method_8320(this.mc.field_1724.method_24515());
      boolean isSwimming = this.mc.field_1724.method_5869() || this.mc.field_1724.method_5771();
      boolean shouldBreakSelfHead = BlockUtils.canBreak(selfHeadPos, selfHeadBlock) && (selfHeadBlock.method_27852(class_2246.field_10540) || selfHeadBlock.method_27852(class_2246.field_22423));
      boolean prioHead = false;
      if (this.antiSwim.get() == AutoMine.AntiSwimMode.Always && shouldBreakSelfHead) {
         this.silentMine.silentBreakBlock(selfHeadPos, class_2350.field_11036, 10.0D);
         prioHead = true;
      }

      if (this.antiSwim.get() == AutoMine.AntiSwimMode.OnMineAndSwim && this.mc.field_1724.method_20448() && shouldBreakSelfHead) {
         this.silentMine.silentBreakBlock(selfHeadPos, class_2350.field_11036, 30.0D);
         prioHead = true;
      }

      if ((this.antiSwim.get() == AutoMine.AntiSwimMode.OnMine || this.antiSwim.get() == AutoMine.AntiSwimMode.OnMineAndSwim) && ((BreakIndicators)Modules.get().get(BreakIndicators.class)).isBlockBeingBroken(this.mc.field_1724.method_24515()) && shouldBreakSelfHead) {
         this.silentMine.silentBreakBlock(selfHeadPos, class_2350.field_11036, 20.0D);
         prioHead = true;
      }

      this.targetPlayer = (class_1657)TargetUtils.get((entity) -> {
         if (!entity.equals(this.mc.field_1724) && !entity.equals(this.mc.field_1719)) {
            if (entity instanceof class_1657) {
               class_1657 player = (class_1657)entity;
               if (player.method_5805() && !player.method_29504() && !player.method_7337() && Friends.get().shouldAttack(player)) {
                  if (entity.method_19538().method_1022(this.mc.field_1724.method_33571()) > (Double)this.range.get()) {
                     return false;
                  } else {
                     return !(Boolean)this.ignoreNakeds.get() || !player.method_31548().field_7548.stream().allMatch((itemStack) -> {
                        return itemStack.method_7960();
                     });
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
      if (this.targetPlayer == null) {
         if (!prioHead) {
            ;
         }
      } else if (!this.silentMine.hasDelayedDestroy() || !selfHeadBlock.method_27852(class_2246.field_10540) || !selfFeetBlock.method_26215() || this.silentMine.getRebreakBlockPos() == null || !this.silentMine.getRebreakBlockPos().equals(selfHeadPos)) {
         if (!prioHead) {
            this.findTargetBlocks();
            Queue<class_2338> targetBlocks = new LinkedList();
            if (this.target1 != null) {
               targetBlocks.add(this.target1.blockPos);
            }

            if (this.target2 != null) {
               targetBlocks.add(this.target2.blockPos);
            }

            class_2338 enemyBlock;
            if (!targetBlocks.isEmpty() && !this.silentMine.hasDelayedDestroy()) {
               enemyBlock = (class_2338)targetBlocks.remove();
               this.silentMine.silentBreakBlock(enemyBlock, class_2350.field_11036, 10.0D);
            }

            if (isSwimming && shouldBreakSelfHead) {
               if (!this.silentMine.hasRebreakBlock() || this.silentMine.canRebreakRebreakBlock()) {
                  this.silentMine.silentBreakBlock(selfHeadPos, class_2350.field_11036, 20.0D);
               }

            } else {
               if (!targetBlocks.isEmpty()) {
                  if (!this.silentMine.hasDelayedDestroy()) {
                     enemyBlock = (class_2338)targetBlocks.remove();
                     this.silentMine.silentBreakBlock(enemyBlock, class_2350.field_11036, 10.0D);
                  }

                  if (!this.silentMine.hasRebreakBlock() || this.silentMine.canRebreakRebreakBlock()) {
                     enemyBlock = (class_2338)targetBlocks.poll();
                     if (enemyBlock != null) {
                        this.silentMine.silentBreakBlock(enemyBlock, class_2350.field_11036, 10.0D);
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
            do {
               do {
                  do {
                     if (!var10.hasNext()) {
                        return set ? bestBlock : null;
                     }

                     pos = (AutoMine.CheckPos)var10.next();
                     blockPos = pos.blockPos;
                  } while(blockPos.equals(exclude));

                  block = this.mc.field_1687.method_8320(blockPos);
                  isPosGoodRebreak = this.silentMine.canRebreakRebreakBlock() && blockPos.equals(this.silentMine.getRebreakBlockPos());
               } while(block.method_26215() && !isPosGoodRebreak);
            } while(!BlockUtils.canBreak(blockPos, block) && !isPosGoodRebreak);

            if (this.silentMine.inBreakRange(blockPos)) {
               double score = inBedrock ? this.scoreBedrockCityBlock(pos) : this.scoreNormalCityBlock(pos);
               if (score != -1000.0D) {
                  if (isPosGoodRebreak) {
                     score += 40.0D;
                  } else {
                     score -= this.getScorePenaltyForSync(pos.blockPos);
                  }

                  if (score > bestBlock.score) {
                     bestBlock.score = score;
                     bestBlock.blockPos = blockPos;
                     bestBlock.isFeetBlock = this.isBlockInFeet(blockPos);
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

      class_2350 perpDir;
      while(var6.hasNext()) {
         pos = (class_2338)var6.next();
         Iterator var8 = class_2353.field_11062.iterator();

         while(var8.hasNext()) {
            perpDir = (class_2350)var8.next();
            checkPos.add(new AutoMine.CheckPos(this, pos.method_10093(perpDir), AutoMine.CheckPosType.Surround));
         }
      }

      checkPos.add(new AutoMine.CheckPos(this, this.targetPlayer.method_24515(), AutoMine.CheckPosType.Feet));
      boolean inMultipleBlocks = class_2338.method_29715(feetBox).count() > 1L;
      if (!inMultipleBlocks) {
         Iterator var11 = class_2353.field_11062.iterator();

         while(var11.hasNext()) {
            class_2350 dir = (class_2350)var11.next();
            switch(((AutoMine.ExtendBreakMode)this.extendBreakMode.get()).ordinal()) {
            case 0:
            default:
               break;
            case 1:
               checkPos.add(new AutoMine.CheckPos(this, this.targetPlayer.method_24515().method_10079(dir, 2), AutoMine.CheckPosType.Extend));
               break;
            case 2:
               perpDir = this.getCornerPerpDir(dir);
               checkPos.add(new AutoMine.CheckPos(this, this.targetPlayer.method_24515().method_10093(dir).method_10093(perpDir), AutoMine.CheckPosType.Extend));
            }
         }
      }

   }

   private class_2350 getCornerPerpDir(class_2350 dir) {
      class_2350 var10000;
      switch(dir) {
      case field_11043:
         var10000 = class_2350.field_11034;
         break;
      case field_11035:
         var10000 = class_2350.field_11039;
         break;
      case field_11034:
         var10000 = class_2350.field_11043;
         break;
      case field_11039:
         var10000 = class_2350.field_11035;
         break;
      default:
         var10000 = null;
      }

      return var10000;
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
      class_2680 headBlock;
      if (blockPos.equals(this.targetPlayer.method_24515())) {
         headBlock = this.mc.field_1687.method_8320(blockPos.method_10084());
         if (headBlock.method_26204().equals(class_2246.field_10540)) {
            score += 100.0D;
         } else {
            if (block.method_26204() == class_2246.field_10343) {
               return -1000.0D;
            }

            score += 50.0D;
         }
      } else {
         headBlock = this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10084());
         if (blockPos.equals(this.mc.field_1724.method_24515()) && (headBlock.method_26204().equals(class_2246.field_10540) || headBlock.method_26204().equals(class_2246.field_9987))) {
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

                  class_2350 perpDir = this.getCornerPerpDir(dir);
                  class_2338 antiSurroundCornerBlockPos = this.targetPlayer.method_24515().method_10093(dir).method_10093(perpDir);
                  if (this.getBlockStateIgnore(antiSurroundCornerBlockPos).method_26215() && this.isCrystalBlock(antiSurroundCornerBlockPos.method_10074())) {
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
      return blockPos != null && !blockPos.equals(this.ignorePos) ? this.mc.field_1687.method_8320(blockPos) : class_2246.field_10124.method_9564();
   }

   private double getScorePenaltyForSync(class_2338 blockPos) {
      if (!(Boolean)this.breakIndicatorsSync.get()) {
         return 0.0D;
      } else {
         BreakIndicators breakIndicators = (BreakIndicators)Modules.get().get(BreakIndicators.class);
         if (breakIndicators.isBeingDoublemined(blockPos)) {
            return (Boolean)this.breakIndicatorsSyncOnlyFriends.get() && !Friends.get().isFriend(breakIndicators.getPlayerDoubleminingBlock(blockPos)) ? 0.0D : (Double)this.breakIndicatorSyncPenalty.get();
         } else {
            return 0.0D;
         }
      }
   }

   public boolean isTargetingAnything() {
      return this.target1 != null || this.target2 != null;
   }

   private void render3d(Render3DEvent event) {
      if (this.targetPlayer != null && (Boolean)this.renderDebugScores.get()) {
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
                  if (!var10.hasNext()) {
                     Color color = Color.RED;
                     Iterator var21 = checkPos.iterator();

                     while(true) {
                        AutoMine.CheckPos pos;
                        class_2338 blockPos;
                        class_2680 block;
                        boolean isPosGoodRebreak;
                        do {
                           do {
                              if (!var21.hasNext()) {
                                 return;
                              }

                              pos = (AutoMine.CheckPos)var21.next();
                              blockPos = pos.blockPos;
                              block = this.mc.field_1687.method_8320(blockPos);
                              isPosGoodRebreak = this.silentMine.canRebreakRebreakBlock() && blockPos.equals(this.silentMine.getRebreakBlockPos());
                           } while(block.method_26215() && !isPosGoodRebreak);
                        } while(!BlockUtils.canBreak(blockPos, block) && !isPosGoodRebreak);

                        if (this.silentMine.inBreakRange(blockPos)) {
                           double score = inBedrock ? this.scoreBedrockCityBlock(pos) : this.scoreNormalCityBlock(pos);
                           if (score != -1000.0D) {
                              if (isPosGoodRebreak) {
                                 score += 40.0D;
                              } else {
                                 score -= this.getScorePenaltyForSync(pos.blockPos);
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
                  isPosGoodRebreak = this.silentMine.canRebreakRebreakBlock() && blockPos.equals(this.silentMine.getRebreakBlockPos());
               } while(block.method_26215() && !isPosGoodRebreak);
            } while(!BlockUtils.canBreak(blockPos, block) && !isPosGoodRebreak);

            if (this.silentMine.inBreakRange(blockPos)) {
               double score = inBedrock ? this.scoreBedrockCityBlock(pos) : this.scoreNormalCityBlock(pos);
               if (score != -1000.0D) {
                  if (isPosGoodRebreak) {
                     score += 40.0D;
                  } else {
                     score -= this.getScorePenaltyForSync(pos.blockPos);
                  }

                  if (score > bestScore) {
                     bestScore = score;
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onRender2d(Render2DEvent event) {
      if (this.targetPlayer != null && (Boolean)this.renderDebugScores.get()) {
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
                  isPosGoodRebreak = this.silentMine.canRebreakRebreakBlock() && blockPos.equals(this.silentMine.getRebreakBlockPos());
               } while(block.method_26215() && !isPosGoodRebreak);
            } while(!BlockUtils.canBreak(blockPos, block) && !isPosGoodRebreak);

            if (this.silentMine.inBreakRange(blockPos)) {
               double score = inBedrock ? this.scoreBedrockCityBlock(pos) : this.scoreNormalCityBlock(pos);
               if (score != -1000.0D) {
                  if (isPosGoodRebreak) {
                     score += 40.0D;
                  } else {
                     score -= this.getScorePenaltyForSync(pos.blockPos);
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

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.update();
         this.render3d(event);
      }
   }

   public String getInfoString() {
      return this.targetPlayer != null ? EntityUtils.getName(this.targetPlayer) : null;
   }

   private static enum ExtendBreakMode {
      None,
      Long,
      Corner;

      // $FF: synthetic method
      private static AutoMine.ExtendBreakMode[] $values() {
         return new AutoMine.ExtendBreakMode[]{None, Long, Corner};
      }
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
