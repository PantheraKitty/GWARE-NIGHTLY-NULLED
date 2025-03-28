package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1533;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2399;
import net.minecraft.class_243;
import net.minecraft.class_2533;
import net.minecraft.class_2541;
import net.minecraft.class_2680;
import net.minecraft.class_2746;
import net.minecraft.class_2885;
import net.minecraft.class_3965;
import net.minecraft.class_5575;

public class ChineseAura extends Module {
   private final SettingGroup sgGeneral;
   private final Set<class_1792> allowedFeetItems;
   private final Set<class_1792> allowedHeadItems;
   private final Setting<Boolean> pauseEat;
   private final Setting<Double> range;
   private final Setting<SortPriority> priority;
   private final Setting<List<class_1792>> feetItems;
   private final Setting<List<class_1792>> headItems;
   private final Setting<Double> placeDelay;
   private final Setting<Boolean> ignoreNakeds;
   private class_1657 targetPlayer;
   private final Map<class_2338, Long> timeOfLastPlace;
   private int tickCounter;

   public ChineseAura() {
      super(Categories.Combat, "chinese-aura", "Places whatever you want on your enemies. Extremely chinese.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.allowedFeetItems = new HashSet<class_1792>() {
         {
            this.add(class_1802.field_8121);
            this.add(class_1802.field_17523);
            this.add(class_1802.field_8143);
            this.add(class_1802.field_8786);
            this.add(class_1802.field_16482);
         }
      };
      this.allowedHeadItems = new HashSet<class_1792>() {
         {
            this.add(class_1802.field_8121);
            this.add(class_1802.field_17523);
            this.add(class_1802.field_8786);
            this.add(class_1802.field_8376);
            this.add(class_1802.field_8495);
            this.add(class_1802.field_8774);
            this.add(class_1802.field_8321);
            this.add(class_1802.field_8190);
            this.add(class_1802.field_8844);
            this.add(class_1802.field_37529);
            this.add(class_1802.field_42702);
            this.add(class_1802.field_40226);
            this.add(class_1802.field_22002);
            this.add(class_1802.field_22003);
         }
      };
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-eat")).description("Pauses while eating.")).defaultValue(true)).build());
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The maximum distance to target players.")).defaultValue(5.0D).range(0.0D, 5.0D).sliderMax(5.0D).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to filter targets within range.")).defaultValue(SortPriority.ClosestAngle)).build());
      this.feetItems = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("feet-items")).description("Items to place on enemies feet")).filter((x) -> {
         return this.allowedFeetItems.contains(x);
      }).build());
      this.headItems = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("head-items")).description("Items to place on enemies heads")).filter((x) -> {
         return this.allowedHeadItems.contains(x);
      }).build());
      this.placeDelay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-delay")).description("How many seconds to wait between placing stuff again")).defaultValue(0.2D).min(0.0D).sliderMax(2.0D).build());
      this.ignoreNakeds = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-nakeds")).description("Ignores players with no armor.")).defaultValue(true)).build());
      this.targetPlayer = null;
      this.timeOfLastPlace = new HashMap();
      this.tickCounter = 0;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (TargetUtils.isBadTarget(this.targetPlayer, (Double)this.range.get())) {
         this.targetPlayer = TargetUtils.getPlayerTarget((Double)this.range.get(), (SortPriority)this.priority.get());
         if (TargetUtils.isBadTarget(this.targetPlayer, (Double)this.range.get())) {
            return;
         }
      }

      if (!(Boolean)this.ignoreNakeds.get() || !this.isNaked(this.targetPlayer)) {
         if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
            ++this.tickCounter;
            FindItemResult headItemResult = InvUtils.find((x) -> {
               return ((List)this.headItems.get()).contains(x.method_7909());
            });
            FindItemResult feetItemResult = InvUtils.find((x) -> {
               return ((List)this.feetItems.get()).contains(x.method_7909());
            });
            long currentTime = System.currentTimeMillis();
            class_1792 item;
            if (headItemResult.found()) {
               item = this.mc.field_1724.method_31548().method_5438(headItemResult.slot()).method_7909();
               class_2338 upperPos = this.targetPlayer.method_24515().method_10084();
               class_2338 lowerPos = this.targetPlayer.method_24515();
               boolean didPlace = false;
               if (MeteorClient.SWAP.beginSwap(headItemResult, true)) {
                  if (item == class_1802.field_17523) {
                     this.placeVine(headItemResult, lowerPos);
                     didPlace = true;
                  } else if (item == class_1802.field_8121) {
                     this.placeLadder(headItemResult, lowerPos);
                     didPlace = true;
                  } else if (item == class_1802.field_8786) {
                     this.placeWeb(headItemResult, lowerPos);
                     didPlace = true;
                  } else if (this.isTrapdoor(item)) {
                     didPlace = this.placeTrapdoor(headItemResult, upperPos, lowerPos, currentTime);
                  }

                  MeteorClient.SWAP.endSwap(true);
               }

               if (didPlace) {
                  this.timeOfLastPlace.put(lowerPos, currentTime);
               }
            }

            if (feetItemResult.found()) {
               item = this.mc.field_1724.method_31548().method_5438(feetItemResult.slot()).method_7909();
               boolean cooldownCheck = !this.timeOfLastPlace.containsKey(this.targetPlayer.method_24515()) || ((double)currentTime - (double)(Long)this.timeOfLastPlace.get(this.targetPlayer.method_24515())) / 1000.0D > (Double)this.placeDelay.get();
               if (cooldownCheck) {
                  boolean didPlace = true;
                  if (MeteorClient.SWAP.beginSwap(feetItemResult, true)) {
                     if (item == class_1802.field_8143) {
                        this.placeItemFrame(feetItemResult);
                     } else if (item == class_1802.field_17523) {
                        this.placeVine(feetItemResult, this.targetPlayer.method_24515());
                     } else if (item == class_1802.field_8121) {
                        this.placeLadder(feetItemResult, this.targetPlayer.method_24515());
                     } else if (item == class_1802.field_8786) {
                        this.placeWeb(feetItemResult, this.targetPlayer.method_24515());
                     } else if (item == class_1802.field_16482) {
                        this.placeScaffold(feetItemResult, this.targetPlayer.method_24515());
                     } else {
                        didPlace = false;
                     }

                     MeteorClient.SWAP.endSwap(true);
                  }

                  if (didPlace) {
                     this.timeOfLastPlace.put(this.targetPlayer.method_24515(), currentTime);
                  }
               }
            }

         }
      }
   }

   private boolean isNaked(class_1657 player) {
      return ((class_1799)player.method_31548().field_7548.get(0)).method_7960() && ((class_1799)player.method_31548().field_7548.get(1)).method_7960() && ((class_1799)player.method_31548().field_7548.get(2)).method_7960() && ((class_1799)player.method_31548().field_7548.get(3)).method_7960();
   }

   private void placeItemFrame(FindItemResult itemResult) {
      if (this.mc.field_1687.method_22347(this.targetPlayer.method_24515())) {
         class_2338 blockPos = this.targetPlayer.method_24515().method_10074();
         class_2350 dir = class_2350.field_11036;
         class_238 boundingBox = this.targetPlayer.method_5829().method_1002(0.01D, 0.1D, 0.01D);
         double feetY = this.targetPlayer.method_23318();
         class_238 feetBox = new class_238(boundingBox.field_1323, feetY, boundingBox.field_1321, boundingBox.field_1320, feetY + 0.1D, boundingBox.field_1324);
         boolean inMultipleBlocks = class_2338.method_29715(feetBox).count() > 1L;
         if (!inMultipleBlocks) {
            class_243 hitPos = blockPos.method_46558().method_1031((double)dir.method_10148() * 0.5D, (double)dir.method_10164() * 0.5D, (double)dir.method_10165() * 0.5D);
            List<class_1533> entities = this.mc.field_1687.method_18023(class_5575.method_31795(class_1533.class), class_238.method_30048(hitPos, 0.1D, 0.1D, 0.1D), (entity) -> {
               return true;
            });
            if (entities.isEmpty()) {
               this.mc.method_1562().method_52787(new class_2885(class_1268.field_5808, new class_3965(hitPos, dir, blockPos, false), this.mc.field_1687.method_41925().method_41937().method_41942()));
            }

         }
      }
   }

   private void placeVine(FindItemResult itemResult, class_2338 pos) {
      if (this.mc.field_1687.method_22347(pos)) {
         class_2350[] var3 = class_2350.field_11041;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            class_2350 dir = var3[var5];
            class_2338 supportPos = pos.method_10093(dir);
            dir = dir.method_10153();
            if (this.canPlaceVine(pos, dir)) {
               class_243 hitPos = supportPos.method_46558().method_1031((double)dir.method_10148() * 0.5D, 0.75D, (double)dir.method_10165() * 0.5D);
               this.mc.method_1562().method_52787(new class_2885(class_1268.field_5808, new class_3965(hitPos, dir, supportPos, false), this.mc.field_1687.method_41925().method_41937().method_41942()));
            }
         }

      }
   }

   private void placeLadder(FindItemResult itemResult, class_2338 pos) {
      if (this.mc.field_1687.method_22347(pos)) {
         class_2350[] var3 = class_2350.field_11041;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            class_2350 dir = var3[var5];
            class_2338 supportPos = pos.method_10093(dir);
            dir = dir.method_10153();
            if (this.canPlaceLadder(pos, dir)) {
               class_243 hitPos = supportPos.method_46558().method_1031((double)dir.method_10148() * 0.5D, 0.75D, (double)dir.method_10165() * 0.5D);
               this.mc.method_1562().method_52787(new class_2885(class_1268.field_5808, new class_3965(hitPos, dir, supportPos, false), this.mc.field_1687.method_41925().method_41937().method_41942()));
            }
         }

      }
   }

   private void placeWeb(FindItemResult itemResult, class_2338 pos) {
      List<class_2338> placePoses = new ArrayList();
      placePoses.add(pos);
      if (MeteorClient.BLOCK.beginPlacement(placePoses, class_1802.field_8786)) {
         placePoses.forEach((blockPos) -> {
            MeteorClient.BLOCK.placeBlock(class_1802.field_8786, blockPos);
         });
         MeteorClient.BLOCK.endPlacement();
      }
   }

   private void placeScaffold(FindItemResult itemResult, class_2338 pos) {
      List<class_2338> placePoses = new ArrayList();
      placePoses.add(pos);
      if (MeteorClient.BLOCK.beginPlacement(placePoses, class_1802.field_16482)) {
         placePoses.forEach((blockPos) -> {
            MeteorClient.BLOCK.placeBlock(class_1802.field_16482, blockPos);
         });
         MeteorClient.BLOCK.endPlacement();
      }
   }

   private boolean placeTrapdoor(FindItemResult itemResult, class_2338 upperPos, class_2338 lowerPos, long currentTime) {
      class_2680 upperState = this.mc.field_1687.method_8320(upperPos);
      class_2338 supportPos = upperPos.method_10084();
      boolean didPlace = false;
      if (!this.mc.field_1687.method_8320(supportPos).method_26212(this.mc.field_1687, supportPos)) {
         return false;
      } else {
         boolean canPlace = !this.timeOfLastPlace.containsKey(upperPos) || ((double)currentTime - (double)(Long)this.timeOfLastPlace.get(upperPos)) / 1000.0D > (Double)this.placeDelay.get();
         if (canPlace) {
            if (!(upperState.method_26204() instanceof class_2533)) {
               class_243 hitPos = supportPos.method_46558().method_1031(0.0D, -0.5D, 0.0D);
               this.mc.method_1562().method_52787(new class_2885(class_1268.field_5808, new class_3965(hitPos, class_2350.field_11033, supportPos, false), this.mc.field_1687.method_41925().method_41937().method_41942()));
               didPlace = true;
            } else if (this.isPlayerCrawling() != (Boolean)upperState.method_11654(class_2533.field_11631)) {
               this.mc.method_1562().method_52787(new class_2885(class_1268.field_5808, new class_3965(upperPos.method_46558(), class_2350.field_11036, upperPos, false), this.mc.field_1687.method_41925().method_41937().method_41942()));
               didPlace = true;
            }
         }

         return didPlace;
      }
   }

   private boolean isPlayerCrawling() {
      if (this.targetPlayer == null) {
         return false;
      } else {
         return this.targetPlayer.method_5829().method_17940() < 1.5D;
      }
   }

   private boolean isTrapdoor(class_1792 item) {
      return item == class_1802.field_8376 || item == class_1802.field_8495 || item == class_1802.field_8774 || item == class_1802.field_8321 || item == class_1802.field_8190 || item == class_1802.field_8844 || item == class_1802.field_37529 || item == class_1802.field_42702 || item == class_1802.field_40226 || item == class_1802.field_22002 || item == class_1802.field_22003;
   }

   private boolean canPlaceVine(class_2338 pos, class_2350 side) {
      class_2680 blockState = this.mc.field_1687.method_8320(pos);
      if (side != class_2350.field_11036 && side != class_2350.field_11033) {
         if (!class_2541.method_10821(this.mc.field_1687, pos.method_10093(side), side.method_10153())) {
            return false;
         } else if (blockState.method_27852(class_2246.field_10597)) {
            class_2746 sideProperty = class_2541.method_10828(side);
            return !(Boolean)blockState.method_11654(sideProperty);
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean canPlaceLadder(class_2338 pos, class_2350 side) {
      if (side != class_2350.field_11036 && side != class_2350.field_11033) {
         class_2680 blockState = this.mc.field_1687.method_8320(pos);
         class_2338 attachedPos = pos.method_10093(side.method_10153());
         class_2680 attachedState = this.mc.field_1687.method_8320(attachedPos);
         if (!class_2248.method_9501(attachedState.method_26220(this.mc.field_1687, attachedPos), side)) {
            return false;
         } else if (blockState.method_27852(class_2246.field_9983)) {
            class_2350 existingDirection = (class_2350)blockState.method_11654(class_2399.field_11253);
            return existingDirection != side;
         } else {
            return blockState.method_26215() || blockState.method_45474();
         }
      } else {
         return false;
      }
   }
}
