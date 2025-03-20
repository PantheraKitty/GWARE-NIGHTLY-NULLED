package meteordevelopment.meteorclient.systems.modules.combat.autocrystal;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.PlayerDeathEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixininterface.IBox;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.AutoMine;
import meteordevelopment.meteorclient.systems.modules.player.SilentMine;
import meteordevelopment.meteorclient.systems.modules.render.BreakIndicators;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2824;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_3965;
import net.minecraft.class_2338.class_2339;

public class AutoCrystal extends Module {
   private final SettingGroup sgGeneral;
   public final SettingGroup sgPlace;
   private final SettingGroup sgFacePlace;
   private final SettingGroup sgBreak;
   private final SettingGroup sgRotate;
   private final SettingGroup sgSwing;
   private final SettingGroup sgRange;
   private final AutoCrystalRenderer renderer;
   private final Setting<Boolean> placeCrystals;
   private final Setting<Boolean> pauseEatPlace;
   private final Setting<Boolean> breakCrystals;
   private final Setting<Boolean> pauseEatBreak;
   private final Setting<Boolean> ignoreNakeds;
   private final Setting<Boolean> setPlayerDead;
   private final Setting<Boolean> predictBlock;
   private final Setting<Double> predictBlockThreshold;
   private final Setting<Boolean> cevHead;
   private final Setting<Boolean> ignoreCalc;
   private final Setting<Double> placeSpeedLimit;
   private final Setting<Double> minPlace;
   private final Setting<Double> maxPlace;
   private final Setting<Boolean> antiSurroundPlace;
   private final Setting<Double> placeDelay;
   private final Setting<Boolean> ignoreItems;
   private final Setting<Boolean> ignoreTrapdoors;
   private final Setting<Boolean> facePlaceMissingArmor;
   private final Setting<Keybind> forceFacePlaceKeybind;
   private final Setting<Boolean> slowPlace;
   private final Setting<Double> slowPlaceMinDamage;
   private final Setting<Double> slowPlaceMaxDamage;
   private final Setting<Double> slowPlaceSpeed;
   private final Setting<Double> breakSpeedLimit;
   private final Setting<Boolean> packetBreak;
   private final Setting<Double> minBreak;
   private final Setting<Double> maxBreak;
   private final Setting<Double> breakDelay;
   private final Setting<Boolean> rotatePlace;
   private final Setting<Boolean> rotateBreak;
   private final Setting<AutoCrystal.SwingMode> breakSwingMode;
   private final Setting<AutoCrystal.SwingMode> placeSwingMode;
   public final Setting<Double> placeRange;
   private final Setting<Double> breakRange;
   private static final Set<class_2248> TRAPDOOR_BLOCKS = new HashSet<class_2248>() {
      {
         this.add(class_2246.field_10137);
         this.add(class_2246.field_10323);
         this.add(class_2246.field_10486);
         this.add(class_2246.field_10017);
         this.add(class_2246.field_10608);
         this.add(class_2246.field_10246);
         this.add(class_2246.field_37555);
         this.add(class_2246.field_42740);
         this.add(class_2246.field_40285);
         this.add(class_2246.field_22094);
         this.add(class_2246.field_22095);
      }
   };
   public final List<class_1297> forceBreakCrystals;
   private final Pool<AutoCrystal.PlacePosition> placePositionPool;
   private final List<AutoCrystal.PlacePosition> _placePositions;
   private final class_2339 mutablePos;
   private final IntSet brokenCrystals;
   private final Map<Integer, Long> crystalBreakDelays;
   private final Map<class_2338, Long> crystalPlaceDelays;
   public final List<Boolean> cachedValidSpots;
   private long lastPlaceTimeMS;
   private long lastBreakTimeMS;
   private AutoMine autoMine;
   private class_2338 lastChosenPos;
   private int placeAttempts;
   private class_2338 lastFurtherPos;
   private Set<class_2338> _calcIgnoreSet;

