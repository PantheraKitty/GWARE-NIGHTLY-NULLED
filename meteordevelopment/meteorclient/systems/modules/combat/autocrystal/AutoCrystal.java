package meteordevelopment.meteorclient.systems.modules.combat.autocrystal;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
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
   private final SettingGroup sgPlace;
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
   private final Setting<Double> placeSpeedLimit;
   private final Setting<Double> minPlace;
   private final Setting<Double> maxPlace;
   private final Setting<Boolean> antiSurroundPlace;
   private final Setting<Double> placeDelay;
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
   public final List<class_1297> forceBreakCrystals;
   private final Pool<AutoCrystal.PlacePosition> placePositionPool;
   private final List<AutoCrystal.PlacePosition> _placePositions;
   private final class_2339 mutablePos;
   private final IntSet explodedCrystals;
   private final Map<Integer, Long> crystalBreakDelays;
   private final Map<class_2338, Long> crystalPlaceDelays;
   public final List<Boolean> cachedValidSpots;
   private long lastPlaceTimeMS;
   private long lastBreakTimeMS;
   private AutoMine autoMine;
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
      this.placeSpeedLimit = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-speed-limit")).description("Maximum number of crystals to place every second.")).defaultValue(40.0D).min(0.0D).sliderRange(0.0D, 40.0D).build());
      this.minPlace = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-place")).description("Minimum enemy damage to place.")).defaultValue(8.0D).min(0.0D).sliderRange(0.0D, 20.0D).build());
      this.maxPlace = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-place")).description("Max self damage to place.")).defaultValue(20.0D).min(0.0D).sliderRange(0.0D, 20.0D).build());
      this.antiSurroundPlace = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-surround")).description("Ignores auto-mine blocks from calculations to place outside of their surround.")).defaultValue(true)).build());
      this.placeDelay = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-delay")).description("The number of seconds to wait to retry placing a crystal at a position.")).defaultValue(0.05D).min(0.0D).sliderMax(0.6D).build());
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
      this.explodedCrystals = new IntOpenHashSet();
      this.crystalBreakDelays = new HashMap();
      this.crystalPlaceDelays = new HashMap();
      this.cachedValidSpots = new ArrayList();
      this.lastPlaceTimeMS = 0L;
      this.lastBreakTimeMS = 0L;
      this._calcIgnoreSet = new HashSet();
   }

   public void onActivate() {
      if (this.autoMine == null) {
         this.autoMine = (AutoMine)Modules.get().get(AutoMine.class);
      }

      this.explodedCrystals.clear();
      this.crystalBreakDelays.clear();
      this.crystalPlaceDelays.clear();
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
         Iterator var6;
         if ((Boolean)this.placeCrystals.get() && (!(Boolean)this.pauseEatPlace.get() || !this.mc.field_1724.method_6115())) {
            this.cachedValidPlaceSpots();
            var6 = this.mc.field_1687.method_18456().iterator();

            label125:
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
                                    if (!var6.hasNext()) {
                                       long currentTime = System.currentTimeMillis();
                                       if (bestPlacePos != null && this.placeSpeedCheck(bestPlacePos.isSlowPlace) && this.placeCrystal(bestPlacePos.blockPos.method_10074())) {
                                          this.lastPlaceTimeMS = currentTime;
                                       }
                                       break label125;
                                    }

                                    player = (class_1657)var6.next();
                                 } while(player == this.mc.field_1724);
                              } while(Friends.get().isFriend(player));
                           } while(player.method_29504());
                        } while((Boolean)this.ignoreNakeds.get() && ((class_1799)player.method_31548().field_7548.get(0)).method_7960() && ((class_1799)player.method_31548().field_7548.get(1)).method_7960() && ((class_1799)player.method_31548().field_7548.get(2)).method_7960() && ((class_1799)player.method_31548().field_7548.get(3)).method_7960());
                     } while(player.method_5707(this.mc.field_1724.method_33571()) > 144.0D);

                     testPos = this.findBestPlacePosition(player);
                  } while(testPos == null);
               } while(bestPlacePos != null && !(testPos.damage > bestPlacePos.damage));

               bestPlacePos = testPos;
            }
         }

         if ((Boolean)this.breakCrystals.get() && (!(Boolean)this.pauseEatBreak.get() || !this.mc.field_1724.method_6115())) {
            var6 = this.mc.field_1687.method_18112().iterator();

            while(var6.hasNext()) {
               class_1297 entity = (class_1297)var6.next();
               if (entity instanceof class_1511 && this.inBreakRange(entity.method_19538()) && this.shouldBreakCrystal(entity) && (!this.breakSpeedCheck() || !this.breakCrystal(entity) && (Boolean)this.rotateBreak.get() && !MeteorClient.ROTATION.lookingAt(entity.method_5829()))) {
                  break;
               }
            }
         }

      }
   }

   public boolean placeCrystal(class_2338 blockPos) {
      if (blockPos != null && this.mc.field_1724 != null) {
         class_2338 crystaBlockPos = blockPos.method_10084();
         class_238 box = new class_238((double)crystaBlockPos.method_10263(), (double)crystaBlockPos.method_10264(), (double)crystaBlockPos.method_10260(), (double)(crystaBlockPos.method_10263() + 1), (double)(crystaBlockPos.method_10264() + 2), (double)(crystaBlockPos.method_10260() + 1));
         if (this.intersectsWithEntities(box)) {
            return false;
         } else {
            FindItemResult result = InvUtils.find(class_1802.field_8301);
            if (!result.found()) {
               return false;
            } else {
               if ((Boolean)this.rotatePlace.get()) {
                  MeteorClient.ROTATION.requestRotation(blockPos.method_46558(), 10.0D);
                  if (!MeteorClient.ROTATION.lookingAt(new class_238(blockPos))) {
                     return false;
                  }
               }

               long currentTime = System.currentTimeMillis();
               if (this.crystalPlaceDelays.containsKey(blockPos) && (double)(currentTime - (Long)this.crystalPlaceDelays.get(blockPos)) / 1000.0D < (Double)this.placeDelay.get()) {
                  return false;
               } else if (!MeteorClient.SWAP.beginSwap(result, true)) {
                  return false;
               } else {
                  this.crystalPlaceDelays.put(blockPos, currentTime);
                  this.renderer.onPlaceCrystal(blockPos);
                  class_3965 calculatedHitResult = AutoCrystalUtil.getPlaceBlockHitResult(blockPos);
                  class_1268 hand = class_1268.field_5808;
                  int s = this.mc.field_1687.method_41925().method_41937().method_41942();
                  this.mc.field_1724.field_3944.method_52787(new class_2885(hand, calculatedHitResult, s));
                  if (this.placeSwingMode.get() == AutoCrystal.SwingMode.Client) {
                     this.mc.field_1724.method_6104(hand);
                  }

                  if (this.placeSwingMode.get() == AutoCrystal.SwingMode.Packet) {
                     this.mc.method_1562().method_52787(new class_2879(hand));
                  }

                  MeteorClient.SWAP.endSwap(true);
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean breakCrystal(class_1297 entity) {
      if (this.mc.field_1724 == null) {
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
            this.renderer.onBreakCrystal(entity);
            class_2824 packet = class_2824.method_34206(entity, this.mc.field_1724.method_5715());
            this.mc.method_1562().method_52787(packet);
            if (this.breakSwingMode.get() == AutoCrystal.SwingMode.Client) {
               this.mc.field_1724.method_6104(class_1268.field_5808);
            }

            if (this.breakSwingMode.get() == AutoCrystal.SwingMode.Packet) {
               this.mc.method_1562().method_52787(new class_2879(class_1268.field_5808));
            }

            this.explodedCrystals.add(entity.method_5628());
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

      if (set) {
         return bestPos;
      } else {
         return null;
      }
   }

   private void cachedValidPlaceSpots() {
      int r = (int)Math.floor((Double)this.placeRange.get());
      class_2338 eyePos = class_2338.method_49638(this.mc.field_1724.method_33571());
      int ex = eyePos.method_10263();
      int ey = eyePos.method_10264();
      int ez = eyePos.method_10260();
      class_238 box = new class_238(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
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

      this.cachedValidSpots.clear();

      while(this.cachedValidSpots.size() < (2 * r + 1) * (2 * r + 1) * (2 * r + 1)) {
         this.cachedValidSpots.add(false);
      }

      for(int x = -r; x <= r; ++x) {
         for(int y = -r; y <= r; ++y) {
            for(int z = -r; z <= r; ++z) {
               if (this.mc.field_1687.method_22347(this.mutablePos.method_10103(ex + x, ey + y, ez + z))) {
                  class_2338 downPos = this.mutablePos.method_10103(ex + x, ey + y - 1, ez + z);
                  class_2680 downState = this.mc.field_1687.method_8320(downPos);
                  class_2248 downBlock = downState.method_26204();
                  if (!downState.method_26215() && (downBlock == class_2246.field_10540 || downBlock == class_2246.field_9987) && this.inPlaceRange(downPos)) {
                     ((IBox)box).set((double)downPos.method_10263(), (double)(downPos.method_10264() + 1), (double)downPos.method_10260(), (double)(downPos.method_10263() + 1), (double)(downPos.method_10264() + 3), (double)(downPos.method_10260() + 1));
                     if (!this.intersectsWithEntities(box)) {
                        double selfDamage = DamageUtils.newCrystalDamage(this.mc.field_1724, this.mc.field_1724.method_5829(), new class_243((double)downPos.method_10263() + 0.5D, (double)(downPos.method_10264() + 1), (double)downPos.method_10260() + 0.5D), this._calcIgnoreSet);
                        if (!(selfDamage > (Double)this.maxPlace.get())) {
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
      class_2338 blockPos = crystalBlockPos.method_10074();
      this.crystalPlaceDelays.remove(blockPos);
      class_238 box = new class_238((double)crystalBlockPos.method_10263(), (double)crystalBlockPos.method_10264(), (double)crystalBlockPos.method_10260(), (double)(crystalBlockPos.method_10263() + 1), (double)(crystalBlockPos.method_10264() + 2), (double)(crystalBlockPos.method_10260() + 1));
      if (!this.intersectsWithEntities(box)) {
         if ((Boolean)this.rotatePlace.get() && snapAt && !MeteorClient.ROTATION.lookingAt(new class_238(blockPos))) {
            MeteorClient.ROTATION.snapAt(blockPos.method_46558());
         }

         this.placeCrystal(blockPos);
      }
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
         Iterator var5 = this.mc.field_1687.method_18456().iterator();

         while(var5.hasNext()) {
            class_1657 player = (class_1657)var5.next();
            if (player != this.mc.field_1724 && !player.method_29504() && !Friends.get().isFriend(player) && !(player.method_5707(this.mc.field_1724.method_33571()) > 196.0D)) {
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
         class_2338 blockPos = entity.method_24515().method_10074();
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
         return !entity.method_7325() && !this.explodedCrystals.contains(entity.method_5628());
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