   public AutoCrystal() {
      super(Categories.Combat, "auto-crystal", "Automatically places and attacks crystals.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlace = this.settings.createGroup("Place");
      this.sgFacePlace = this.settings.createGroup("Face Place");
      this.sgBreak = this.settings.createGroup("Break");
      this.sgRotate = this.settings.createGroup("Rotate");
      this.sgSwing = this.settings.createGroup("Swing");
      this.sgRange = this.settings.createGroup("Range");
      this.renderer = new AutoCrystalRenderer(this);
      this.placeCrystals = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place")).description("Places crystals.")).defaultValue(true)).build());
      this.pauseEatPlace = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-eat-place")).description("Pauses placing when eating")).defaultValue(true)).build());
      this.breakCrystals = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break")).description("Breaks crystals.")).defaultValue(true)).build());
      this.pauseEatBreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-eat-break")).description("Pauses placing when breaking")).defaultValue(false)).build());
      this.ignoreNakeds = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-nakeds")).description("Ignore players with no items.")).defaultValue(true)).build());
      this.setPlayerDead = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("set-player-dead-instantly")).description("Tries to not blow up loot by instantly killing the player in the packet they die.")).defaultValue(true)).build());
      this.predictBlock = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict-block")).description("Predicts block damage")).defaultValue(false)).build());
      this.predictBlockThreshold = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("predict-block-threshold")).description("Block break progress threshold for pre-placing crystals.")).defaultValue(95.0D).min(80.0D).max(99.0D).sliderRange(80.0D, 99.0D).visible(() -> {
         return (Boolean)this.predictBlock.get();
      })).build());
      this.cevHead = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("cev-head")).description("So you wanna be a 1.12 player?")).defaultValue(false)).build());
      this.ignoreCalc = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Laser")).description("It BEAMS people.")).defaultValue(true)).build());
      this.placeSpeedLimit = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-speed-limit")).description("Maximum number of crystals to place every second.")).defaultValue(40.0D).min(0.0D).sliderRange(0.0D, 40.0D).build());
      this.minPlace = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-place")).description("Minimum enemy damage to place.")).defaultValue(8.0D).min(0.0D).sliderRange(0.0D, 20.0D).build());
      this.maxPlace = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-place")).description("Max self damage to place.")).defaultValue(20.0D).min(0.0D).sliderRange(0.0D, 20.0D).build());
      this.antiSurroundPlace = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-surround")).description("Ignores auto-mine blocks from calculations to place outside of their surround.")).defaultValue(true)).build());
      this.placeDelay = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-delay")).description("The number of seconds to wait to retry placing a crystal at a position.")).defaultValue(0.05D).min(0.0D).sliderMax(0.6D).build());
      this.ignoreItems = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-items")).description("Places on items. Good for low ping.")).defaultValue(true)).build());
      this.ignoreTrapdoors = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-trapdoors")).description("Places crystals even if a trapdoor is in the way.")).defaultValue(true)).build());
      this.facePlaceMissingArmor = this.sgFacePlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("face-place-missing-armor")).description("Face places on missing armor")).defaultValue(true)).build());
      this.forceFacePlaceKeybind = this.sgFacePlace.add(((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-face-place")).description("Keybind to force face place")).build());
      this.slowPlace = this.sgFacePlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("slow-place")).description("Slowly places crystals at lower damages.")).defaultValue(true)).build());
      this.slowPlaceMinDamage = this.sgFacePlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("slow-place-min-place")).description("Minimum damage to slow place.")).defaultValue(4.0D).min(0.0D).sliderRange(0.0D, 20.0D).visible(() -> {
         return (Boolean)this.slowPlace.get();
      })).build());
      this.slowPlaceMaxDamage = this.sgFacePlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("slow-place-max-place")).description("Maximum damage to slow place.")).defaultValue(8.0D).min(0.0D).sliderRange(0.0D, 20.0D).visible(() -> {
         return (Boolean)this.slowPlace.get();
      })).build());
      this.slowPlaceSpeed = this.sgFacePlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("slow-place-speed")).description("Speed at which to slow place.")).defaultValue(2.0D).min(0.0D).sliderRange(0.0D, 20.0D).visible(() -> {
         return (Boolean)this.slowPlace.get();
      })).build());
      this.breakSpeedLimit = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-speed-limit")).description("Maximum number of crystals to break every second.")).defaultValue(60.0D).min(0.0D).sliderRange(0.0D, 60.0D).build());
      this.packetBreak = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("packet-break")).description("Breaks when the crystal packet arrives")).defaultValue(true)).build());
      this.minBreak = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-break")).description("Minimum enemy damage to break.")).defaultValue(3.0D).min(0.0D).sliderRange(0.0D, 20.0D).build());
      this.maxBreak = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-break")).description("Max self damage to break.")).defaultValue(20.0D).min(0.0D).sliderRange(0.0D, 20.0D).build());
      this.breakDelay = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-delay")).description("The number of seconds to wait to retry breaking a crystal.")).defaultValue(0.05D).min(0.0D).sliderMax(0.6D).build());
      this.rotatePlace = this.sgRotate.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate-place")).description("Rotates server-side towards the crystals when placed.")).defaultValue(false)).build());
      this.rotateBreak = this.sgRotate.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate-break")).description("Rotates server-side towards the crystals when broken.")).defaultValue(true)).build());
      this.breakSwingMode = this.sgSwing.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("break-swing-mode")).description("Mode for swinging your hand when breaking")).defaultValue(AutoCrystal.SwingMode.None)).build());
      this.placeSwingMode = this.sgSwing.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("place-swing-mode")).description("Mode for swinging your hand when placing")).defaultValue(AutoCrystal.SwingMode.None)).build());
      this.placeRange = this.sgRange.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("Maximum distance to place crystals for")).defaultValue(4.0D).build());
      this.breakRange = this.sgRange.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-range")).description("Maximum distance to break crystals for")).defaultValue(4.0D).build());
      this.forceBreakCrystals = new ArrayList();
      this.placePositionPool = new Pool(() -> {
         return new AutoCrystal.PlacePosition(this);
      });
      this._placePositions = new ArrayList();
      this.mutablePos = new class_2339();
      this.brokenCrystals = new IntOpenHashSet();
      this.crystalBreakDelays = new HashMap();
      this.crystalPlaceDelays = new HashMap();
      this.cachedValidSpots = new ArrayList();
      this.lastPlaceTimeMS = 0L;
      this.lastBreakTimeMS = 0L;
      this.lastChosenPos = null;
      this.placeAttempts = 0;
      this.lastFurtherPos = null;
      this._calcIgnoreSet = new HashSet();
   }

   public void onActivate() {
      if (this.autoMine == null) {
         this.autoMine = (AutoMine)Modules.get().get(AutoMine.class);
      }

      this.crystalBreakDelays.clear();
      this.crystalPlaceDelays.clear();
      this.lastFurtherPos = null;
      this.renderer.onActivate();
   }

   private void update() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null && !this.mc.field_1687.method_18456().isEmpty()) {
         if (this.autoMine == null) {
            this.autoMine = (AutoMine)Modules.get().get(AutoMine.class);
         }

         Iterator var1 = this._placePositions.iterator();

         while(var1.hasNext()) {
            AutoCrystal.PlacePosition p = (AutoCrystal.PlacePosition)var1.next();
            this.placePositionPool.free(p);
         }

         this._placePositions.clear();
         AutoCrystal.PlacePosition bestPlacePos = null;
         Iterator var12;
         if ((Boolean)this.placeCrystals.get() && (!(Boolean)this.pauseEatPlace.get() || !this.mc.field_1724.method_6115())) {
            this.cachedValidPlaceSpots();
            this.preplaceCrystal((class_2338)null, true);
            var12 = this.mc.field_1687.method_18456().iterator();

            label191:
            while(true) {
               AutoCrystal.PlacePosition testPos;
               do {
                  do {
                     class_1657 player;
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    if (!var12.hasNext()) {
                                       break label191;
                                    }

                                    player = (class_1657)var12.next();
                                 } while(player == this.mc.field_1724);
                              } while(Friends.get().isFriend(player));
                           } while(player.method_29504());
                        } while((Boolean)this.ignoreNakeds.get() && ((class_1799)player.method_31548().field_7548.get(0)).method_7960() && ((class_1799)player.method_31548().field_7548.get(1)).method_7960() && ((class_1799)player.method_31548().field_7548.get(2)).method_7960() && ((class_1799)player.method_31548().field_7548.get(3)).method_7960());
                     } while(player.method_5707(this.mc.field_1724.method_33571()) > 144.0D);

                     if ((Boolean)this.cevHead.get() && this.isPlayerPhased(player)) {
                        class_2338 headBlockPos = class_2338.method_49638(player.method_19538()).method_10084();
                        class_2680 headBlockState = this.mc.field_1687.method_8320(headBlockPos);
                        if (headBlockState.method_26214(this.mc.field_1687, headBlockPos) >= 0.0F && this.inPlaceRange(headBlockPos)) {
                           class_2338 crystalPos = headBlockPos.method_10084();
                           class_2680 crystalBlockState = this.mc.field_1687.method_8320(crystalPos);
                           if ((crystalBlockState.method_26215() || (Boolean)this.ignoreTrapdoors.get() && TRAPDOOR_BLOCKS.contains(crystalBlockState.method_26204())) && !this.crystalPlaceDelays.containsKey(crystalPos.method_10074())) {
                              BreakIndicators breakIndicators = (BreakIndicators)Modules.get().get(BreakIndicators.class);
                              if (breakIndicators != null && breakIndicators.isBlockBeingBroken(headBlockPos)) {
                                 double breakProgress = ((BreakIndicators.BlockBreak)breakIndicators.breakStartTimes.get(headBlockPos)).getBreakProgress(RenderUtils.getCurrentGameTickCalculated());
                                 if (breakProgress >= (Double)this.predictBlockThreshold.get() / 100.0D) {
                                    this.preplaceCrystal(crystalPos, true);
                                    break label191;
                                 }
                              }
                           }
                        }
                     }

                     testPos = this.findBestPlacePosition(player);
                  } while(testPos == null);
               } while(bestPlacePos != null && !(testPos.damage > bestPlacePos.damage));

               bestPlacePos = testPos;
            }

            long currentTime = System.currentTimeMillis();
            if (bestPlacePos != null && this.placeSpeedCheck(bestPlacePos.isSlowPlace) && this.placeCrystal(bestPlacePos.blockPos.method_10074())) {
               this.lastPlaceTimeMS = currentTime;
            }

            if ((Boolean)this.predictBlock.get()) {
               BreakIndicators breakIndicators = (BreakIndicators)Modules.get().get(BreakIndicators.class);
               if (breakIndicators != null) {
                  Iterator var17 = breakIndicators.breakStartTimes.entrySet().iterator();

                  label126:
                  while(true) {
                     class_2338 crystalPos;
                     do {
                        class_2338 blockPos;
                        double breakProgress;
                        do {
                           Entry entry;
                           do {
                              if (!var17.hasNext()) {
                                 break label126;
                              }

                              entry = (Entry)var17.next();
                              blockPos = (class_2338)entry.getKey();
                           } while(!breakIndicators.isBlockBeingBroken(blockPos));

                           breakProgress = ((BreakIndicators.BlockBreak)entry.getValue()).getBreakProgress(RenderUtils.getCurrentGameTickCalculated());
                        } while(!(breakProgress >= (Double)this.predictBlockThreshold.get() / 100.0D));

                        crystalPos = blockPos.method_10084();
                     } while(!this.mc.field_1687.method_22347(crystalPos) && (!(Boolean)this.ignoreTrapdoors.get() || !TRAPDOOR_BLOCKS.contains(this.mc.field_1687.method_8320(crystalPos).method_26204())));

                     if (this.inPlaceRange(crystalPos)) {
                        this.preplaceCrystal(crystalPos, true);
                     }
                  }
               }
            }
         }

         if ((Boolean)this.breakCrystals.get() && (!(Boolean)this.pauseEatBreak.get() || !this.mc.field_1724.method_6115())) {
            var12 = this.mc.field_1687.method_18112().iterator();

            while(var12.hasNext()) {
               class_1297 entity = (class_1297)var12.next();
               if (entity instanceof class_1511 && this.inBreakRange(entity.method_19538()) && this.shouldBreakCrystal(entity) && (!this.breakSpeedCheck() || !this.breakCrystal(entity) && (Boolean)this.rotateBreak.get() && !MeteorClient.ROTATION.lookingAt(entity.method_5829()))) {
                  break;
               }
            }
         }

      }
   }

   private boolean isPlayerPhased(class_1657 player) {
      boolean isCrawling = player.method_20448();
      class_238 lowerHitbox = isCrawling ? new class_238(player.method_23317() - 0.3D, player.method_23318(), player.method_23321() - 0.3D, player.method_23317() + 0.3D, player.method_23318() + 0.6D, player.method_23321() + 0.3D) : new class_238(player.method_23317() - 0.3D, player.method_23318(), player.method_23321() - 0.3D, player.method_23317() + 0.3D, player.method_23318() + 1.8D, player.method_23321() + 0.3D);
      boolean lowerHitboxPhased = this.mc.field_1687.method_20812(player, lowerHitbox).iterator().hasNext();
      class_238 fullHitbox = player.method_5829();
      boolean fullHitboxPhased = this.mc.field_1687.method_20812(player, fullHitbox).iterator().hasNext();
      return lowerHitboxPhased || fullHitboxPhased;
   }

   public boolean placeCrystal(class_2338 blockPos) {
      if (blockPos != null && this.mc.field_1724 != null) {
         class_2338 crystalPos = blockPos.method_10084();
         class_2680 crystalState = this.mc.field_1687.method_8320(crystalPos);
         if (this.mc.field_1687.method_22347(crystalPos) || (Boolean)this.ignoreTrapdoors.get() && TRAPDOOR_BLOCKS.contains(crystalState.method_26204())) {
            class_238 box = new class_238((double)crystalPos.method_10263(), (double)crystalPos.method_10264(), (double)crystalPos.method_10260(), (double)(crystalPos.method_10263() + 1), (double)(crystalPos.method_10264() + 2), (double)(crystalPos.method_10260() + 1));
            if (this.intersectsWithEntities(box)) {
               return false;
            } else {
               FindItemResult result = InvUtils.find(class_1802.field_8301);
               if (!result.found()) {
                  return false;
               } else {
                  boolean isHoldingCrystal = this.mc.field_1724.method_6047().method_7909() == class_1802.field_8301 || this.mc.field_1724.method_6079().method_7909() == class_1802.field_8301;
                  if ((Boolean)this.rotatePlace.get()) {
                     MeteorClient.ROTATION.requestRotation(blockPos.method_46558(), 10.0D);
                     if (!MeteorClient.ROTATION.lookingAt(new class_238(blockPos))) {
                        return false;
                     }
                  }

                  long currentTime = System.currentTimeMillis();
                  if (this.crystalPlaceDelays.containsKey(blockPos) && (double)(currentTime - (Long)this.crystalPlaceDelays.get(blockPos)) / 1000.0D < (Double)this.placeDelay.get()) {
                     return false;
                  } else if (!isHoldingCrystal && !MeteorClient.SWAP.beginSwap(result, true)) {
                     return false;
                  } else {
                     this.crystalPlaceDelays.put(blockPos, currentTime);
                     this.renderer.onPlaceCrystal(blockPos);
                     class_3965 calculatedHitResult = AutoCrystalUtil.getPlaceBlockHitResult(blockPos);
                     class_1268 hand = this.mc.field_1724.method_6047().method_7909() == class_1802.field_8301 ? class_1268.field_5808 : class_1268.field_5810;
                     int s = this.mc.field_1687.method_41925().method_41937().method_41942();
                     this.mc.field_1724.field_3944.method_52787(new class_2885(hand, calculatedHitResult, s));
                     if (this.placeSwingMode.get() == AutoCrystal.SwingMode.Client) {
                        this.mc.field_1724.method_6104(hand);
                     }

                     if (this.placeSwingMode.get() == AutoCrystal.SwingMode.Packet) {
                        this.mc.method_1562().method_52787(new class_2879(hand));
                     }

                     if (!isHoldingCrystal) {
                        MeteorClient.SWAP.endSwap(true);
                     }

                     return true;
                  }
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean breakCrystal(class_1297 entity) {
      if ((Boolean)this.pauseEatBreak.get() && this.mc.field_1724.method_6115()) {
         return false;
      } else if (this.mc.field_1724 == null) {
         return false;
      } else {
         if ((Boolean)this.rotateBreak.get()) {
            MeteorClient.ROTATION.requestRotation(entity.method_19538(), 10.0D);
            if (!MeteorClient.ROTATION.lookingAt(entity.method_5829())) {
               return false;
            }
         }

         long currentTime = System.currentTimeMillis();
         if (this.crystalBreakDelays.containsKey(entity.method_5628()) && (double)(currentTime - (Long)this.crystalBreakDelays.get(entity.method_5628())) / 1000.0D < (Double)this.breakDelay.get()) {
            return false;
         } else {
            this.crystalBreakDelays.put(entity.method_5628(), currentTime);
            this.brokenCrystals.add(entity.method_5628());
            this.renderer.onBreakCrystal(entity);
            class_2824 packet = class_2824.method_34206(entity, this.mc.field_1724.method_5715());
            this.mc.method_1562().method_52787(packet);
            if (this.breakSwingMode.get() == AutoCrystal.SwingMode.Client) {
               this.mc.field_1724.method_6104(class_1268.field_5808);
            }

            if (this.breakSwingMode.get() == AutoCrystal.SwingMode.Packet) {
               this.mc.method_1562().method_52787(new class_2879(class_1268.field_5808));
            }

            this.lastBreakTimeMS = System.currentTimeMillis();
            return true;
         }
      }
   }

   private AutoCrystal.PlacePosition findBestPlacePosition(class_1657 target) {
      AutoCrystal.PlacePosition bestPos = (AutoCrystal.PlacePosition)this.placePositionPool.get();
      this._placePositions.add(bestPos);
      bestPos.damage = 0.0D;
      bestPos.blockPos = null;
      bestPos.isSlowPlace = false;
      int r = (int)Math.floor((Double)this.placeRange.get());
      class_2338 eyePos = class_2338.method_49638(this.mc.field_1724.method_33571());
      int ex = eyePos.method_10263();
      int ey = eyePos.method_10264();
      int ez = eyePos.method_10260();
      boolean set = false;
      this._calcIgnoreSet.clear();
      if ((Boolean)this.antiSurroundPlace.get()) {
         SilentMine silentMine = (SilentMine)Modules.get().get(SilentMine.class);
         if (silentMine.isActive()) {
            if (silentMine.getDelayedDestroyBlockPos() != null) {
               this._calcIgnoreSet.add(silentMine.getDelayedDestroyBlockPos());
            }

            if (silentMine.getRebreakBlockPos() != null) {
               this._calcIgnoreSet.add(silentMine.getRebreakBlockPos());
            }
         }
      }

      boolean shouldFacePlace = false;
      if ((Boolean)this.facePlaceMissingArmor.get() && (((class_1799)target.method_31548().field_7548.get(0)).method_7960() || ((class_1799)target.method_31548().field_7548.get(1)).method_7960() || ((class_1799)target.method_31548().field_7548.get(2)).method_7960() || ((class_1799)target.method_31548().field_7548.get(3)).method_7960())) {
         shouldFacePlace = true;
      }

      if (((Keybind)this.forceFacePlaceKeybind.get()).isPressed()) {
         shouldFacePlace = true;
      }

      for(int x = -r; x <= r; ++x) {
         for(int y = -r; y <= r; ++y) {
            for(int z = -r; z <= r; ++z) {
               if ((Boolean)this.cachedValidSpots.get((x + r) * 2 * r * 2 * r + (y + r) * 2 * r + z + r)) {
                  class_2338 pos = this.mutablePos.method_10103(ex + x, ey + y, ez + z);
                  if (!this.isPlayerPhased(target)) {
                     double targetDamage = DamageUtils.newCrystalDamage(target, target.method_5829(), new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264(), (double)pos.method_10260() + 0.5D), this._calcIgnoreSet);
                     boolean shouldSet = targetDamage >= (shouldFacePlace ? 1.0D : (Double)this.minPlace.get()) && targetDamage > bestPos.damage;
                     boolean isSlowPlace = false;
                     if ((Boolean)this.slowPlace.get() && targetDamage > bestPos.damage && targetDamage <= (Double)this.slowPlaceMaxDamage.get() && targetDamage >= (Double)this.slowPlaceMinDamage.get()) {
                        shouldSet = true;
                        isSlowPlace = true;
                     }

                     if (shouldSet) {
                        bestPos.blockPos = pos.method_10062();
                        bestPos.damage = targetDamage;
                        bestPos.isSlowPlace = isSlowPlace;
                        set = true;
                     }
                  }
               }
            }
         }
      }

      if (!set) {
         Iterator var19 = this.mc.field_1687.method_18112().iterator();

         label99:
         while(true) {
            class_2338 itemPos;
            class_2338 placePos;
            class_2680 placeState;
            do {
               class_1297 entity;
               do {
                  do {
                     if (!var19.hasNext()) {
                        break label99;
                     }

                     entity = (class_1297)var19.next();
                  } while(!(entity instanceof class_1542));
               } while(!(entity.method_5707(target.method_19538()) < 9.0D));

               itemPos = class_2338.method_49638(entity.method_19538());
               placePos = itemPos.method_10084();
               placeState = this.mc.field_1687.method_8320(placePos);
            } while(!placeState.method_26215() && (!(Boolean)this.ignoreTrapdoors.get() || !TRAPDOOR_BLOCKS.contains(placeState.method_26204())));

            if (this.mc.field_1687.method_8320(itemPos).method_26204() == class_2246.field_10540) {
               double targetDamage = DamageUtils.newCrystalDamage(target, target.method_5829(), new class_243((double)placePos.method_10263() + 0.5D, (double)placePos.method_10264(), (double)placePos.method_10260() + 0.5D), this._calcIgnoreSet);
               if (targetDamage >= (Double)this.minPlace.get() && targetDamage > bestPos.damage) {
                  bestPos.blockPos = placePos.method_10062();
                  bestPos.damage = targetDamage;
                  bestPos.isSlowPlace = false;
                  set = true;
               }
            }
         }
      }

      if (set) {
         return bestPos;
      } else {
         return null;
      }
   }

   private void cachedValidPlaceSpots() {
      int r = (int)Math.floor((Double)this.placeRange.get());
      class_2338 eyePos = class_2338.method_49638(this.mc.field_1724.method_33571());
      class_238 box = new class_238(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
      this._calcIgnoreSet.clear();
      SilentMine silentMine;
      if ((Boolean)this.antiSurroundPlace.get()) {
         silentMine = (SilentMine)Modules.get().get(SilentMine.class);
         if (silentMine.isActive()) {
            if (silentMine.getDelayedDestroyBlockPos() != null) {
               this._calcIgnoreSet.add(silentMine.getDelayedDestroyBlockPos());
            }

            if (silentMine.getRebreakBlockPos() != null) {
               this._calcIgnoreSet.add(silentMine.getRebreakBlockPos());
            }
         }
      }

      this.cachedValidSpots.clear();
      this.cachedValidSpots.addAll(Collections.nCopies((2 * r + 1) * (2 * r + 1) * (2 * r + 1), false));
      silentMine = (SilentMine)Modules.get().get(SilentMine.class);
      class_2338 rebreakBlockPos = silentMine != null && silentMine.isActive() ? silentMine.getRebreakBlockPos() : null;

      for(int x = -r; x <= r; ++x) {
         for(int y = -r; y <= r; ++y) {
            for(int z = -r; z <= r; ++z) {
               class_2338 pos = this.mutablePos.method_10103(eyePos.method_10263() + x, eyePos.method_10264() + y, eyePos.method_10260() + z);
               class_2338 downPos = pos.method_10074();
               class_2680 posState = this.mc.field_1687.method_8320(pos);
               boolean isRebreakBlock = rebreakBlockPos != null && rebreakBlockPos.equals(downPos);
               if ((this.mc.field_1687.method_22347(pos) || isRebreakBlock || (Boolean)this.ignoreTrapdoors.get() && TRAPDOOR_BLOCKS.contains(posState.method_26204())) && this.inPlaceRange(downPos)) {
                  class_2680 downState = this.mc.field_1687.method_8320(downPos);
                  class_2248 downBlock = downState.method_26204();
                  if (downBlock == class_2246.field_10540 || downBlock == class_2246.field_9987) {
                     ((IBox)box).set((double)downPos.method_10263(), (double)(downPos.method_10264() + 1), (double)downPos.method_10260(), (double)(downPos.method_10263() + 1), (double)(downPos.method_10264() + 3), (double)(downPos.method_10260() + 1));
                     if (!this.intersectsWithEntities(box)) {
                        double selfDamage = DamageUtils.newCrystalDamage(this.mc.field_1724, this.mc.field_1724.method_5829(), new class_243((double)downPos.method_10263() + 0.5D, (double)(downPos.method_10264() + 1), (double)downPos.method_10260() + 0.5D), this._calcIgnoreSet);
                        if (selfDamage <= (Double)this.maxPlace.get()) {
                           this.cachedValidSpots.set((x + r) * 2 * r * 2 * r + (y + r) * 2 * r + z + r, true);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void preplaceCrystal(class_2338 crystalBlockPos, boolean snapAt) {
      if (!(Boolean)this.pauseEatPlace.get() || !this.mc.field_1724.method_6115()) {
         class_1657 target = this.getNearestEnemyPlayer();
         if (target != null && !target.method_29504() && this.inPlaceRange(class_2338.method_49638(target.method_19538()))) {
            if (this.isPlayerPhased(target)) {
               this.lastChosenPos = null;
               this.lastFurtherPos = null;
               this.placeAttempts = 0;
            } else {
               class_2338 enemyPos = class_2338.method_49638(target.method_19538());
               System.out.println("Target enemyPos: " + String.valueOf(enemyPos));
               class_2338[] ignorePositions = new class_2338[]{class_2338.method_49638(this.mc.field_1724.method_19538()), class_2338.method_49638(this.mc.field_1724.method_19538()).method_10074(), class_2338.method_49638(this.mc.field_1724.method_19538()).method_10087(2)};
               class_2338[] adjacentPositions = new class_2338[]{enemyPos.method_10095(), enemyPos.method_10072(), enemyPos.method_10078(), enemyPos.method_10067()};
               class_2338[] furtherPositions = new class_2338[]{enemyPos.method_10076(2).method_10074(), enemyPos.method_10077(2).method_10074(), enemyPos.method_10089(2).method_10074(), enemyPos.method_10088(2).method_10074()};
               SilentMine silentMine = (SilentMine)Modules.get().get(SilentMine.class);
               class_2338 rebreakPos = silentMine != null && silentMine.isActive() ? silentMine.getRebreakBlockPos() : null;
               System.out.println("rebreakPos: " + String.valueOf(rebreakPos));
               if (crystalBlockPos != null) {
                  if (this.tryPlaceCrystalAtPos(crystalBlockPos.method_10074(), snapAt)) {
                     this.lastChosenPos = crystalBlockPos.method_10074();
                     this.lastFurtherPos = null;
                     this.placeAttempts = 0;
                  } else {
                     ++this.placeAttempts;
                     if (this.placeAttempts >= 1) {
                        this.placeCrystal(crystalBlockPos.method_10074());
                        this.placeAttempts = 0;
                        this.lastChosenPos = crystalBlockPos.method_10074();
                        this.lastFurtherPos = null;
                     }
                  }

               } else {
                  int i;
                  PrintStream var10000;
                  String var10001;
                  if (this.lastFurtherPos != null) {
                     i = -1;

                     for(int i = 0; i < furtherPositions.length; ++i) {
                        if (furtherPositions[i].equals(this.lastFurtherPos)) {
                           i = i;
                           break;
                        }
                     }

                     if (i != -1 && this.isValidPosition(this.lastFurtherPos) && (this.mc.field_1687.method_22347(this.lastFurtherPos.method_10084()) || (Boolean)this.ignoreTrapdoors.get() && TRAPDOOR_BLOCKS.contains(this.mc.field_1687.method_8320(this.lastFurtherPos.method_10084()).method_26204())) && rebreakPos != null && rebreakPos.equals(adjacentPositions[i])) {
                        if (this.placeSpeedCheck(false)) {
                           if (this.tryPlaceCrystalAtPos(this.lastFurtherPos, snapAt)) {
                              this.lastChosenPos = this.lastFurtherPos;
                              this.placeAttempts = 0;
                              System.out.println("Placed at further: " + String.valueOf(this.lastFurtherPos));
                           } else {
                              ++this.placeAttempts;
                              if (this.placeAttempts >= 1) {
                                 this.placeCrystal(this.lastFurtherPos);
                                 this.placeAttempts = 0;
                                 this.lastChosenPos = this.lastFurtherPos;
                                 System.out.println("Forced place at further: " + String.valueOf(this.lastFurtherPos));
                              }
                           }
                        }

                        return;
                     }

                     var10000 = System.out;
                     var10001 = String.valueOf(this.lastFurtherPos);
                     var10000.println("Cleared lastFurtherPos: " + var10001 + ", valid: " + this.isValidPosition(this.lastFurtherPos) + ", air: " + this.mc.field_1687.method_22347(this.lastFurtherPos.method_10084()) + ", rebreak match: " + (rebreakPos != null && Arrays.stream(adjacentPositions).anyMatch((posx) -> {
                        return posx.equals(rebreakPos);
                     })));
                     this.lastFurtherPos = null;
                  }

                  class_2338 furtherPos;
                  if (rebreakPos != null) {
                     for(i = 0; i < furtherPositions.length; ++i) {
                        furtherPos = furtherPositions[i];
                        class_2338 adjacentPos = adjacentPositions[i];
                        class_2680 aboveState = this.mc.field_1687.method_8320(furtherPos.method_10084());
                        boolean airAbove = this.mc.field_1687.method_22347(furtherPos.method_10084()) || (Boolean)this.ignoreTrapdoors.get() && TRAPDOOR_BLOCKS.contains(aboveState.method_26204());
                        boolean validPos = this.isValidPosition(furtherPos);
                        boolean rebreakMatch = rebreakPos.equals(adjacentPos);
                        var10000 = System.out;
                        var10001 = String.valueOf(furtherPos);
                        var10000.println("Checking furtherPos: " + var10001 + ", adjacent: " + String.valueOf(adjacentPos) + ", airAbove: " + airAbove + ", valid: " + validPos + ", rebreakMatch: " + rebreakMatch);
                        if (airAbove && validPos && rebreakMatch) {
                           this.lastFurtherPos = furtherPos;
                           if (this.placeSpeedCheck(false)) {
                              if (this.tryPlaceCrystalAtPos(furtherPos, snapAt)) {
                                 this.lastChosenPos = furtherPos;
                                 this.placeAttempts = 0;
                                 System.out.println("Placed at new further: " + String.valueOf(furtherPos));
                              } else {
                                 ++this.placeAttempts;
                                 if (this.placeAttempts >= 1) {
                                    this.placeCrystal(furtherPos);
                                    this.placeAttempts = 0;
                                    this.lastChosenPos = furtherPos;
                                    System.out.println("Forced place at new further: " + String.valueOf(furtherPos));
                                 }
                              }
                           }

                           return;
                        }
                     }
                  }

                  class_2338[] fallbackAdjacentPositions = new class_2338[]{enemyPos.method_10095().method_10074(), enemyPos.method_10072().method_10074(), enemyPos.method_10078().method_10074(), enemyPos.method_10067().method_10074()};
                  furtherPos = null;
                  class_2338[] var19 = fallbackAdjacentPositions;
                  int var20 = fallbackAdjacentPositions.length;

                  for(int var21 = 0; var21 < var20; ++var21) {
                     class_2338 pos = var19[var21];
                     if (this.isValidPosition(pos) && !Arrays.asList(ignorePositions).contains(pos)) {
                        furtherPos = pos;
                        break;
                     }
                  }

                  if (furtherPos != null) {
                     this.lastFurtherPos = null;
                     this.lastChosenPos = furtherPos;
                     if (this.tryPlaceCrystalAtPos(furtherPos, snapAt)) {
                        this.placeAttempts = 0;
                        System.out.println("Placed at adjacent: " + String.valueOf(furtherPos));
                     } else {
                        ++this.placeAttempts;
                        if (this.placeAttempts >= 1) {
                           this.placeCrystal(furtherPos);
                           this.placeAttempts = 0;
                           System.out.println("Forced place at adjacent: " + String.valueOf(furtherPos));
                        }
                     }
                  }

               }
            }
         } else {
            this.lastChosenPos = null;
            this.lastFurtherPos = null;
            this.placeAttempts = 0;
         }
      }
   }

   private boolean isValidPosition(class_2338 pos) {
      if (!this.inPlaceRange(pos)) {
         return false;
      } else {
         class_2680 downState = this.mc.field_1687.method_8320(pos);
         class_2248 downBlock = downState.method_26204();
         if (downBlock != class_2246.field_10540 && downBlock != class_2246.field_9987) {
            return false;
         } else {
            class_2680 aboveState = this.mc.field_1687.method_8320(pos.method_10084());
            if (!this.mc.field_1687.method_22347(pos.method_10084()) && (!(Boolean)this.ignoreTrapdoors.get() || !TRAPDOOR_BLOCKS.contains(aboveState.method_26204()))) {
               return false;
            } else {
               class_238 box = new class_238((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 3), (double)(pos.method_10260() + 1));
               return !this.intersectsWithEntities(box);
            }
         }
      }
   }

   private boolean tryPlaceCrystalAtPos(class_2338 pos, boolean snapAt) {
      if (!this.inPlaceRange(pos)) {
         return false;
      } else {
         class_2680 downState = this.mc.field_1687.method_8320(pos);
         class_2248 downBlock = downState.method_26204();
         if (downBlock != class_2246.field_10540 && downBlock != class_2246.field_9987) {
            return false;
         } else {
            class_2680 aboveState = this.mc.field_1687.method_8320(pos.method_10084());
            if (this.mc.field_1687.method_22347(pos.method_10084()) || (Boolean)this.ignoreTrapdoors.get() && TRAPDOOR_BLOCKS.contains(aboveState.method_26204())) {
               class_238 box = new class_238((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 3), (double)(pos.method_10260() + 1));
               if (this.intersectsWithEntities(box)) {
                  return false;
               } else {
                  if ((Boolean)this.rotatePlace.get() && snapAt && !MeteorClient.ROTATION.lookingAt(new class_238(pos))) {
                     MeteorClient.ROTATION.snapAt(pos.method_46558());
                  }

                  return this.placeCrystal(pos);
               }
            } else {
               return false;
            }
         }
      }
   }

   private class_1657 getNearestEnemyPlayer() {
      class_1657 nearest = null;
      double nearestDistance = Double.MAX_VALUE;
      Iterator var4 = this.mc.field_1687.method_18456().iterator();

      while(var4.hasNext()) {
         class_1657 player = (class_1657)var4.next();
         if (player != this.mc.field_1724 && !Friends.get().isFriend(player) && !player.method_29504()) {
            double distance = player.method_5707(this.mc.field_1724.method_33571());
            if (distance < nearestDistance) {
               nearest = player;
               nearestDistance = distance;
            }
         }
      }

      return nearest;
   }

   public boolean inPlaceRange(class_2338 blockPos) {
      class_243 from = this.mc.field_1724.method_33571();
      return blockPos.method_46558().method_1022(from) <= (Double)this.placeRange.get();
   }

   public boolean inBreakRange(class_243 pos) {
      class_243 from = this.mc.field_1724.method_33571();
      return pos.method_1022(from) <= (Double)this.breakRange.get();
   }

   public boolean shouldBreakCrystal(class_1297 entity) {
      boolean damageCheck = false;
      double selfDamage = DamageUtils.newCrystalDamage(this.mc.field_1724, this.mc.field_1724.method_5829(), entity.method_19538(), (Set)null);
      if (selfDamage > (Double)this.maxBreak.get()) {
         return false;
      } else {
         class_2338 crystalPos = class_2338.method_49638(entity.method_19538());
         class_2338 floorPos = crystalPos.method_10074();
         Iterator var7 = this.mc.field_1687.method_18456().iterator();

         while(var7.hasNext()) {
            class_1657 player = (class_1657)var7.next();
            if (player != this.mc.field_1724 && !player.method_29504() && !Friends.get().isFriend(player)) {
               class_2338 playerFloorPos = class_2338.method_49638(player.method_19538()).method_10074();
               if (playerFloorPos.equals(floorPos)) {
                  return true;
               }

               if ((Boolean)this.ignoreCalc.get()) {
                  damageCheck = true;
                  break;
               }

               double targetDamage = DamageUtils.newCrystalDamage(player, player.method_5829(), entity.method_19538(), (Set)null);
               if (targetDamage >= (Double)this.minBreak.get()) {
                  damageCheck = true;
                  break;
               }
            }
         }

         return damageCheck;
      }
   }

   @EventHandler(
      priority = 200
   )
   private void onEntity(EntityAddedEvent event) {
      class_1297 entity = event.entity;
      if (entity instanceof class_1511) {
         class_2338 blockPos = class_2338.method_49638(entity.method_19538()).method_10074();
         if (this.crystalPlaceDelays.containsKey(blockPos)) {
            this.crystalPlaceDelays.remove(blockPos);
         }

         if ((Boolean)this.breakCrystals.get() && (Boolean)this.packetBreak.get()) {
            if (!(entity instanceof class_1511)) {
               return;
            }

            if (!this.inBreakRange(entity.method_19538())) {
               return;
            }

            if (!this.shouldBreakCrystal(entity)) {
               return;
            }

            if (!this.breakSpeedCheck()) {
               return;
            }

            this.breakCrystal(entity);
         }

      }
   }

   @EventHandler(
      priority = 201
   )
   private void onRender3D(Render3DEvent event) {
      if (this.isActive()) {
         this.update();
         this.renderer.onRender3D(event);
      }
   }

   @EventHandler
   private void onPlayerDeath(PlayerDeathEvent.Death event) {
      if (event.getPlayer() != null && event.getPlayer() != this.mc.field_1724) {
         if ((Boolean)this.setPlayerDead.get()) {
            event.getPlayer().method_6033(0.0F);
         }

      }
   }

   private boolean intersectsWithEntities(class_238 box) {
      return EntityUtils.intersectsWithEntity(box, (entity) -> {
         if (entity.method_7325()) {
            return false;
         } else if (this.brokenCrystals.contains(entity.method_5628())) {
            return false;
         } else {
            if ((Boolean)this.ignoreItems.get() && entity instanceof class_1542) {
               class_1542 item = (class_1542)entity;
               if (item.field_6012 < 10) {
                  return false;
               }
            }

            return true;
         }
      });
   }

   private boolean breakSpeedCheck() {
      long currentTime = System.currentTimeMillis();
      return (Double)this.breakSpeedLimit.get() == 0.0D || (double)(currentTime - this.lastBreakTimeMS) / 1000.0D > 1.0D / (Double)this.breakSpeedLimit.get();
   }

   private boolean placeSpeedCheck(boolean slowPlace) {
      long currentTime = System.currentTimeMillis();
      double placeSpeed = slowPlace ? (Double)this.slowPlaceSpeed.get() : (Double)this.placeSpeedLimit.get();
      return placeSpeed == 0.0D || (double)(currentTime - this.lastPlaceTimeMS) / 1000.0D > 1.0D / placeSpeed;
   }

   public String getInfoString() {
      long currentTime = System.currentTimeMillis();
      return String.format("%d", this.crystalBreakDelays.values().stream().filter((x) -> {
         return currentTime - x <= 1000L;
      }).count());
   }

   public static enum SwingMode {
      Packet,
      Client,
      None;

      // $FF: synthetic method
      private static AutoCrystal.SwingMode[] $values() {
         return new AutoCrystal.SwingMode[]{Packet, Client, None};
      }
   }

   private class PlacePosition {
      public class_2338 blockPos;
      public double damage = 0.0D;
      public boolean isSlowPlace = false;

      private PlacePosition(final AutoCrystal param1) {
      }
   }
}
